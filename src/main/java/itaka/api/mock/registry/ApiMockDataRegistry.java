package itaka.api.mock.registry;

import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.bean.ApiMockRequest;
import itaka.api.mock.bean.ApiMockResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * Created by isao on 2016/07/09.
 */
@Slf4j
@Component
public class ApiMockDataRegistry {
    private final Map<String, ApiMockData> dataMap = new HashMap<>();

    private FileManager fileManager;

    @Autowired
    public ApiMockDataRegistry(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public String register(ApiMockData apiMockData) {
        String key = apiMockData.getId();
        if (StringUtils.isBlank(key)) {
            key = apiMockData.getRequest().hash();
        }

        log.info("register mock data wiht id = {}, data = {}", key, apiMockData);
        dataMap.put(key, apiMockData);

        return key;
    }

    public String register(String id, ApiMockRequest request) {
        log.info("register mock data with id = {}, request = {}", id, request);

        String defaultBody = "{\"message\" : \"request is mocked, but response is not defined.}\"";
        ApiMockResponse response = new ApiMockResponse()
                .setBody(defaultBody).setHttpStatus(200);

        return register(new ApiMockData(id, request, response));
    }

    public void updateResponse(String id, ApiMockResponse response) {
        log.info("update response of {} with {}", id, response);

        dataMap.computeIfPresent(id, (key, value) -> value.setResponse(response));
    }

    public Optional<ApiMockData> get(String id) {
        return Optional.ofNullable(dataMap.get(id));
    }

    public List<ApiMockData> getList() {
        return dataMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .sorted(comparing(data -> data.getRequest().getEndpoint()))
                .collect(Collectors.toList());
    }

    public boolean isExists(String id) {
        return get(id).isPresent();
    }

    public Integer save(List<ApiMockData> apiMockDataList) {
        return fileManager.save(apiMockDataList);
    }

    public Integer load() {
        List<ApiMockData> loadedDataList = fileManager.load();

        loadedDataList.forEach(data -> {
            data.setId(data.getRequest().hash());
            register(data);
        });

        return loadedDataList.size();
    }
}
