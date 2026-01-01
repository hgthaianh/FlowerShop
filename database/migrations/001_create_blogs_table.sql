-- Migration: Create blogs table
-- Version: 001
-- Date: 2025-11-10
-- Description: Add blog functionality to Flower Shop

-- Create blogs table
CREATE TABLE IF NOT EXISTS blogs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(255),
    summary VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    author_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    CONSTRAINT fk_blogs_author 
        FOREIGN KEY (author_id) 
        REFERENCES admins(id) 
        ON DELETE SET NULL,
    
    -- Check constraint for status
    CONSTRAINT chk_blogs_status 
        CHECK (status IN ('DRAFT', 'PUBLISHED'))
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_blogs_status ON blogs(status);
CREATE INDEX IF NOT EXISTS idx_blogs_author_id ON blogs(author_id);
CREATE INDEX IF NOT EXISTS idx_blogs_created_at ON blogs(created_at DESC);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_blogs_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_blogs_updated_at
    BEFORE UPDATE ON blogs
    FOR EACH ROW
    EXECUTE FUNCTION update_blogs_updated_at();

-- Add comment to table
COMMENT ON TABLE blogs IS 'Blog posts for Flower Shop website';
COMMENT ON COLUMN blogs.status IS 'Blog status: DRAFT or PUBLISHED';
COMMENT ON COLUMN blogs.author_id IS 'Reference to admin who created the blog';

-- Insert sample blog (optional)
INSERT INTO blogs (title, content, image_url, summary, status, author_id) 
VALUES (
    'Chào mừng đến với Blog của Flower Shop',
    'Đây là bài viết đầu tiên trên blog của chúng tôi. Chúng tôi sẽ chia sẻ những kiến thức hữu ích về cách chăm sóc hoa, chọn hoa phù hợp cho từng dịp, và nhiều mẹo hay khác về hoa.',
    NULL,
    'Bài viết giới thiệu blog Flower Shop',
    'PUBLISHED',
    1
) ON CONFLICT DO NOTHING;
