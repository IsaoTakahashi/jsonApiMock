# jsonApiMock
Mock application for API which responds json

## Lisence
This software is released under the MIT License, see LICENSE.md.

## Environment
- JDK 1.8
- Maven 3

## Usage
Sorry, it is under construction.

## Endpoints
### Basic Endpoints
#### /apimock/register/{mockTargetPath} [anyMethod]
register API request's `Endpoint` / `HttpMethod` / `RequestBody` and `QueryString` as Mock Data.

#### Request
- {mockTargetPath} : endpoint you want to mock
- HttpMethod, RequestBody, QueryString : same value when you call Original API

#### Response
reference ID of mock data

#### Sample
```shell
#register mock data for /test/hoge with 'name' parameter
curl http://localhost:8080/apimock/register/test/hoge?name=John
##> 3440f2e01a9d2779b2823811b5e5cf9c
```

#### /apimock/updateResponse/{id}/{status} [POST]
register or update mock response body and status for mocked request.

##### Request
- {id} : reference ID of mock data
- {status} : http status of mock response you want to receive
- RequestBody : json text which you want to receive

##### Response
reference ID of mock data

##### Sample
```shell
# update response for 3440f2e01a9d2779b2823811b5e5cf9c as 200 status
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/updateResponse/3440f2e01a9d2779b2823811b5e5cf9c/200 -d '{"greeting" : "hello, John!"}'
##> 3440f2e01a9d2779b2823811b5e5cf9c
```

#### /apimock/execute/{mockTargetPath} [anyMethod]
respond mocked response which is registered.

##### Request
- {mockTargetPath} : endpoint you want to mock
- HttpMethod, RequestBody, QueryString : same value when you call Original API

##### Response
mocked response you registered via `/apimock/updateResponse`

##### Sample
```shell
# request is matched
http://localhost:8080/apimock/execute/test/hoge?name=John
##> {"greeting" : "hello, John!"}

# param's value is not matched
curl http://localhost:8080/apimock/execute/test/hoge?name=Taro
##> {"message" : "the request is not mocked."}

# endpoint is not matched
curl http://localhost:8080/apimock/execute/test/moge?name=John
##> {"message" : "the request is not mocked."}

# HttpMethod is not matched
curl -X POST http://localhost:8080/apimock/execute/test/moge?name=John
##> {"message" : "the request is not mocked."}
```
