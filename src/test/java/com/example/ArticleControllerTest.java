package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTest {

    private static final String API = "/api/articles";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn200OkWhenNotModifiedSinceHeaderIsOld() throws Exception {
        // Given
        String oldDate = Utils.formatInstantToRFC1123(
                Instant.now().minus(Duration.ofDays(1))
        );

        // When & Then
        mockMvc.perform(get(API + "/1")
                        .header(HttpHeaders.IF_MODIFIED_SINCE, oldDate))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("My First Article"));
    }

    @Test
    void shouldReturn304NotModifiedWhenNotModifiedSinceHeaderIsRecent() throws Exception {
        // Given
        String recentDate = Utils.formatInstantToRFC1123(
                Instant.now().plus(Duration.ofMinutes(1))
        );

        // When & Then
        mockMvc.perform(get(API + "/1")
                        .header(HttpHeaders.IF_MODIFIED_SINCE, recentDate))
                .andExpect(status().isNotModified());
    }

    @Test
    void shouldReturn200OkWhenNoIfModifiedSinceHeader() throws Exception {
        // No If-Modified-Since header

        mockMvc.perform(get(API + "/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn304AfterUpdatingArticle() throws Exception {
        // Step 1: Get the article (store Last-Modified)
        MvcResult getResult = mockMvc.perform(get(API + "/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
                .andReturn();
        String lastModified = getResult.getResponse().getHeader(HttpHeaders.LAST_MODIFIED);

        // Step 2: Update the article
        ArticleController.Article updatedArticle = new ArticleController.Article(
                1L,
                "My First Article (Updated)",
                "Updated content here",
                Instant.now()
        );
        mockMvc.perform(put(API + "/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedArticle)))
                .andExpect(status().isOk());

        // Step 3: Get the new Last-Modified from response
        MvcResult updatedGetResult = mockMvc.perform(get(API + "/1")
                        .header(HttpHeaders.IF_MODIFIED_SINCE, lastModified))
                .andExpect(status().isOk())
                .andReturn();
        String newLastModified = updatedGetResult.getResponse().getHeader(HttpHeaders.LAST_MODIFIED);

        // Step 4: Fetch again using the NEW Last-Modified -> should return 304 Not Modified
        mockMvc.perform(get(API + "/1")
                        .header(HttpHeaders.IF_MODIFIED_SINCE, newLastModified))
                .andExpect(status().isNotModified());
    }
}
