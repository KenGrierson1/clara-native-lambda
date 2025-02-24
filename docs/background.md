# Background, performance, etc

## First call
The first call to the lambda is slow, as AWS provisions the server

![Provisioning Call](./FirstCall.png "Provisioning Call")

## Second call
The second call is  much faster with no cold starts
![Second Call](./SecondCall.png "Second Call")

## Later calls
Later calls are still fast even when the application is restarted - see the low uptime
![Later Calls](./Later.png "Later Calls")