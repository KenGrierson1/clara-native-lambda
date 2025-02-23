# Background, performance, etc

## First call
The first call to the lambda is slow, as AWS provisions the server

![Provisioning Call](./FirstCall.png "Provisioning Call")

## Subsequent calls
Subsequent calls are much faster with no cold starts
![Subsequent Calls](./SecondCall.png "Subsequent Calls")