package itaka.api.mock.logic;

import com.google.common.collect.ImmutableList;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.config.ApiMockProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by isao on 2016/07/12.
 */
@Component
@Slf4j
public class ApiCaller {

    private ApiMockProperties apiMockProperties;

    private RestTemplate restTemplate;

    @Autowired
    public ApiCaller(ApiMockProperties apiMockProperties) {
        this.apiMockProperties = apiMockProperties;

        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new SpyRestTemplateErrorHandler());
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        restTemplate.setMessageConverters(ImmutableList.of(formHttpMessageConverter, stringHttpMessageConverter));
    }

    public ResponseEntity<String> call(ApiMockRequest mockRequest) throws URISyntaxException {
        RequestEntity<?> requestEntity = createRequestEntity(mockRequest);

        return createResponseEntity(restTemplate.exchange(requestEntity, String.class));
    }

    private RequestEntity<?> createRequestEntity(ApiMockRequest mockRequest) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        mockRequest.getHeaders().entrySet().forEach(entry -> headers.add(entry.getKey(), entry.getValue()));
        HttpMethod httpMethod = HttpMethod.valueOf(mockRequest.getMethod());

        RequestEntity<?> requestEntity;
        String urlString = apiMockProperties.getSpyTargetUrl() + mockRequest.getEndpoint();

        if (httpMethod == HttpMethod.GET) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
            mockRequest.getParams().stream().forEach(p -> builder.queryParam(p.getKey(), p.getValue()));
            requestEntity = new RequestEntity<>(null, headers, httpMethod, builder.build().encode().toUri());
        } else {
            URI uri = new URI(urlString);
            if (StringUtils.isNotEmpty(mockRequest.getBody())) {
                requestEntity = new RequestEntity<>(mockRequest.getBody(), headers, httpMethod, uri);
            } else {
                MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
                mockRequest.getParams().stream().forEach(p -> paramMap.add(p.getKey(), p.getValue()));
                requestEntity = new RequestEntity<>(paramMap, headers, httpMethod, uri);
            }
        }

        log.info("RequestEntity : {}", requestEntity);
        return requestEntity;
    }

    private ResponseEntity<String> createResponseEntity(ResponseEntity<String> apiEntity) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if(apiEntity.getHeaders().getContentType() != null) {
            responseHeaders.setContentType(apiEntity.getHeaders().getContentType());
        }

        return new ResponseEntity<>(apiEntity.getBody(), responseHeaders, apiEntity.getStatusCode());
    }
}
