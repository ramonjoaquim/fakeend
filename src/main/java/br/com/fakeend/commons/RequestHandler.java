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
        this.requestURL = request.getRequestURL().toString();
        this.id = Constants.ID_PATH_DEFAULT;
        String endpointPath = getEndpointPath();

        if (isPathById(endpointPath)) {
            buildPathAndId(endpointPath);
            return;
        }

        if (isPurgeAll(endpointPath)) {
            buildPathAndPurgeAll(endpointPath);
            return;
        }

        this.path = endpointPath;
    }

    private String getEndpointPath() {
        String fullPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        String[] splitPath = fullPath.split(API_HOST);

        return splitPath[1];
    }

    private void buildPathAndId(String path) {
        String pathBuilder;
        // remove number from string
        pathBuilder = path.replaceAll("\\d", "");
        // remove last caracter
        this.path = pathBuilder.replaceFirst(".$", "");
        // get id in path
        this.id = Integer.parseInt(path.replaceAll("\\D+", ""));
    }

    private boolean isPathById(String path) {
        return path.matches("^.+?\\d$");
    }

    private void buildPathAndPurgeAll(String path) {
        this.path = path.replace(ENDPOINT_PURGE, "");
        this.purgeAll = true;
    }

    private boolean isPurgeAll(String path) {
        return path.endsWith(ENDPOINT_PURGE);
    }
}
