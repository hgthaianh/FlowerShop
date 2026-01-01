package vn.quahoa.flowershop.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.quahoa.flowershop.model.Blog.BlogStatus;

@Data
public class BlogCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String imageUrl;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;

    private BlogStatus status = BlogStatus.DRAFT;

    private Long authorId;
}
