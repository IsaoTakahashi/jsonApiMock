package itaka.api.mock.util;

import itaka.api.mock.bean.RequestParam;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by isao on 2016/07/09.
 */
@Slf4j
public class RequestConverter {

    public static String getBody(HttpServletRequest request) {
        try {
            return request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            log.error("failed to parse request body", e);
            return "";
        }
    }

    public static List<RequestParam> getParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .map(entry -> new RequestParam(entry.getKey(), Stream.of(entry.getValue()).collect(Collectors.joining(","))))
                .collect(Collectors.toList());
    }
}
