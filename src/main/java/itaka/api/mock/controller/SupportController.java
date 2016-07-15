package itaka.api.mock.controller;

import itaka.api.mock.bean.ApiMockData;
import itaka.api.mock.registry.ApiMockDataRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isao on 2016/07/10.
 */
@RestController
@Slf4j
public class SupportController {

    @Autowired
    ApiMockDataRegistry apiMockDataRegistry;

    @RequestMapping(value = "data", method = RequestMethod.GET)
    public List<ApiMockData> getMockDataList() {
        return apiMockDataRegistry.getList();
    }

    @RequestMapping(value = "data/{id}", method = RequestMethod.GET)
    public ApiMockData getMockData(@PathVariable String id){
        return apiMockDataRegistry.get(id).orElse(new ApiMockData());
    }

    @RequestMapping(value = "data", method = RequestMethod.POST)
    public String setMockData(@RequestBody ApiMockData mockData) {
        return apiMockDataRegistry.register(mockData);
    }

    @RequestMapping(value = "dataList", method = RequestMethod.POST)
    public List<String> setMockDataList(@RequestBody List<ApiMockData> mockDataList) {
        return mockDataList.stream()
                .map(apiMockDataRegistry::register)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String saveMockData() {
        Integer saveResult = apiMockDataRegistry.save(apiMockDataRegistry.getList());
        return "save " + saveResult + " mocks.";
    }
    
    @RequestMapping(value = "load", method = RequestMethod.POST)
    public String loadMockData() {
        Integer loadResult = apiMockDataRegistry.load();
        return "load " + loadResult + " mocks";
    }
}
