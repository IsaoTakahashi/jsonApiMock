# jsonApiMock
![circle_ci_result](https://circleci.com/gh/IsaoTakahashi/jsonApiMock.png?style=shield&circle-token=%204347e2fed3eca5da1b49bae9327de054d5f8f3c2)

Mock application for API which responds json  
(日本語のドキュメントは[こちら](http://qiita.com/IsaoTakahashi/items/a2a184710f1b7834f19d))

## Feature
- Simulate json style API behaviour
    + GET, POST and other HTTP methods
    + can respond different json for same `Endpoint`, if `HttpMethod` or `RequestBody` or `QueryString` is different.
- Grab client's request and API response as mock data
    + for detail, please refer "Spy Mode".

![jsonApiMock_simpleflow.png](https://qiita-image-store.s3.amazonaws.com/0/72808/e24538af-e8df-2969-506a-f68e7de8b73b.png)

### What is mocked in jsonApiMock?

- Request
    + Endpoint
    + Method
    + Content-type
    + Body
    + Parameters (QueryString)
- Response
    + Body
    + Status

Here is API mock data.

```json
{
    "id": "82b16c33c5060e5d57e9d2111d465c1c",
    "request": {
        "body": "",
        "endpoint": "/users",
        "headers": {
            "accept": "*/*",
            "host": "localhost:8080",
            "user-agent": "curl/7.43.0"
        },
        "method": "GET",
        "params": [
            {
                "key": "name",
                "value": "Jiro"
            }
        ]
    },
    "response": {
        "body": "[\n  {\n    \"name\": \"Jiro\",\n    \"id\": 3\n  }\n]",
        "httpStatus": 200
    }
}
```

## Environment
- JDK 1.8
- Maven 3

## Framework
- Spring Boot 1.3.6

## Usage

### How to startup
```shell
mvn spring-boot:run
```
or run packaged jar

```shell
mvn package
java -jar target/jsonAPIMock.jar
```

#### Health Check
call `http://localhost:8080/apimock/health`
(default port is 8080, you can change it by modifying `server.port` in src/main/resources/application.yml)

### Simple Flow
![jsonApiMock_simpleflow.png](https://qiita-image-store.s3.amazonaws.com/0/72808/e24538af-e8df-2969-506a-f68e7de8b73b.png)

1. register mock request via `/apimock/request/{mockTargetPath}`
2. register mock response via `/apimock/response/{id}/{status}`
3. call `/apimock/execute/{mockTargetPath}` to retrieve mocked response


```shell
# register Request for "/test/hoge" 
curl http://localhost:8080/apimock/request/test/hoge?name=John
##> 3440f2e01a9d2779b2823811b5e5cf9c

# register Response for "/test/hoge"
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/response/3440f2e01a9d2779b2823811b5e5cf9c/200 -d '
{"greeting" : "hello, John!"}'
##> 3440f2e01a9d2779b2823811b5e5cf9c

# use mock as "/test/hoge"
curl http://localhost:8080/apimock/execute/test/hoge?name=John
##> {"greeting" : "hello, John!"}
```

### Spy Mode (Experimental)
:sunglasses:  
In this mode, jsonApiMock will be proxy between client app and actual API.
And jsonApiMock grabs client's request and API's response as API mock data.

![jsonApiMock_spymode.png](https://qiita-image-store.s3.amazonaws.com/0/72808/7af60031-62eb-bd37-63dd-fbae346263b9.png)

1. set actual API url as `apimock.spy-target-url` in application.yml.  
   (you can set the property running jar file.   
    e.g. `java -jar target/jsonAPIMock.jar --apimock.spy-target-url="http://actual.api.com/root" `)
2. client app calls api via `/apimock/spy/{mockTargetPath}`
   -> jsonApiMock call actual API with client's request.
   -> jsonApiMock returns API's response to client.
3. you can get mocked API response via `/apimock/execute/{mockTargetPath}`

For detail, please refer to [Spy Endpoint](#spy-endpoint)

## Endpoints
### Basic Endpoints
#### /apimock/request/{mockTargetPath} [anyMethod]
register API request's `Endpoint` / `HttpMethod` / `RequestBody` and `QueryString` as Mock Data.

#### Request
- {mockTargetPath} : endpoint you want to mock
- HttpMethod, RequestBody, QueryString : same value when you call Original API

#### Response
reference ID of API mock data

#### Sample
```shell
#register mock data for /test/hoge with 'name' parameter
curl http://localhost:8080/apimock/request/test/hoge?name=John

##> 3440f2e01a9d2779b2823811b5e5cf9c
```

#### /apimock/response/{id}/{status} [POST]
register or update mock response body and status for mocked request.

##### Request
- {id} : reference ID of mock data
- {status} : http status of mock response you want to receive
- RequestBody : json text which you want to receive

##### Response
reference ID of API mock data

##### Sample
```shell
# update response for 3440f2e01a9d2779b2823811b5e5cf9c as 200 status
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/response/3440f2e01a9d2779b2823811b5e5cf9c/200 -d '
{"greeting" : "hello, John!"}'

##> 3440f2e01a9d2779b2823811b5e5cf9c
```

#### /apimock/execute/{mockTargetPath} [anyMethod]
execute API mock which is registered.

##### Request
- {mockTargetPath} : endpoint you want to mock
- HttpMethod, RequestBody, QueryString : same value when you call Original API

##### Response
mocked API response you registered via `/apimock/updateResponse`

##### Sample
```shell
# request is matched
curl http://localhost:8080/apimock/execute/test/hoge?name=John

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

#### /apimock/test/{mockTargetPath} [anyMethod]
get API mock data(request, response) which is registered.

##### Request
- {mockTargetPath} : endpoint you want to mock
- HttpMethod, RequestBody, QueryString : same value when you call Original API

##### Response
API mock data you registered via `/apimock/updateResponse`

##### Sample
```shell
curl http://localhost:8080/apimock/test/test/hoge?name=John

##> {"id":"3440f2e01a9d2779b2823811b5e5cf9c","request":{"endpoint":"/test/hoge","method":"GET","body":"","params":[{"key":"name","value":"John"}]},"response":{"body":"{    \"reply\" : \"hello, Hoge-san!\"}","httpStatus":200}}
```

### Support Endpoints
#### /apimock/data [GET]
get all registered API mock data

##### Request
- none

##### Response
all registered API mock data

##### Sample
```shell
curl http://localhost:8080/apimock/data

##> [{"id":"417926714941341a6706616733719fed","request":{"endpoint":"/test/hoge","method":"GET","body":"","params":[{"key":"name","value":"JohnDoe"}]},"response":{"body":"{\"message\" : \"request is mocked, but response is not defined.}\"","httpStatus":200}},{"id":"3440f2e01a9d2779b2823811b5e5cf9c","request":{"endpoint":"/test/hoge","method":"GET","body":"","params":[{"key":"name","value":"John"}]},"response":{"body":"{    \"reply\" : \"hello, Hoge-san!\"}","httpStatus":200}}]
```

#### /apimock/data/{id} [GET]
retrieve API Mock data for {id}

##### Request
- {id} : reference ID of API mock data

##### Response
API Mock data

##### Sample
```shell
curl http://localhost:8080/apimock/data/3440f2e01a9d2779b2823811b5e5cf9c

##> {"id":"3440f2e01a9d2779b2823811b5e5cf9c","request":{"endpoint":"/test/hoge","method":"GET","body":"","params":[{"key":"name","value":"John"}]},"response":{"body":"{    \"reply\" : \"hello, Hoge-san!\"}","httpStatus":200}}
```

#### /apimock/data [POST]
register(or replace) API mock data

##### Request
- RequestBody : API mock data(json format)

##### Response
reference ID of API mock

##### Sample
```shell
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/data -d '
{"id":"cf832c073372815d2dea17cb8f3e9788","request":{"endpoint":"/test/aaa","method":"GET","body":"","params":[]},"response":{"body":"{    \"answer\" : \"OK!\"}","httpStatus":200}}'

##> cf832c073372815d2dea17cb8f3e9788
```

#### /apimock/dataList [POST]
register(or replace) multiple API mock data

##### Request
- RequestBody : List of API mock data(json format)

##### Response
- List of reference ID

##### Sample
```shell
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/dataList -d '
[{"id":"417926714941341a6706616733719fed","request":{"endpoint":"/test/hoge","method":"GET","body":"","params":[{"key":"name","value":"JohnDoe"}]},"response":{"body":"{\"message\" : \"request is mocked, but response is not defined.}\"","httpStatus":200}},{"id":"3440f2e01a9d2779b2823811b5e5cf9c","request":{"endpoint":"/test/hoge","method":"GET","body":"","params":[{"key":"name","value":"John"}]},"response":{"body":"{    \"reply\" : \"hello, Hoge-san!\"}","httpStatus":200}}]'

##> ["417926714941341a6706616733719fed","3440f2e01a9d2779b2823811b5e5cf9c"]
```

### Spy Endpoint
#### /apimock/spy/{mockTargetPath} [anyMethod]
proxy user's request to actual API, and return API's response as it as.

##### Request
- {mockTargetPath} : endpoint you want to mock
- HttpMethod, RequestBody, QueryString : same value when you call actual API

##### Response
actual API's response

##### Sample
```shell
# run with setting actual API url
java -jar target/jsonAPIMock.jar --apimock.spy-target-url="http://gturnquist-quoters.cfapps.io"

# or, you can live-update the url via /env endpoint
curl -X POST http://localhost:8080/apimock/env -d apimock.spy-target-url="http://gturnquist-quoters.cfapps.io"
##> {"apimock.spy-target-url":"http://gturnquist-quoters.cfapps.io"}

# confirm thre is not mock data (optional)
curl http://localhost:8080/apimock/data
##> []

# spy actual API request
curl http://localhost:8080/apimock/spy/api/random
##> {"type":"success","value":{"id":12,"quote":"@springboot with @springframework is pure productivity! Who said in #java one has to write double the code than in other langs? #newFavLib"}}

# confirm spy result
curl http://localhost:8080/apimock/data
##> [{"id":"89150a098c708c1cfc2c26c03b867902","request":{"endpoint":"/api/random","method":"GET","contentType":null,"body":"","params":[]},"response":{"body":"{\"type\":\"success\",\"value\":{\"id\":12,\"quote\":\"@springboot with @springframework is pure productivity! Who said in #java one has to write double the code than in other langs? #newFavLib\"}}","httpStatus":200}}]

# you can execute this mock
curl http://localhost:8080/apimock/execute/api/random
##> {"type":"success","value":{"id":12,"quote":"@springboot with @springframework is pure productivity! Who said in #java one has to write double the code than in other langs? #newFavLib"}}
```

## Lisence
This software is released under the MIT License, see LICENSE.md.
