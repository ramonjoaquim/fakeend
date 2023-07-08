package br.com.fakeend.controller;

import br.com.fakeend.dto.EndpointDTO;
import br.com.fakeend.dto.TimeoutDTO;
import br.com.fakeend.model.Endpoint;
import br.com.fakeend.repository.EndpointExtensionRepository;
import br.com.fakeend.repository.EndpointRepository;
import com.mongodb.client.result.UpdateResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(
        value = "fakeend/api/endpoint",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
public class EndpointController {

    private final EndpointRepository repository;
    private final EndpointExtensionRepository endpointExtensionRepository;

    public EndpointController(EndpointRepository repository, EndpointExtensionRepository endpointExtensionRepository) {
        this.repository = repository;
        this.endpointExtensionRepository = endpointExtensionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Endpoint>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping(path = "create")
    public ResponseEntity<Endpoint> createEndpoint(@Valid @RequestBody EndpointDTO dto) {
        if (isEndpointExists(dto)) {
            String messageConflict = MessageFormat.format("Endpoint: {0} or path: {1} already exists", dto.getName(), dto.getPath());
            throw new ResponseStatusException(HttpStatus.CONFLICT, messageConflict);
        }

        Endpoint endpoint = new Endpoint(dto.getName(), dto.getPath(), dto.getTimeout());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.insert(endpoint));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> deleteEndpoint(@PathVariable("id") String id) {
        Optional<Endpoint> endpoint = repository.findById(id);
        if (endpoint.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint not exists");
        }

        repository.delete(endpoint.get());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping(path = "{id}/timeout")
    public ResponseEntity<Object> timeout(@PathVariable("id") String id, @RequestBody TimeoutDTO dto) {
        UpdateResult patch = endpointExtensionRepository.patchEndpoint(id, dto);
        if (patch.getModifiedCount() == 0) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Endpoint with id " + id + " not found.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(repository.findById(id));
    }

    private boolean isEndpointExists(EndpointDTO dto) {
        return !repository.findByNameOrPath(dto.getName(), dto.getPath()).isEmpty();
    }
}
