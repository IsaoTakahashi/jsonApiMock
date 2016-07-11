package itaka.api.mock.logic;

import com.google.common.collect.ImmutableList;
import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import itaka.api.mock.registry.ApiMockDataRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Created by isao on 2016/07/10.
 */
@Component
@Slf4j
public class ApiSpy {

    @Value(value = "${apimock.spy-target-url}")
    private String spyTargetUrl;

    private RestTemplate restTemplate;

    public ApiSpy() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new SpyRestTemplateErrorHandler());
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        restTemplate.setMessageConverters(ImmutableList.of(formHttpMessageConverter, stringHttpMessageConverter));
    }

    public ResponseEntity<String> proxy(HttpServletRequest request) throws URISyntaxException {

        ApiMockRequest mockRequest = new ApiMockRequest(request, "/spy");

        //call actual API
        ResponseEntity<String> responseEntity = callAPI(mockRequest);

        //register API mock data
        ApiMockResponse mockResponse = new ApiMockResponse(responseEntity.getBody(), responseEntity.getStatusCode().value());
        ApiMockData apiMockData = new ApiMockData(mockRequest.hash(), mockRequest, mockResponse);

        ApiMockDataRegistry.register(apiMockData);

        return createResponse(responseEntity);
    }

    private ResponseEntity<String> callAPI(ApiMockRequest mockRequest) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        if (mockRequest.getContentType() != null) {
            headers.setContentType(MediaType.parseMediaType(mockRequest.getContentType()));
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(spyTargetUrl + mockRequest.getEndpoint());
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();

        mockRequest.getParams().stream().forEach(p -> {
            builder.queryParam(p.getKey(), p.getValue());
            paramMap.add(p.getKey(), p.getValue());
        });
        HttpEntity<?> entity;

        if (StringUtils.isNotEmpty(mockRequest.getBody())) {
            entity = new HttpEntity<>(mockRequest.getBody(), headers);
        } else {
            entity = new HttpEntity<>(paramMap, headers);
        }

        return restTemplate.exchange(
                builder.build().encode().toUri().toString(),
                HttpMethod.valueOf(mockRequest.getMethod()),
                entity,
                String.class);
    }

    private ResponseEntity<String> createResponse(ResponseEntity<String> apiEntity) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(apiEntity.getHeaders().getContentType());

        return new ResponseEntity<>(apiEntity.getBody(), responseHeaders, apiEntity.getStatusCode());
    }
}
