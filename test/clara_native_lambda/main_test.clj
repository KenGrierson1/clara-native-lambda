(ns clara-native-lambda.main-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer :all]
            [clara-native-lambda.main :as main])
  (:import (com.amazonaws.services.lambda.runtime RequestStreamHandler)))

(deftest clara-native-lambda-test
  (testing "lambda handler class can be called"
    (println "result" (main/process "{\"some\": \"content\"}"))
    (is (= 1 1))
    ))

(deftest lambda-handler-test
  (testing "lambda handler class exists"
    (compile 'clara-native-lambda.main)                     ; not found when run from idea but lein kaocha finds it
    (is (= [RequestStreamHandler]
           (seq (.getInterfaces (Class/forName main/handler-class)))))))
