package com.br.fakeend.controller;

import com.br.fakeend.business.FakeendBusiness;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.br.fakeend.commons.Constants.*;
import static java.lang.Integer.parseInt;

@RestController
@RequestMapping(value = "/fakeend", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class MainController {
  private static final String COLLECTION = "ENDPOINTS";

  @Autowired private final FakeendBusiness business;

  @RequestMapping(
      value = "/**",
      method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
  public ResponseEntity<?> mainSolve(
      @RequestBody(required = false) Map<String, Object> body, HttpServletRequest request) {
    var pathHandler = getPathHandler(request);
    switch (request.getMethod()) {
      case "GET":
        return mainGET(pathHandler);
      case "POST":
        return mainPOST(body, pathHandler);
      case "PUT":
        return mainPUT(body, pathHandler);
      case "DELETE":
        return mainDELETE(pathHandler);
      case "PATCH":
        return mainPATCH(body, pathHandler);
      default:
        throw new RuntimeException("Oops, something is wrong. By the way, look if your problem are in our GitHub issues https://github.com/ramonjoaquim/fakeend/issues");
    }
  }

  private ResponseEntity<?> mainGET(Map<String, Object> pathHandler) {
    Map endpoint = business.getEndpoint(COLLECTION, (String) pathHandler.get(PATH));
    Map<String, Object> resultValidation = validateRequest(endpoint, null, false);

    if (resultValidation.containsKey("Error") || resultValidation.get("Error") != null) {
      return (ResponseEntity<?>) resultValidation.get("Message");
    }

    return Objects.equals(pathHandler.get(ID_PATH), ID_PATH_DEFAULT)
        ? business.getAll(endpoint.get(NAME).toString())
        : business.getById((Integer) pathHandler.get(ID_PATH), endpoint.get(NAME).toString());
  }

  private ResponseEntity<?> mainPOST(Map<String, Object> body, Map<String, Object> pathHandler) {
    if (!Objects.equals(pathHandler.get(ID_PATH), ID_PATH_DEFAULT)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Identifier not be used in URL in POST method, use PUT/PATCH method instead.");
    }

    Map endpoint = business.getEndpoint(COLLECTION, (String) pathHandler.get(PATH));
    Map<String, Object> resultValidation = validateRequest(endpoint, body, true);
    if (resultValidation.containsKey("Error") || resultValidation.get("Error") != null) {
      return (ResponseEntity<?>) resultValidation.get("Message");
    }

    return business.create(endpoint, body, (String) pathHandler.get(PATH), COLLECTION);
  }

  private ResponseEntity<?> mainPUT(Map<String, Object> body, Map<String, Object> pathHandler) {
    if (pathHandler.get(ID_PATH).equals(ID_PATH_DEFAULT)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Identifier path must be informed in URL.");
    }

    Map endpoint = business.getEndpoint(COLLECTION, (String) pathHandler.get(PATH));
    Map<String, Object> resultValidation = validateRequest(endpoint, body, true);
    if (resultValidation.containsKey("Error") || resultValidation.get("Error") != null) {
      return (ResponseEntity<?>) resultValidation.get("Message");
    }

    if (!body.containsKey(ID)) body.put(ID, pathHandler.get(ID_PATH));

    return business.update(body, endpoint.get(NAME).toString());
  }

  private ResponseEntity<?> mainDELETE(Map<String, Object> pathHandler) {
    if (pathHandler.get(ID_PATH).equals(ID_PATH_DEFAULT) && !((Boolean) pathHandler.get(PURGE_ALL))) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Identifier path must be informed in URL.");
    }

    Map endpoint = business.getEndpoint(COLLECTION, (String) pathHandler.get(PATH));
    Map<String, Object> resultValidation = validateRequest(endpoint, null, false);

    if (resultValidation.containsKey("Error") || resultValidation.get("Error") != null) {
      return (ResponseEntity<?>) resultValidation.get("Message");
    }

    return business.delete(
        (Integer) pathHandler.get(ID_PATH),
        endpoint.get(NAME).toString(),
        (Boolean) pathHandler.get(PURGE_ALL));
  }

  private ResponseEntity<?> mainPATCH(Map<String, Object> body, Map<String, Object> pathHandler) {
    if (pathHandler.get(ID_PATH).equals(ID_PATH_DEFAULT)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body("Identifier path must be informed in URL.");
    }

    Map endpoint = business.getEndpoint(COLLECTION, (String) pathHandler.get(PATH));
    Map<String, Object> resultValidation = validateRequest(endpoint, body, true);
    if (resultValidation.containsKey("Error") || resultValidation.get("Error") != null) {
      return (ResponseEntity<?>) resultValidation.get("Message");
    }

    return business.patch((Integer) pathHandler.get(ID_PATH), body, endpoint.get(NAME).toString());
  }
  private Map<String, Object> validateRequest(Map endpoint, Map body, Boolean bodyRequired) {
    Map<String, Object> map = new LinkedHashMap<>();
    if (endpoint.isEmpty()) {
      map.put("Error", true);
      map.put("Message", ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endpoint not found."));
      return map;
    }

    if (bodyRequired && Objects.isNull(body)) {
      map.put("Error", true);
      map.put(
          "Message",
          ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body Request must be informed."));
      return map;
    }

    return map;
  }

  private Map<String, Object> getPathHandler(HttpServletRequest request) {
    String fullPath =
        (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    String path;
    Boolean purgeAll = false;
    int idPath = ID_PATH_DEFAULT;
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
    result.put(PATH, path);
    result.put(ID_PATH, idPath);
    result.put(PURGE_ALL, purgeAll);

    return result;
  }
}
