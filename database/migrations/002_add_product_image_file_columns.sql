-- Migration: Add file_name and file_path columns to product_images table
-- Date: 2025-11-11
-- Description: Support for uploading product images from local machine

ALTER TABLE product_images 
ADD COLUMN IF NOT EXISTS file_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS file_path VARCHAR(500);

-- Update existing records to have file_name from imageUrl
UPDATE product_images 
SET file_name = SUBSTRING(image_url FROM '[^/]+$'),
    file_path = image_url
WHERE file_name IS NULL;
