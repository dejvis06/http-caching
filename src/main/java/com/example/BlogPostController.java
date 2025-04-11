package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogposts")
public class BlogPostController {

    // Simulating an blogPost stored in memory
    private BlogPost blogPost = new BlogPost(
            1L,
            "My First BlogPost",
            "This is the content.");

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getBlogPost(
            @PathVariable Long id,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
    ) {
        String currentETag = "\"" + Utils.sha256Hex(
                blogPost.getTitle() + blogPost.getContent()
        ) + "\"";

        if (ifNoneMatch != null && ifNoneMatch.equals(currentETag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(currentETag)
                    .build();
        }

        return ResponseEntity.ok()
                .eTag(currentETag)
                .body(blogPost);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<Void> updateBlogPost(
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @PathVariable Long id,
            @RequestBody BlogPost updated) {

        // Generate the current ETag based on existing content
        String currentETag = "\"" + Utils.sha256Hex(blogPost.getTitle() + blogPost.getContent()) + "\"";

        // If client sends If-Match, check it against current ETag
        if (ifMatch != null && !ifMatch.equals(currentETag)) {
            // Mid-air collision detected -> reject with 412 Precondition Failed
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        blogPost = new BlogPost(
                id,
                updated.getTitle(),
                updated.getContent()
        );
        String newETag = "\"" + Utils.sha256Hex(blogPost.getTitle() + blogPost.getContent()) + "\"";

        return ResponseEntity.ok()
                .eTag(newETag)
                .build();
    }

    public static class BlogPost {
        private final Long id;
        private final String title;
        private String content;

        public BlogPost(Long id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

