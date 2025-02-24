(defproject clara-native-lambda "0.1.0-SNAPSHOT"

  :description "Example project of Clara Rules + GraalVM Native Image + AWS Lambda Containers"
  :url "https://github.com/KenGrierson1/clara-native-lambda"
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.9.0"
  :dependencies [[com.amazonaws/aws-lambda-java-core "1.2.1"]
                 [com.amazonaws/aws-lambda-java-runtime-interface-client "1.0.0"]
                 [cheshire "5.13.0"]
                 [medley "1.4.0"]
                 [org.clojure/clojure "1.12.0"]
                 [uswitch/lambada "0.1.2"]

                 ;; Dependency for time-based rules example.
                 [clj-time "0.6.0"]

                 ;; Dependency for rule DSL example.
                 [instaparse "1.4.1"]

                 [com.cerner/clara-rules "0.24.0" :exclusions [org.clojure/clojure]]
                 [com.github.clj-easy/graal-build-time "1.0.5" :exclusions [org.clojure/clojure]]
                 ]
  :managed-dependencies [[org.clojure/spec.alpha "0.2.187"]]
  :pedantic? :abort

  :target-path "target/%s"
  :main clara-native-lambda.main
  :global-vars {*warn-on-reflection* true
                *print-namespace-maps* false}
  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"
             ]

  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]}
  :plugins [[lein-ancient "0.6.15"]]

  :profiles {:uberjar {:uberjar-name "clara-native-lambda.jar"
                       :aot :all
                       :omit-source true}
             :dev {:dependencies [[lambdaisland/kaocha "1.0.732"]
                                  [org.clojure/test.check "1.1.0"]]
                   :jvm-opts ["-agentlib:native-image-agent=caller-filter-file=filter.json,config-merge-dir=resources/META-INF/native-image/clara-native-lambda"]
                   }
             :kaocha {}})
