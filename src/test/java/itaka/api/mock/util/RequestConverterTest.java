package itaka.api.mock.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import itaka.api.mock.bean.RequestParam;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by isao on 2016/07/09.
 */
public class RequestConverterTest {


    @Test
    public void testGetBody(@Mocked MockHttpServletRequest request) throws UnsupportedEncodingException {
        new Expectations() {{
            request.getReader().lines();
            result = ImmutableList.of("aa","ee").stream();
        }};

        assertThat(RequestConverter.getBody(request)).isEqualTo("aaee");
    }

    @Test
    public void testGetParams() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ImmutableMap<String, String> paramMap = ImmutableMap.of("param1", "value1", "name", "John");
        request.setParameters(paramMap);
        List<RequestParam> expected = ImmutableList.of(
                new RequestParam("param1", "value1"),
                new RequestParam("name", "John")
                );

        assertThat(RequestConverter.getParams(request)).isEqualTo(expected);
    }
}