package br.com.fakeend.commons;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.web.servlet.HandlerMapping;

@Getter
public class RequestHandler {

    private static final String API_HOST = "/fakeend/";
    private static final String ENDPOINT_PURGE = "/purge-all";
    private String path;
    private Integer id;
    private boolean purgeAll;

    @Getter(AccessLevel.NONE)
    private final HttpServletRequest request;

    public RequestHandler(HttpServletRequest request) {
        this.request = request;
        create();
    }

    private void create() {
        String fullPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        String path;
        int idPath = Constants.ID_PATH_DEFAULT;

        String[] splitPath = fullPath.split(API_HOST);
        String endpointPath = splitPath[1];

        // verify path ends with number
        if (!endpointPath.matches("^.+?\\d$")) {
            if (endpointPath.endsWith(ENDPOINT_PURGE)) {
                path = endpointPath.replace(ENDPOINT_PURGE, "");
                this.purgeAll = true;
            } else {
                path = endpointPath;
            }
        } else {
            // remove number from string
            path = endpointPath.replaceAll("\\d", "");
            // remove last caracter
            path = path.replaceFirst(".$", "");
            // get id in path
            idPath = Integer.parseInt(endpointPath.replaceAll("\\D+", ""));
        }

        this.path = path;
        this.id = idPath;
    }
}
