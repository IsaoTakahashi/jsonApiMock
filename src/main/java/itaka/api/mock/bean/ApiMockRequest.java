package itaka.api.mock.bean;

import itaka.api.mock.util.RequestConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isao on 2016/07/09.
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApiMockRequest {
    String endpoint;
    String method;
    String body;
    List<RequestParam> params;

    public ApiMockRequest(HttpServletRequest request,String path) {
        this.endpoint = request.getServletPath().replaceFirst(path,"");
        this.method = request.getMethod();
        this.body = RequestConverter.getBody(request);
        this.params = RequestConverter.getParams(request);
    }

    public String hash() {
        String paramString = params.stream()
                .map(p -> String.format("[%s:%s]", p.getKey(), p.getValue()))
                .collect(Collectors.joining(","));

        String identifiedString = new StringBuilder()
                .append(endpoint)
                .append(method)
                .append(body.replaceAll("\\s",""))
                .append(paramString)
                .toString();

        return DigestUtils.md5DigestAsHex(identifiedString.getBytes());
    }
}
