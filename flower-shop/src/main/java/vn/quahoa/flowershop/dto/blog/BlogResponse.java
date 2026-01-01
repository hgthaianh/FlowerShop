package vn.quahoa.flowershop.dto.blog;

import java.time.LocalDateTime;

import lombok.Data;
import vn.quahoa.flowershop.model.Blog;
import vn.quahoa.flowershop.model.Blog.BlogStatus;

@Data
public class BlogResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String summary;
    private BlogStatus status;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BlogResponse fromEntity(Blog blog) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setContent(blog.getContent());
        response.setImageUrl(blog.getImageUrl());
        response.setSummary(blog.getSummary());
        response.setStatus(blog.getStatus());
        response.setCreatedAt(blog.getCreatedAt());
        response.setUpdatedAt(blog.getUpdatedAt());
        
        if (blog.getAuthor() != null) {
            response.setAuthorId(blog.getAuthor().getId());
            response.setAuthorUsername(blog.getAuthor().getUsername());
        }
        
        return response;
    }
}
