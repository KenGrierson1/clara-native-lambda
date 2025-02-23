#!/usr/bin/env bash

image=./clara-native-lambda

# Feature clj_easy.graal_build_time replaces --initialize-at-build-time
# jackson classes pulled in by cheshire library
native-image \
        --add-opens java.base/java.util=ALL-UNNAMED \
        --no-fallback \
        -H:+UnlockExperimentalVMOptions \
        -H:Name=clara-native-lambda \
        -H:+ReportExceptionStackTraces \
        -H:+PrintAnalysisCallTree \
        --features=clj_easy.graal_build_time.InitClojureClasses \
        \
        --initialize-at-build-time=com.fasterxml.jackson \
        --initialize-at-build-time=com.fasterxml.jackson.core.JsonFactory \
        --initialize-at-build-time=com.fasterxml.jackson.core.io.SerializedString \
        --initialize-at-build-time=com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer \
        --initialize-at-build-time=com.fasterxml.jackson.dataformat.cbor.CBORFactory \
        --initialize-at-build-time=com.fasterxml.jackson.core.StreamReadConstraints \
        \
        --initialize-at-build-time=org.joda.time.tz \
        --initialize-at-build-time=org.joda.time.tz.FixedDateTimeZone \
        --initialize-at-build-time=org.joda.time.tz.ZoneInfoProvider \
        --initialize-at-build-time=org.joda.time.tz.DefaultNameProvider \
        \
        --exact-reachability-metadata=warn \
        --initialize-at-run-time=clojure.pprint.dispatch__init \
        --initialize-at-run-time=clojure.stacktrace__init \
        --initialize-at-run-time=clojure.pprint__init \
        --initialize-at-run-time=clojure.reflect.java__init \
        \
        --initialize-at-run-time=cerner-clara.examples.validation__init \
        --initialize-at-run-time=cerner-clara.examples.truth_maintenance__init \
        \
        -jar target/uberjar/clara-native-lambda.jar $image
ec=$?
echo "native-image exit code: $ec"
if [[ $ec -eq 0 && -e $image ]] ; then
   chmod +x $image
fi
