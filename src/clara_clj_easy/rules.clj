(ns clara-clj-easy.rules
  (:require [clara.rules :refer :all]))

(defrecord SupportRequest [client level])

(defrecord ClientRepresentative [name client])

(defrecord Notification [description])

(defrule is-important
  "Find important support requests."
  [SupportRequest (= :high level)]
  =>
  (insert! (->Notification "High support requested!")))

(defrule notify-client-rep
  "Find the client representative and send a notification of a support request."
  [SupportRequest (= ?client client)]
  [ClientRepresentative (= ?client client) (= ?name name)] ; Join via the ?client binding.
  =>
  (insert! (->Notification (str "Notify " ?name " that " ?client " has a new support request!"))))

(defquery check-task
  "Checks for support tasks."
  []
  [?notification <- Notification])
