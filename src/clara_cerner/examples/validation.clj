(ns clara-cerner.examples.validation
  (:require [clara.rules :refer :all]
            [clj-time.core :as t]))

;;;; Facts used in the examples below.

(defrecord WorkOrder [id clientid scale type requestdate duedate])

(defrecord ValidationError [reason description])

(defrecord ApprovalForm [formname])

(defrecord ClientTier [id tier])

(defn days-between
  "Returns the days between the start and end times."
  [start end]
  (t/in-days (t/interval start end)))

;;;; Some example rules. ;;;;

(defrule large-job-delay
  "Large jobs must have at least a two week delay,
   unless it is a top-tier client"
  [WorkOrder (= ?clientid clientid)
   (= scale :big)
   (< (days-between requestdate duedate) 14)]

  [:not [ClientTier
         (= ?clientid id)                                   ; Join to the above client ID.
         (= tier :top)]]
  =>
  (insert! (->ValidationError
             :timeframe
             "Insufficient time prior to due date of the large order.")))

(defrule hvac-approval
  "HVAC repairs need the appropriate paperwork."
  [WorkOrder (= type :hvac)]
  [:not [ApprovalForm (= formname "27B-6")]]
  =>
  (insert! (->ValidationError
             :approval
             "HVAC repairs must include a 27B-6 form.")))

(defquery check-for-validation-error
  "Checks the job for validation errors."
  []
  [?issue <- ValidationError])

;;;; Run the above example. ;;;;

(defn validate! [session]
  (vec (map #(get-in % [:?issue :description]) (query session check-for-validation-error)))
  )

(defsession compiled-session
            'clara-cerner.examples.validation)

(defn run-examples
  "Function to run the above example."
  []
  {:work-order-id 1
   :errors        (-> compiled-session
                      (insert (->WorkOrder 1 123
                                           :big
                                           :hvac
                                           (t/date-time 2013 8 2)
                                           (t/date-time 2013 8 5)))
                      (insert (->ClientTier 123 :top))
                      (insert (->ApprovalForm "27B-6"))
                      (fire-rules)
                      (validate!))
   }
  {:work-order-id 2
   :errors        (-> compiled-session
                      (insert (->WorkOrder 2 123
                                           :big
                                           :hvac
                                           (t/date-time 2013 8 2)
                                           (t/date-time 2013 8 5)))
                      (fire-rules)
                      (validate!))}
  )
