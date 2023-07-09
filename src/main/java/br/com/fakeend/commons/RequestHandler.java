package br.com.fakeend.commons;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.HandlerMapping;

@Getter
@Setter
public class RequestHandler {

    @Getter(AccessLevel.NONE)
    private static final String API_HOST = "/fakeend/";
    @Getter(AccessLevel.NONE)
    private static final String ENDPOINT_PURGE = "/purge-all";
    @Getter(AccessLevel.NONE)
    private final HttpServletRequest request;

    private String path;
    private Integer id;
    private boolean purgeAll;
    private String requestURL;

    public RequestHandler(HttpServletRequest request) {
        this.request = request;
        create();
    }

    private void create() {
        String fullPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        int pathId = Constants.ID_PATH_DEFAULT;
        String pathBuilder;

        String[] splitPath = fullPath.split(API_HOST);
        String endpointPath = splitPath[1];

        // verify path ends with number
        if (!endpointPath.matches("^.+?\\d$")) {
            if (endpointPath.endsWith(ENDPOINT_PURGE)) {
                pathBuilder = endpointPath.replace(ENDPOINT_PURGE, "");
                this.purgeAll = true;
            } else {
                pathBuilder = endpointPath;
            }
        } else {
            // remove number from string
            pathBuilder = endpointPath.replaceAll("\\d", "");
            // remove last caracter
            pathBuilder = pathBuilder.replaceFirst(".$", "");
            // get id in path
            pathId = Integer.parseInt(endpointPath.replaceAll("\\D+", ""));
        }

        this.path = pathBuilder;
        this.id = pathId;
        this.requestURL = request.getRequestURL().toString();
    }
}
