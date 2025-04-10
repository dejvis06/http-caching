# 📦 Project: HTTP Caching with Spring Boot

This project demonstrates how to implement **HTTP caching mechanisms** like **ETag** and **Last-Modified** using **Spring Boot** controllers.

---

## 📜 Main Code

- **ArticleController**  
  Handles an in-memory article with HTTP caching using **Last-Modified** headers.

- **BlogPostController**  
  Handles a blog post entity with **ETag** validation using **SHA-256** hashing for strong cache validation.

- **CountryController**  
  Returns a list of countries with a **Cache-Control** header (`max-age=1 hour`), simulating server-side caching.

---

## 🧪 Test Code

- **ArticleControllerTest**  
  Tests the `ArticleController` for correct handling of **Last-Modified** and **304 Not Modified** behavior.

- **BlogPostControllerTest**  
  Tests the `BlogPostController` for proper **ETag** generation, validation, and **304 Not Modified** behavior after content updates.

- **CountryControllerTest**  
  Tests the `CountryController` for correct **Cache-Control** headers and HTTP 200 responses.

---

## Blog

Check the blog post for more information: //TODO

---
