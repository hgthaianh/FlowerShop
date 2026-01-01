package vn.quahoa.flowershop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.quahoa.flowershop.model.Blog;
import vn.quahoa.flowershop.model.Blog.BlogStatus;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByStatus(BlogStatus status);
    
    List<Blog> findByAuthor_Id(Long authorId);
    
    @Query("SELECT b FROM Blog b WHERE b.status = :status AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Blog> searchPublishedBlogs(@Param("status") BlogStatus status, @Param("keyword") String keyword);
}
