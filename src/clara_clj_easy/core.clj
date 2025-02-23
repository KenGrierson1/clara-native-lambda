(ns clara-clj-easy.core
  (:require [clara.rules :refer :all]
            [clara-clj-easy.rules :as r]))

;;
;; Update this file to use the libary you wish to test.
;;

(defsession compiled-session
            'clara-clj-easy.rules)

(defn notifications! [session]
  (vec (map #(get-in % [:?notification :description]) (query session r/check-task)))
  )

(defn support []
  {:notifications (-> compiled-session
                (insert (r/->ClientRepresentative "Alice" "Acme")
                        (r/->SupportRequest "Acme" :high))
                (fire-rules)
                (notifications!))})
