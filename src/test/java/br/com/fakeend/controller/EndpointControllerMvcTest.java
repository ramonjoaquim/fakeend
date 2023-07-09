package br.com.fakeend.controller;

import br.com.fakeend.MvcTestConfiguration;
import br.com.fakeend.model.Endpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@WebMvcTest
class EndpointControllerMvcTest extends MvcTestConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testFindAll() throws Exception {
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(new Endpoint("Endpoint 1", "/path1", 10));
        endpoints.add(new Endpoint("Endpoint 2", "/path2", 20));

        ObjectMapper mapper = new ObjectMapper();
        String expectedContent = mapper.writeValueAsString(endpoints);

        when(repository.findAll()).thenReturn(endpoints);

        mockMvc.perform(MockMvcRequestBuilders.get("/fakeend/api/endpoint")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().json(expectedContent));

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

}
