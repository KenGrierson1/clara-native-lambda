#!/bin/sh
export _HANDLER="clara_native_lambda.Handler"
exec ./aws-lambda-rie ./clara-native-lambda &
