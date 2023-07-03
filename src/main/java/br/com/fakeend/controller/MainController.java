package br.com.fakeend.controller;

import br.com.fakeend.commons.RequestHandler;
import br.com.fakeend.handler.FakeendException;
import br.com.fakeend.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/fakeend", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    private final GetService getService;
    private final PostService postService;
    private final PutService putService;
    private final PatchService patchService;
    private final DeleteService deleteService;

    public MainController(GetService getService,
                          PostService postService,
                          PutService putService,
                          PatchService patchService,
                          DeleteService deleteService) {
        this.getService = getService;
        this.postService = postService;
        this.putService = putService;
        this.patchService = patchService;
        this.deleteService = deleteService;
    }

    @RequestMapping(
            value = "/**",
            method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}
    )
    public ResponseEntity<Object> handler(@RequestBody(required = false) Map<String, Object> body,
                                     HttpServletRequest request) throws FakeendException {

        RequestHandler requestHandler = new RequestHandler(request);

        return switch (RequestMethod.valueOf(request.getMethod())) {
            case GET -> getService.process(requestHandler);
            case DELETE -> deleteService.process(requestHandler);
            case POST -> postService.process(requestHandler, body);
            case PUT -> putService.process(requestHandler, body);
            case PATCH -> patchService.process(requestHandler, body);
            default ->
                    throw new FakeendException("Oops, something is wrong. By the way, look if your problem are in our GitHub issues: https://github.com/ramonjoaquim/fakeend/issues");
        };
    }
}
