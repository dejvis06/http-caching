package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCountriesWithCacheControl() throws Exception {
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("USA"))
                .andExpect(jsonPath("$[1]").value("Germany"))
                .andExpect(jsonPath("$[2]").value("Japan"))
                .andExpect(jsonPath("$[3]").value("Brazil"))
                .andExpect(jsonPath("$[4]").value("Australia"))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "max-age=3600"));
    }
}
