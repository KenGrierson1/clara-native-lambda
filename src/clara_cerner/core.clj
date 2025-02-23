(ns clara-cerner.core
  (:require [clara-cerner.examples.shopping :as shopping]
            [clara-cerner.examples.validation :as validation]
            [clara-cerner.examples.sensors :as sensors]
            [clara-cerner.examples.booleans :as booleans]
            [clara-cerner.examples.fact-type-options :as type-opts]
            [clara-cerner.examples.insta :as insta]
            [clara-cerner.examples.truth-maintenance :as truth]
            ))

(defn run-examples []
  {
   :shopping          (shopping/run-examples)
   :validation        (validation/run-examples)
   :sensor            (sensors/run-examples)
   :boolean           (booleans/run-examples)
   :fact-type-options (type-opts/run-examples)
   :insta             (insta/run-examples)
   :truth-maintenance (truth/run-examples)
   }
  )
