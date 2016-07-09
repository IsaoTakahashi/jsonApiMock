package itaka.api.mock.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by isao on 2016/07/09.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApiMockData {
    String id;
    ApiMockRequest request = new ApiMockRequest();
    ApiMockResponse response = new ApiMockResponse();
}
