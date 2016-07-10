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
reference ID of API mock data

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
reference ID of API mock data

##### Sample
```shell
# update response for 3440f2e01a9d2779b2823811b5e5cf9c as 200 status
curl -H "Content-type: application/json" -X POST http://localhost:8080/apimock/updateResponse/3440f2e01a9d2779b2823811b5e5cf9c/200 -d '{"greeting" : "hello, John!"}'

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