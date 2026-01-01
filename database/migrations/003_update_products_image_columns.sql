-- Migration: Update products table for better image handling
-- Date: 2025-11-11
-- Description: Change image_url to TEXT to support both long URLs and local paths

-- Update products table
ALTER TABLE products 
ALTER COLUMN image_url TYPE TEXT;

-- Update product_images table
ALTER TABLE product_images 
ALTER COLUMN image_url TYPE TEXT;

-- Add comments
COMMENT ON COLUMN products.image_url IS 'Main product image URL or local path';
COMMENT ON COLUMN product_images.image_url IS 'Product image URL or local path';
COMMENT ON COLUMN product_images.file_name IS 'Original or generated file name';
COMMENT ON COLUMN product_images.file_path IS 'Server file system path';
