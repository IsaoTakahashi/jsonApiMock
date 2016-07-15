package itaka.api.mock.registry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.config.ApiMockProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by isao on 2016/07/15.
 */
@Component
@Slf4j
public class FileManager {

    private ApiMockProperties apiMockProperties;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public FileManager(ApiMockProperties apiMockProperties) {
        this.apiMockProperties = apiMockProperties;
    }

    public Integer save(List<ApiMockData> apiMockDataList) {
        try {
            mapper.writeValue(new File(apiMockProperties.getDataFilePath()), apiMockDataList);
            return apiMockDataList.size();
        } catch (IOException e) {
            log.error("failed to save", e);
            return 0;
        }
    }

    public List<ApiMockData> load() {
        try {
            return mapper.readValue(new File(apiMockProperties.getDataFilePath()),
                    new TypeReference<List<ApiMockData>>() {});
        } catch (IOException e) {
            log.error("failed to load", e);
            return new ArrayList<>();
        }
    }
}
