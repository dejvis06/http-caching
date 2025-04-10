package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    // Simulating an article stored in memory
    private final Article article = new Article(
            1L,
            "My First Article",
            "This is the content.",
            Instant.now()
    );

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(
            @PathVariable Long id,
            @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince
    ) {

        // Parse client header if present
        if (ifModifiedSince != null) {
            Instant clientTime = Utils.parseRFC1123ToInstant(ifModifiedSince);
            if (!article.getLastModified().isAfter(clientTime)) {
                // Not modified
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }
        return ResponseEntity.ok()
                .lastModified(
                        article.getLastModified().toEpochMilli()
                )
                .body(article);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<Void> updateArticle(@PathVariable Long id, @RequestBody Article updated) {
        article.setContent(updated.getContent());
        article.setLastModified(Instant.now()); // Update the last modified time
        return ResponseEntity.ok().build();
    }

    public static class Article {
        private Long id;
        private String title;
        private String content;
        private Instant lastModified;

        public Article(Long id, String title, String content, Instant lastModified) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.lastModified = lastModified;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Instant getLastModified() {
            return lastModified;
        }

        public void setLastModified(Instant lastModified) {
            this.lastModified = lastModified;
        }
    }
}

