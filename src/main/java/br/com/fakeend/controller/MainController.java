package br.com.fakeend.controller;

import br.com.fakeend.commons.Constants;
import br.com.fakeend.model.Content;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.model.EndpointContent;
import br.com.fakeend.repository.EndpointContentExtensionRepository;
import br.com.fakeend.repository.EndpointContentRepository;
import br.com.fakeend.repository.EndpointRepository;
import com.mongodb.client.result.UpdateResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.util.*;

import static java.lang.Integer.parseInt;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = "/fakeend", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    private final EndpointRepository endpointRepository;
    private final EndpointContentRepository contentRepository;

    private final EndpointContentExtensionRepository eceRepository;

    public MainController(EndpointRepository endpointRepository,
                          EndpointContentRepository contentRepository,
                          EndpointContentExtensionRepository eceRepository) {
        this.endpointRepository = endpointRepository;
        this.contentRepository = contentRepository;
        this.eceRepository = eceRepository;
    }

    @RequestMapping(
            value = "/**",
            method = {POST, GET, PUT, DELETE, PATCH}
    )
    public ResponseEntity<?> handler(@RequestBody(required = false) Map<String, Object> body,
                                     HttpServletRequest request) throws Exception {
        var pathHandler = getPathHandler(request);
        return switch (valueOf(request.getMethod())) {
            case GET -> getHandler(pathHandler);
            case DELETE -> deleteHandler(pathHandler);
            case POST -> postHandler(pathHandler, body);
            case PUT -> putHandler(pathHandler, body);
            case PATCH -> patchHandler(pathHandler, body);
            default ->
                    throw new Exception("Oops, something is wrong. By the way, look if your problem are in our GitHub issues: https://github.com/ramonjoaquim/fakeend/issues");
        };
    }

    private ResponseEntity<?> getHandler(Map<String, Object> pathHandler) {
        Endpoint endpoint = endpointRepository.findByName((String) pathHandler.get(Constants.PATH));
        validateRequest(endpoint, null, false);

        boolean isGetAll = Objects.equals(pathHandler.get(Constants.ID_PATH), Constants.ID_PATH_DEFAULT);

        if (isGetAll) {
            List<EndpointContent> contents = contentRepository.findByName(endpoint.name());
            return ResponseEntity.ok(contents);
        } else {
            Integer contentId = (Integer) pathHandler.get(Constants.ID_PATH);
            EndpointContent content = contentRepository.findByEndpointNameAndContentId(endpoint.name(), contentId);

            return ResponseEntity.ok(content);
        }
    }

    private ResponseEntity<?> postHandler(Map<String, Object> pathHandler, Map<String, Object> body) {
        if (!Objects.equals(pathHandler.get(Constants.ID_PATH), Constants.ID_PATH_DEFAULT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier not be used in URL in POST method, use PUT/PATCH method instead.");
        }

        Endpoint endpoint = endpointRepository.findByName((String) pathHandler.get(Constants.PATH));
        validateRequest(endpoint, null, false);

        Content content = transformData(endpoint, body);
        EndpointContent insert = new EndpointContent(endpoint.name(), content);

        contentRepository.insert(insert);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(content.body());
    }

    public Integer getLastId(String nameCollection) {
        List<EndpointContent> contents = contentRepository.findByName(nameCollection);
        List<Integer> idsOfContent = new ArrayList<>();
        contents.stream()
                .map(EndpointContent::content)
                .map(Content::id)
                .filter(Objects::nonNull)
                .forEach(idsOfContent::add);

        boolean isFirstContentForCollection = idsOfContent.isEmpty();
        if (isFirstContentForCollection) {
            return 0;
        } else {
            return Collections.max(idsOfContent);
        }
    }

    private Content transformData(Endpoint endpoint, Map<String, Object> body) {
        Integer lastId = getLastId(endpoint.name());
        if (!body.containsKey(Constants.ID)) {
            body.put(Constants.ID, lastId + 1);
        } else {
            body.replace(Constants.ID, lastId + 1);
        }

        return new Content(lastId + 1, body);
    }

    private ResponseEntity<?> putHandler(Map<String, Object> pathHandler, Map<String, Object> body) {
        if (pathHandler.get(Constants.ID_PATH).equals(Constants.ID_PATH_DEFAULT) && !((Boolean) pathHandler.get(Constants.PURGE_ALL))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Identifier path must be informed in URL.");
        }

        Endpoint endpoint = endpointRepository.findByName((String) pathHandler.get(Constants.PATH));
        validateRequest(endpoint, body, true);

        body.put(Constants.ID, pathHandler.get(Constants.ID_PATH));

        UpdateResult target = eceRepository.updateContent((Integer) pathHandler.get(Constants.ID_PATH), body);

        if (target.getModifiedCount() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Record with id " + body.get(Constants.ID) + " not found.");
        }

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> deleteHandler(Map<String, Object> pathHandler) {
        if (pathHandler.get(Constants.ID_PATH).equals(Constants.ID_PATH_DEFAULT) && !((Boolean) pathHandler.get(Constants.PURGE_ALL))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Identifier path must be informed in URL.");
        }

        Endpoint endpoint = endpointRepository.findByName((String) pathHandler.get(Constants.PATH));
        validateRequest(endpoint, null, false);

        boolean isPurgeAll = (Boolean) pathHandler.get(Constants.PURGE_ALL);

        if (isPurgeAll) {
            eceRepository.deleteByEndpointName(endpoint.name());
        } else {
            Integer contentId = (Integer) pathHandler.get(Constants.ID_PATH);
            long deletedCount = eceRepository.deleteByContentId(contentId);
            if (deletedCount == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private ResponseEntity<?> patchHandler(Map<String, Object> pathHandler, Map<String, Object> body) {
        if (pathHandler.get(Constants.ID_PATH).equals(Constants.ID_PATH_DEFAULT) && !((Boolean) pathHandler.get(Constants.PURGE_ALL))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Identifier path must be informed in URL.");
        }

        Endpoint endpoint = endpointRepository.findByName((String) pathHandler.get(Constants.PATH));
        validateRequest(endpoint, body, true);

        body.put(Constants.ID, pathHandler.get(Constants.ID_PATH));

        UpdateResult target = eceRepository.patch((Integer) pathHandler.get(Constants.ID_PATH), body);

        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "%s with id %s not found".formatted(endpoint.name(), pathHandler.get(Constants.ID_PATH)));
        }

        return ResponseEntity.ok()
                .body(contentRepository.findByEndpointNameAndContentId(endpoint.name(), (Integer) pathHandler.get(Constants.ID_PATH)).content().body());
    }

    private void validateRequest(Endpoint endpoint, Map body, boolean bodyRequired) {
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint not exists");
        }

        if (bodyRequired && Objects.isNull(body)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body Request must be informed");
        }
    }

    private Map<String, Object> getPathHandler(HttpServletRequest request) {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String path;
        boolean purgeAll = false;
        int idPath = Constants.ID_PATH_DEFAULT;
        // verify path ends with number
        if (!fullPath.split("/fakeend/")[1].matches("^.+?\\d$")) {
            if (fullPath.split("/fakeend/")[1].endsWith("/purge-all")) {
                path = fullPath.split("/fakeend/")[1].replace("/purge-all", "");
                purgeAll = true;
            } else {
                path = fullPath.split("/fakeend/")[1];
            }
        } else {
            // remove number from string
            path = fullPath.split("/fakeend/")[1].replaceAll("\\d", "");
            // remove last caracter
            path = path.replaceFirst(".$", "");
            // get id in path
            idPath = parseInt(fullPath.split("/fakeend/")[1].replaceAll("\\D+", ""));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Constants.PATH, path);
        result.put(Constants.ID_PATH, idPath);
        result.put(Constants.PURGE_ALL, purgeAll);

        return result;
    }
}
