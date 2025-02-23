(ns clara-native-lambda.main
  (:require [cheshire.core :as json]
            [uswitch.lambada.core :as lambada]
            [clara-clj-easy.core :as clara-clj-easy]
            [clara-cerner.core :as clara-cerner])
  (:import (com.amazonaws.services.lambda.runtime Context)
           (com.amazonaws.services.lambda.runtime.api.client AWSLambda)
           (java.io InputStream OutputStream OutputStreamWriter)
           (java.lang.management ManagementFactory GarbageCollectorMXBean))
  (:gen-class))

(def handler-class "clara_native_lambda.Handler")

; Could specify type since we have access to source but otherwise add entry in reflect-config.json
; The agent can generate this info for us, see project.clj
(defn- upper-str [s]
  (.toUpperCase s))

;; reflection on String happens here
(defn- lower-str [s]
  (.toLowerCase s))

(defn process [input]

  {
   :input              (json/parse-string input true)       ; normally we would hand the input json to our clara rules
   :upper-conversion   (upper-str "originally lowercase but converted to uppercase!")
   :lower-conversion   (lower-str "ORIGINALLY UPPERCASE BUT CONVERTED TO LOWERCASE!")
   :uptime-ms          (.getUptime (ManagementFactory/getRuntimeMXBean))
   :garbage-collection (vec (map (fn [^GarbageCollectorMXBean bean] (hash-map :name (.getName bean) :count (.getCollectionCount bean) :ms (.getCollectionTime bean)))
                                 (ManagementFactory/getGarbageCollectorMXBeans)))

   :system-properties  (map #(str % "=" (System/getProperty %))
                            ["java.specification.version"
                             "java.version"
                             "java.vm.name"
                             "java.vm.version"
                             "java.vendor"
                             "java.vendor.version"])
   :clara-clj-easy     (clara-clj-easy/support)
   :clara-cerner       (clara-cerner/run-examples)
   })

(lambada/deflambdafn clara_native_lambda.Handler [^InputStream in ^OutputStream out ^Context ctx]
                     (let [writer (OutputStreamWriter. out)]
                       (json/generate-stream (process (slurp in)) writer)
                       (.flush writer)
                       (.close writer)
                       )
                     )

(defn -main [& _args]
  (AWSLambda/main (into-array String [handler-class])))
