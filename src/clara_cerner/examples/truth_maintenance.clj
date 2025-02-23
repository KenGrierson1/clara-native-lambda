(ns clara-cerner.examples.truth-maintenance
  (:require [clara.rules.accumulators :as acc]
            [clara.rules :refer :all]))

(defrecord Temperature [temperature location])

(defrecord LocalTemperatureRecords [high low location])

(defrecord Cold [temperature])

(defrecord AlwaysOverZeroLocation [location])

(defrule insert-temperature-records
  [?min-temp <- (acc/min :temperature) :from [Temperature (= ?loc location)]]
  [?max-temp <- (acc/max :temperature) :from [Temperature (= ?loc location)]]
  =>
  (insert! (map->LocalTemperatureRecords {:high ?max-temp :low ?min-temp :location ?loc})))

;; When a Temperature fact is inserted or retracted, the output of insert-temperature-records will
;; be adjusted to compensate, and the output of this rule will in turn be adjusted to compensate for the
;; change in the LocalTemperatureRecords facts in the session.
(defrule always-over-zero
  [LocalTemperatureRecords (> low 0) (= ?loc location)]
  =>
  (insert! (->AlwaysOverZeroLocation ?loc)))

(defrule insert-cold-temperature
  [Temperature (= ?temperature temperature) (< temperature 30)]
  =>
  (insert! (->Cold ?temperature)))

(defquery cold-facts
  "Query for Cold facts"
  []
  [Cold (= ?temperature temperature)])

(defquery records-facts
  "Query for LocalTemperatureRecord facts"
  []
  [LocalTemperatureRecords (= ?high high) (= ?low low) (= ?loc location)])

(defquery always-over-zero-facts
  "Query for AlwaysOverZeroLocation facts"
  []
  [AlwaysOverZeroLocation (= ?loc location)])

(defsession compiled-session
            'clara-cerner.examples.truth-maintenance)

(defn run-examples []
  (let [initial-session (-> compiled-session
                            (insert (->Temperature -10 "MCI")
                                    (->Temperature 110 "MCI")
                                    (->Temperature 20 "LHR")
                                    (->Temperature 90 "LHR"))
                            fire-rules)

        ; Now add a temperature of -5 to LHR and a temperature of 115 to MCI
        with-mods-session (-> initial-session
                              (insert (->Temperature -5 "LHR")
                                      (->Temperature 115 "MCI"))
                              fire-rules)

        ; Now we retract the temperature of -5 at LHR
        with-retracted-session (-> with-mods-session
                                   (retract (->Temperature -5 "LHR"))
                                   fire-rules)
        ]

    {:initial-facts
     [
      {:description "Initial cold temperatures"
       :value       (query initial-session cold-facts)}
      {:description "Initial local temperature records"
       :value       (query initial-session records-facts)}
      {:description "Initial locations that have never been below 0"
       :value       (query initial-session always-over-zero-facts)}
      ]
     :add-facts
     [{:description "New cold temperatures"
       :value       (query with-mods-session cold-facts)}
      {:description "New local temperature records"
       :value       (query with-mods-session records-facts)}
      {:description "New locations that have never been below 0"
       :value       (query with-mods-session always-over-zero-facts)}
      ]
     :after-retraction
     [{:description "Cold temperatures after this retraction"
       :value       (query with-retracted-session cold-facts)}
      {:description "Local temperature records after this retraction"
       :value       (query with-retracted-session records-facts)}
      {:description "Locations that have never been below 0 after this retraction"
       :value       (query with-retracted-session always-over-zero-facts)}]
     }

      ))
