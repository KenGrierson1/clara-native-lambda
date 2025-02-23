(ns clara-cerner.examples.shopping
  (:require [clara.rules.accumulators :as acc]
            [clara.rules :refer :all]))

;;;; Facts used in the examples below.

(defrecord Order [year month day])

(defrecord Customer [status])

(defrecord Purchase [cost item])

(defrecord Discount [reason percent])

(defrecord Total [total])

(defrecord Promotion [reason type])

;;;; Some example rules. ;;;;

(defrule total-purchases
  [?total <- (acc/sum :cost) :from [Purchase]]
  =>
  (insert! (->Total ?total)))

;;; Discounts.
(defrule summer-special
  "Place an order in the summer and get 20% off!"
  [Order (#{:june :july :august} month)]
  =>
  (insert! (->Discount :summer-special 20)))

(defrule vip-discount
  "VIPs get a discount on purchases over $100. Cannot be combined with any other discount."
  [Customer (= status :vip)]
  [Total (> total 100)]
  =>
  (insert! (->Discount :vip 10)))

(def max-discount
  "Accumulator that returns the highest percentage discount."
  (acc/max :percent :returns-fact true))

(defquery get-best-discount
  "Query to find the best discount that can be applied"
  []
  [?discount <- max-discount :from [Discount]])

;;; Promotions.
(defrule free-widget-month
  "All purchases over $200 in August get a free widget."
  [Order (= :august month)]
  [Total (> total 200)]
  =>
  (insert! (->Promotion :free-widget-month :widget)))

(defrule free-lunch-with-gizmo
  "Anyone who purchases a gizmo gets a free lunch."
  [Purchase (= item :gizmo)]
  =>
  (insert! (->Promotion :free-lunch-with-gizmo :lunch)))

(defquery get-promotions
  "Query to find promotions for the purchase."
  []
  [?promotion <- Promotion])

;;;; The section below shows this example in action. ;;;;

(defn format-discounts
  "Format the discounts from the given session."
  [session]

  ;; Destructure and print each discount.
  (for [{{reason :reason percent :percent} :?discount} (query session get-best-discount)]
    (hash-map :percent percent :reason reason))
  )

(defn format-promotions
  "Format promotions from the given session"
  [session]

  (for [{{reason :reason type :type} :?promotion} (query session get-promotions)]
    (hash-map :type type :reason reason))
  )

(defsession compiled-session-no-cache
            'clara-cerner.examples.shopping
            :cache false)

(defn run-examples
  "Function to run the above example."
  []
  (let [
        ; VIP shopping example
        vip-session (-> compiled-session-no-cache           ; Load the rules.
                        (insert (->Customer :vip)
                                (->Order 2013 :march 20)
                                (->Purchase 20 :gizmo)
                                (->Purchase 120 :widget))   ; Insert some facts.
                        (fire-rules)
                        )

        ; Summer special and widget promotion example
        promo-session (-> compiled-session-no-cache         ; Load the rules.
                          (insert (->Customer :vip)
                                  (->Order 2013 :august 20)
                                  (->Purchase 20 :gizmo)
                                  (->Purchase 120 :widget)
                                  (->Purchase 90 :widget))  ; Insert some facts.
                          (fire-rules)
                          )
        ]

    {:vip {:discounts (format-discounts vip-session)}
     :promo {:discounts (format-discounts promo-session)
             :promotions (format-promotions promo-session)}}
    ))
