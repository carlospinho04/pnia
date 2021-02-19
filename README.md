# PhoneNumberAggregator
PhoneNumberAggregator is a REST service that aggregates phone numbers according to prefix and business sector which it belongs to.

## How to run
To run this service you can run `docker-compose up web` and you will have a service listening to requests on `http://localhost:8080`.

After a change in the code you can run `docker-compose up --build web` and update the executed container.

When the service is available you do a POST request to:

`http://localhost:8080/aggregate`

With the body:

```
["+1983248", "001382355", "+147 8192", "+4439877"]
```