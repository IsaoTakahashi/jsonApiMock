package itaka.api.mock.controller;

import itaka.api.mock.logic.ApiSpy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Created by isao on 2016/07/10.
 */
@RestController
@Slf4j
public class SpyController {

    @Autowired
    ApiSpy apiSpy;

    @RequestMapping(value = "spy/**")
    public ResponseEntity<String> spy(HttpServletRequest request) throws URISyntaxException {
        return apiSpy.proxy(request);
    }
}
