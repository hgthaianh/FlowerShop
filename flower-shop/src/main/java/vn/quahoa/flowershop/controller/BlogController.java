package vn.quahoa.flowershop.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.quahoa.flowershop.dto.blog.BlogCreateRequest;
import vn.quahoa.flowershop.dto.blog.BlogResponse;
import vn.quahoa.flowershop.dto.blog.BlogUpdateRequest;
import vn.quahoa.flowershop.service.BlogService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/admin/blogs")
    @ResponseStatus(HttpStatus.CREATED)
    public BlogResponse createBlog(@Valid @RequestBody BlogCreateRequest request) {
        return BlogResponse.fromEntity(blogService.createBlog(request));
    }

    @GetMapping("/blogs")
    public List<BlogResponse> getPublicBlogs(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return blogService.searchBlogs(search).stream()
                    .map(BlogResponse::fromEntity)
                    .collect(Collectors.toList());
        }
        return blogService.getPublishedBlogs().stream()
                .map(BlogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/blogs/{id}")
    public BlogResponse getBlog(@PathVariable Long id) {
        return BlogResponse.fromEntity(blogService.getBlogById(id));
    }

    @GetMapping("/admin/blogs")
    public List<BlogResponse> getAllBlogsForAdmin() {
        return blogService.getAllBlogs().stream()
                .map(BlogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/admin/blogs/author/{authorId}")
    public List<BlogResponse> getBlogsByAuthor(@PathVariable Long authorId) {
        return blogService.getBlogsByAuthor(authorId).stream()
                .map(BlogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PutMapping("/admin/blogs/{id}")
    public BlogResponse updateBlog(@PathVariable Long id, @Valid @RequestBody BlogUpdateRequest request) {
        return BlogResponse.fromEntity(blogService.updateBlog(id, request));
    }

    @PatchMapping("/admin/blogs/{id}/publish")
    public BlogResponse publishBlog(@PathVariable Long id) {
        return BlogResponse.fromEntity(blogService.publishBlog(id));
    }

    @PatchMapping("/admin/blogs/{id}/unpublish")
    public BlogResponse unpublishBlog(@PathVariable Long id) {
        return BlogResponse.fromEntity(blogService.unpublishBlog(id));
    }

    @DeleteMapping("/admin/blogs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
    }
}
