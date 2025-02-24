# clara-native-lambda
A Clara Rules clojure microservice implemented as a native image with AWS lambda and ecr.

A typical web-service that consumes json input and produces json output generated in part by the Clara Rules engine.
The clojure code is compiled to a native image using GraalVM, deployed as a docker container on AWS ECR and executed as an AWS lambda.

## Prerequisites
[Clara Rules](http://www.clara-rules.org/) +
[GraalVM Native Image](https://www.graalvm.org/reference-manual/native-image/) +
[AWS Lambda container images](https://aws.amazon.com/blogs/aws/new-for-aws-lambda-container-image-support/).

### Build tools:

- [Java GraalVM 23 JDK](https://github.com/graalvm/graalvm-ce-builds/releases)
- [Leiningen](https://leiningen.org/)
- [Docker](https://www.docker.com/)

### Deployment tools:

- [AWS CLI](https://aws.amazon.com/cli/)

## Building
You should have GraalVM installed as your java compiler, this project was built with v23

- lein kaocha
- lein uberjar
- ./native-image.sh # runs [Native Image](https://www.graalvm.org/reference-manual/native-image/)  to process the uberjar and create the executable. The `native-image` command requires lots of time and memory. Even this
  simple application is slow !

Pre-built application-specific Native Image configuration is provided in [resources/META-INF/native-image/reflect-config.json](resources/META-INF/native-image/reflect-config.json) and directory [resources/META-INF/native-image/com.amazonaws](resources/META-INF/native-image/com.amazonaws).
More 'reachability data' is generated when the tests are run and these will appear in directory resources/META-INF/native-image/clara-native-lambda

## Running Locally

- `.\run-local.sh`
- `curl -XPOST "http://localhost:8080/2015-03-31/functions/function/invocations" -d '{"some": "content"}'`

## Configuring AWS
- `aws ecr create-repository --repository-name clara-native-lambda`
- `aws ecr get-login-password --region <your region> | docker login --username AWS --password-stdin <your account>.dkr.ecr.<ypur region>.amazonaws.com`
- `aws lambda create-function --function-name clara-native-lambda  --package-type Image --code ImageUri=<your account>.dkr.ecr.<your region>.amazonaws.com/clara-native-lambda:latest  --role arn:aws:iam::<your account>:role/lambda-role`

## Deploying to AWS

There is a single Dockerfiles for packaging the application:

- [Dockerfile-clara-native-lambda](Dockerfile-clara-native-lambda) Uses an AWS-provided base image suitable for deploying to AWS ECR
- `docker build -t clara-native-lambda -f Dockerfile-clara-native-lambda .`
- `docker tag clara-native-lambda:latest <your account>.dkr.ecr.<your region>.amazonaws.com/clara-native-lambda:latest`
- `docker push <your account>.dkr.ecr.<your region>.amazonaws.com/clara-native-lambda:latest`

## Acknowledgements

This project is freely adapted from ;-
- Esko Luontola's [native-clojure-lambda](https://github.com/luontola/native-clojure-lambda)
- Cerner's  [Clara Example Rules](https://github.com/cerner/clara-examples)
- clj-easy's clara-rules [graalvm-clojure](https://github.com/clj-easy/graalvm-clojure.git)
