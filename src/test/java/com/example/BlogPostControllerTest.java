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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class BlogPostControllerTest {

    public static final String API = "/api/blogposts";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn200OkWhenNoIfNoneMatchHeader() throws Exception {
        mockMvc.perform(get(API + "/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    void shouldReturn304NotModifiedWhenETagMatches() throws Exception {
        // Step 1: Fetch once to get the current ETag
        MvcResult result = mockMvc.perform(get(API + "/1"))
                .andExpect(status().isOk())
                .andReturn();

        String currentETag = result.getResponse().getHeader(HttpHeaders.ETAG);

        // Step 2: Fetch again with If-None-Match using the same ETag
        mockMvc.perform(get(API + "/1")
                        .header(HttpHeaders.IF_NONE_MATCH, currentETag))
                .andExpect(status().isNotModified())
                .andExpect(header().string(HttpHeaders.ETAG, currentETag));
    }

    @Test
    void shouldReturn200OkWhenETagDoesNotMatch() throws Exception {
        // Send a wrong ETag
        mockMvc.perform(get(API + "/1")
                        .header(HttpHeaders.IF_NONE_MATCH, "\"invalid-etag\""))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnPreconditionFailedWhenUpdatingBlogPostWithOldETag() throws Exception {
        // Step 1: GET the blog post to retrieve the initial ETag
        MvcResult getResult = mockMvc.perform(get(API + "/1"))
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andExpect(status().isOk())
                .andReturn();

        String initialETag = getResult.getResponse().getHeader(HttpHeaders.ETAG);

        // Step 2: PUT (update) the blog post normally (simulate content change)
        BlogPostController.BlogPost updatedBlogPost = new BlogPostController.BlogPost(
                1L,
                "My First BlogPost",
                "Updated blog post content"
        );

        MvcResult putResult = mockMvc.perform(put(API + "/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBlogPost)))
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andExpect(status().isOk())
                .andReturn();

        String newETag = putResult.getResponse().getHeader(HttpHeaders.ETAG);

        // Step 3: Attempt to update again using the OLD ETag (stale version)
        BlogPostController.BlogPost conflictingUpdate = new BlogPostController.BlogPost(
                1L,
                "Conflicting Update",
                "Trying to save with an old ETag"
        );

        mockMvc.perform(put(API + "/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_MATCH, initialETag) // stale ETag
                        .content(objectMapper.writeValueAsString(conflictingUpdate)))
                .andExpect(status().isPreconditionFailed()); // Expect 412
    }

}
