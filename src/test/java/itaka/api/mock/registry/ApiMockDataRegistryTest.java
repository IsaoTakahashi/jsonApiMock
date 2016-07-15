package itaka.api.mock.registry;

import com.google.common.collect.ImmutableList;
import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import itaka.api.mock.bean.RequestParam;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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

    @Mocked
    private FileManager fileManager;

    private ApiMockDataRegistry apiMockDataRegistry;

    @Before
    public void setUp() {
        apiMockDataRegistry = new ApiMockDataRegistry(fileManager);
    }

    @Test
    public void testRegister() {
        String defaultBody = "{\"message\" : \"request is mocked, but response is not defined.}\"";
        ApiMockResponse response = new ApiMockResponse()
                .setBody(defaultBody).setHttpStatus(200);
        ApiMockData expected = new ApiMockData(TEST_ID, TEST_REQUEST, response);

        apiMockDataRegistry.register(TEST_ID, TEST_REQUEST);

        assertThat(apiMockDataRegistry.get(TEST_ID).get()).isEqualTo(expected);
    }

    @Test
    public void testUpdateResponse() {
        ApiMockResponse expected = new ApiMockResponse()
                .setBody("update").setHttpStatus(200);
        apiMockDataRegistry.register(TEST_ID, TEST_REQUEST);

        apiMockDataRegistry.updateResponse(TEST_ID, expected);

        assertThat(apiMockDataRegistry.get(TEST_ID).get()).extracting("response").contains(expected);
    }

    @Test
    public void testIsExists_exist() {
        apiMockDataRegistry.register(TEST_ID, new ApiMockRequest());

        assertThat(apiMockDataRegistry.get(TEST_ID)).isPresent();
    }

    @Test
    public void testIsExists_notExist() {
        apiMockDataRegistry.register(TEST_ID, new ApiMockRequest());

        assertThat(apiMockDataRegistry.get(TEST_ID + "hoge")).isEmpty();
    }

    @Test
    public void testSave() {
        List<ApiMockData> dataList = ImmutableList.of(new ApiMockData());
        new Expectations() {{
            fileManager.save(dataList);
            result = 1;
        }};

        assertThat(apiMockDataRegistry.save(dataList)).isEqualTo(1);
    }

    @Test
    public void testLoad() {
        List<ApiMockData> dataList = ImmutableList.of(new ApiMockData());
        new Expectations() {{
            fileManager.load();
            result = dataList;
        }};

        assertThat(apiMockDataRegistry.load()).isEqualTo(1);
    }
}