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
#### /apimock/register/{mockTargetPath} [anyMethod]

##### Description
register API request's endpoint / httpMethod / requestBody / requestParams as Mock Data.

```sh
curl http://localhost:8080/apimock/register/test/hoge?name=John
## 3440f2e01a9d2779b2823811b5e5cf9c
```

#### /apimock/updateResponse/{id}/{status} [POST]
register or update mock response for mocked request.

```sh
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/updateResponse/3440f2e01a9d2779b2823811b5e5cf9c/200 -d '{"greeting" : "hello, John!"}'
## 3440f2e01a9d2779b2823811b5e5cf9c
```

#### /apimock/execute/{mockTargetPath} [anyMethod]
respond mocked response which is registered.

```sh
# request is matched
http://localhost:8080/apimock/execute/test/hoge?name=John
## {"greeting" : "hello, John!"}

# param's value is not matched
curl http://localhost:8080/apimock/execute/test/hoge?name=Taro
## {"message" : "the request is not mocked."}

# endpoint is not matched
curl http://localhost:8080/apimock/execute/test/moge?name=John
## {"message" : "the request is not mocked."}

# HttpMethod is not matched
curl -X POST http://localhost:8080/apimock/execute/test/moge?name=John
## {"message" : "the request is not mocked."}
```
