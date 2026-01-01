package vn.quahoa.flowershop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.quahoa.flowershop.dto.blog.BlogCreateRequest;
import vn.quahoa.flowershop.dto.blog.BlogUpdateRequest;
import vn.quahoa.flowershop.exception.ResourceNotFoundException;
import vn.quahoa.flowershop.model.Admin;
import vn.quahoa.flowershop.model.Blog;
import vn.quahoa.flowershop.model.Blog.BlogStatus;
import vn.quahoa.flowershop.repository.AdminRepository;
import vn.quahoa.flowershop.repository.BlogRepository;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final AdminRepository adminRepository;

    public Blog createBlog(BlogCreateRequest request) {
        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setImageUrl(request.getImageUrl());
        blog.setSummary(request.getSummary());
        blog.setStatus(request.getStatus() != null ? request.getStatus() : BlogStatus.DRAFT);
        
        if (request.getAuthorId() != null) {
            Admin author = adminRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin", request.getAuthorId()));
            blog.setAuthor(author);
        }
        
        return blogRepository.save(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public List<Blog> getPublishedBlogs() {
        return blogRepository.findByStatus(BlogStatus.PUBLISHED);
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", id));
    }

    public List<Blog> getBlogsByAuthor(Long authorId) {
        if (!adminRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Admin", authorId);
        }
        return blogRepository.findByAuthor_Id(authorId);
    }

    public List<Blog> searchBlogs(String keyword) {
        return blogRepository.searchPublishedBlogs(BlogStatus.PUBLISHED, keyword);
    }

    public Blog updateBlog(Long id, BlogUpdateRequest request) {
        Blog blog = getBlogById(id);
        
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setImageUrl(request.getImageUrl());
        blog.setSummary(request.getSummary());
        
        if (request.getStatus() != null) {
            blog.setStatus(request.getStatus());
        }
        
        return blogRepository.save(blog);
    }

    public void deleteBlog(Long id) {
        Blog blog = getBlogById(id);
        blogRepository.delete(blog);
    }

    public Blog publishBlog(Long id) {
        Blog blog = getBlogById(id);
        blog.setStatus(BlogStatus.PUBLISHED);
        return blogRepository.save(blog);
    }

    public Blog unpublishBlog(Long id) {
        Blog blog = getBlogById(id);
        blog.setStatus(BlogStatus.DRAFT);
        return blogRepository.save(blog);
    }
}
