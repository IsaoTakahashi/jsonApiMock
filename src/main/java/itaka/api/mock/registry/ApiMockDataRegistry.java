package itaka.api.mock.registry;

import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by isao on 2016/07/09.
 */
@Slf4j
public class ApiMockDataRegistry {
    private static final Map<String, ApiMockData> DATA_MAP = new HashMap<>();

    private static void register(ApiMockData apiMockData) {
        DATA_MAP.put(apiMockData.getId(), apiMockData);
    }

    public static void register(String id, ApiMockRequest request) {
        log.info("register mock data with id = {}, request= {}", id, request);

        String defaultBody = "{\"message\" : \"request is mocked, but response is not defined.}\"";
        ApiMockResponse response = new ApiMockResponse()
                .setBody(defaultBody).setHttpStatus(200);

        register(new ApiMockData(id, request, response));
    }

    public static void updateResponse(String id, ApiMockResponse response) {
        log.info("update response of {} with {}", id, response);

        DATA_MAP.computeIfPresent(id, (key, value) -> value.setResponse(response));
    }

    public static Optional<ApiMockData> get(String id) {
        return Optional.ofNullable(DATA_MAP.get(id));
    }

    public static boolean isExists(String id) {
        return get(id).isPresent();
    }
}
