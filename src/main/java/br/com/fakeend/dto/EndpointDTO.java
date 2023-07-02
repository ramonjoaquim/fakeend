package br.com.fakeend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EndpointDTO {

    @NotEmpty(message = "name should be informed")
    private String name;

    @NotEmpty(message = "path should be informed")
    private String path;
}
