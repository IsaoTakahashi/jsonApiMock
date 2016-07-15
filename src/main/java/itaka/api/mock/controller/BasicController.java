package itaka.api.mock.controller;

import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import itaka.api.mock.exception.NoMockDataException;
import itaka.api.mock.registry.ApiMockDataRegistry;
import itaka.api.mock.util.RequestConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by isao on 2016/07/09.
 */
@RestController
@Slf4j
public class BasicController {

    @Autowired
    ApiMockDataRegistry apiMockDataRegistry;

    @RequestMapping(value = "request/**")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerRequest(HttpServletRequest request) {
        ApiMockRequest apiMockRequest = new ApiMockRequest(request, "/request");
        String id = apiMockRequest.hash();
        apiMockDataRegistry.register(id, apiMockRequest);

        return id;
    }

    @RequestMapping(value = "response/{id}/{status}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String updateResponse(@PathVariable String id, @PathVariable Integer status, HttpServletRequest request) {
        if (!apiMockDataRegistry.isExists(id)) {
            throw new NoMockDataException("requested data is not found : " + id);
        }

        apiMockDataRegistry.updateResponse(id, new ApiMockResponse(RequestConverter.getBody(request), status));
        return id;
    }

    @RequestMapping(value = "execute/**")
    public ResponseEntity<String> executeMock(HttpServletRequest request) {
        ApiMockRequest apiMockRequest = new ApiMockRequest(request, "/execute");
        String id = apiMockRequest.hash();
        ApiMockData apiMockData = apiMockDataRegistry.get(id).orElse(new ApiMockData());

        return buildResponseEntity(apiMockData.getResponse());
    }

    @RequestMapping(value = "test/**")
    public ApiMockData testMock(HttpServletRequest request) {
        ApiMockRequest apiMockRequest = new ApiMockRequest(request, "/test");
        String id = apiMockRequest.hash();

        return apiMockDataRegistry.get(id).orElse(new ApiMockData());
    }

    private ResponseEntity<String> buildResponseEntity(ApiMockResponse apiMockResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");

        return new ResponseEntity<>(apiMockResponse.getBody(),
                headers, HttpStatus.valueOf(apiMockResponse.getHttpStatus()));
    }

    @ExceptionHandler(NoMockDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String dataNotFound(NoMockDataException e) {
        return e.getMessage();
    }
}
