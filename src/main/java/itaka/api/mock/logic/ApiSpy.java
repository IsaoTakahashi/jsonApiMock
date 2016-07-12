package itaka.api.mock.logic;

import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import itaka.api.mock.registry.ApiMockDataRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Created by isao on 2016/07/10.
 */
@Component
@Slf4j
public class ApiSpy {

    private ApiCaller apiCaller;

    @Autowired
    public ApiSpy(ApiCaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    public ResponseEntity<String> proxy(HttpServletRequest request) throws URISyntaxException {

        ApiMockRequest mockRequest = new ApiMockRequest(request, "/spy");

        //call actual API
        ResponseEntity<String> responseEntity = apiCaller.call(mockRequest);

        //register API mock data
        ApiMockResponse mockResponse = new ApiMockResponse(responseEntity.getBody(), responseEntity.getStatusCode().value());
        ApiMockData apiMockData = new ApiMockData(mockRequest.hash(), mockRequest, mockResponse);

        ApiMockDataRegistry.register(apiMockData);

        return responseEntity;
    }
}
