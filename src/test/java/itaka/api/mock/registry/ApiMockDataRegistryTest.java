package itaka.api.mock.registry;

import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import itaka.api.mock.bean.RequestParam;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by isao on 2016/07/09.
 */
public class ApiMockDataRegistryTest {

    private static final String TEST_ID = "testId";

    private static final ApiMockRequest TEST_REQUEST = new ApiMockRequest()
            .setEndpoint("/test/hoge").setMethod("POST")
            .setBody("{}")
            .setParams(Arrays.asList(new RequestParam("key1", "value1")));

    @Test
    public void testRegister() {
        String defaultBody = "{\"message\" : \"request is mocked, but response is not defined.}\"";
        ApiMockResponse response = new ApiMockResponse()
                .setBody(defaultBody).setHttpStatus(200);
        ApiMockData expected = new ApiMockData(TEST_ID, TEST_REQUEST, response);

        ApiMockDataRegistry.register(TEST_ID, TEST_REQUEST);

        assertThat(ApiMockDataRegistry.get(TEST_ID).get()).isEqualTo(expected);
    }

    @Test
    public void testUpdateResponse() {
        ApiMockResponse expected = new ApiMockResponse()
                .setBody("update").setHttpStatus(200);
        ApiMockDataRegistry.register(TEST_ID, TEST_REQUEST);

        ApiMockDataRegistry.updateResponse(TEST_ID, expected);

        assertThat(ApiMockDataRegistry.get(TEST_ID).get()).extracting("response").contains(expected);
    }

    @Test
    public void testIsExists_exist() {
        ApiMockDataRegistry.register(TEST_ID, new ApiMockRequest());

        assertThat(ApiMockDataRegistry.get(TEST_ID)).isPresent();
    }

    @Test
    public void testIsExists_notExist() {
        ApiMockDataRegistry.register(TEST_ID, new ApiMockRequest());

        assertThat(ApiMockDataRegistry.get(TEST_ID + "hoge")).isEmpty();
    }
}