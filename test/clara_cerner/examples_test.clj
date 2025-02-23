(ns clara-cerner.examples-test
  (:require [clojure.test :refer :all]
            [clara-cerner.core :as core]))

(deftest clara-cerner-examples-test
  (testing "clara cerner examples"
    (println (core/run-examples))
    (is (= 1 1))
    ))
