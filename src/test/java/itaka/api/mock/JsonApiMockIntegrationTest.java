package itaka.api.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itaka.api.mock.bean.ApiMockData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by isao on 2016/07/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JsonApiMockApplication.class)
@WebIntegrationTest
@Slf4j
public class JsonApiMockIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/apimock/";

    private RestTemplate restTemplate = new TestRestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test_request_response_execute() {
        String mockTargetOath = "test/hoge";

        // register request
        String referenceID = restTemplate.getForObject(BASE_URL + "request/" + mockTargetOath, String.class);

        // register response
        String responseString = "{\"id\" : \"test\"}";
        restTemplate.postForObject(BASE_URL + "response/" + referenceID + "/200", responseString, String.class);

        // try execute
        String result = restTemplate.getForObject(BASE_URL + "execute/" + mockTargetOath, String.class);

        assertThat(result).isEqualTo(responseString);
    }

    @Test
    public void test_spy_proxy() throws IOException {
        String resultJson = restTemplate.getForObject(BASE_URL + "spy/info", String.class);
        Map<String, String> resultMap = mapper.readValue(resultJson, new TypeReference<HashMap<String, String>>() {
        });

        log.info(resultMap.toString());
        assertThat(resultMap).containsKeys("version");
    }

    @Test
    public void test_spy_recordMockData() throws IOException {
        restTemplate.getForObject(BASE_URL + "spy/info", String.class);
        ResponseEntity<List<ApiMockData>> responseEntity = restTemplate.exchange(BASE_URL + "data",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ApiMockData>>() {
                });

        log.info(responseEntity.getBody().toString());
        assertThat(responseEntity.getBody()).extracting("id").contains("5aa80de21433aff7f1dfb0c797c468f9");
    }
}
