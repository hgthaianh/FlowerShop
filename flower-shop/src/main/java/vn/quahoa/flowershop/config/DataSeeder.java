package vn.quahoa.flowershop.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quahoa.flowershop.model.Admin;
import vn.quahoa.flowershop.model.Blog;
import vn.quahoa.flowershop.model.Blog.BlogStatus;
import vn.quahoa.flowershop.model.Category;
import vn.quahoa.flowershop.model.Product;
import vn.quahoa.flowershop.repository.AdminRepository;
import vn.quahoa.flowershop.repository.BlogRepository;
import vn.quahoa.flowershop.repository.CategoryRepository;
import vn.quahoa.flowershop.repository.ProductRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BlogRepository blogRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting database seeding...");
            Admin admin = seedAdmin();
            List<Category> categories = seedCategories();
            seedProducts(categories);
            seedBlogs(admin);
            log.info("Database seeding completed.");
        };
    }

    private Admin seedAdmin() {
        Optional<Admin> existing = adminRepository.findByUsername("admin");
        if (existing.isPresent()) {
            log.info("Admin user already exists.");
            return existing.get();
        }

        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        log.info("Seeding Admin user: admin / admin123");
        return adminRepository.save(admin);
    }

    private List<Category> seedCategories() {
        List<Category> categories = categoryRepository.findAll();

        List<String> requiredCategories = Arrays.asList("Tình yêu", "Khai trương", "Chia buồn");
        for (String name : requiredCategories) {
            if (categories.stream().noneMatch(c -> c.getName().equalsIgnoreCase(name))) {
                Category newCat = new Category();
                newCat.setName(name);
                categories.add(categoryRepository.save(newCat));
                log.info("Seeded category: " + name);
            }
        }
        return categories;
    }

    private void seedProducts(List<Category> categories) {
        if (categories.isEmpty()) {
            // Should not happen as we seed them above
            log.info("No categories found, skipping product seeding.");
            return;
        }

        Category birthday = findCategory(categories, "Birthday", "Hoa sinh nhật");
        Category love = findCategory(categories, "Love", "Tình yêu");
        Category wedding = findCategory(categories, "Wedding", "Hoa cưới", "Hoa cưới pastel");
        Category condolence = findCategory(categories, "Condolence", "Chia buồn");
        Category grandOpening = findCategory(categories, "Grand Opening", "Khai trương");

        if (birthday != null) {
            createProduct("P001", "Sunshine Rose Bouquet",
                    "A vibrant bouquet of yellow roses symbolizing friendship and joy, perfect for brightening someone's special day.",
                    450000,
                    "https://images.unsplash.com/photo-1561181286-d3fee7d55364?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    birthday);
            createProduct("P002", "Pink Carnation Delight",
                    "Soft pink carnations arranged with baby's breath, expressing admiration and gratitude.", 350000,
                    "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    birthday);
            createProduct("P006", "Colorful Gerbera Mix",
                    "A cheerful mix of colorful gerberas to celebrate another year of life.", 400000,
                    "https://images.unsplash.com/photo-1599733589046-10c005739ef9?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    birthday);
            createProduct("P007", "Sweet 16 Orchid", "Delicate purple orchids for a sophisticated birthday gift.",
                    850000,
                    "https://images.unsplash.com/photo-1566938064504-a38c53808539?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    birthday);
        }

        if (love != null) {
            createProduct("P003", "Red Romance Roses",
                    "Classic deep red roses wrapped in premium black paper, the ultimate symbol of passionate love.",
                    600000,
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    love);
            createProduct("P004", "Premium Tulip Box",
                    "Elegant purple tulips presented in a luxury box, representing royalty and admiration.", 1200000,
                    "https://images.unsplash.com/photo-1520763185298-1b434c919102?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    love);
            createProduct("P008", "Eternal White Roses", "Pristine white roses signifying innocence and eternal love.",
                    700000,
                    "https://images.unsplash.com/photo-1533616688419-b7a585564566?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    love);
            createProduct("P009", "Heart of Gold Sunflower",
                    "A giant sunflower surrounded by red roses, for a love that is warm and bright.", 550000,
                    "https://images.unsplash.com/photo-1470509037663-253afd7f0f51?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    love);
        }

        if (wedding != null) {
            createProduct("P005", "White Lily Wedding Bouquet",
                    "Pure white lilies and greenery, a classic choice for a beautiful bride.", 1500000,
                    "https://images.unsplash.com/photo-1558280417-ea782f829e93?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    wedding);
            createProduct("P010", "Blush Peony Dream",
                    "Soft blush peonies creating a romantic and dreamy atmosphere for the big day.", 2200000,
                    "https://images.unsplash.com/photo-1563241527-3004b7be025b?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    wedding);
            createProduct("P011", "Rustic Wildflower Bunch",
                    "A natural, hand-tied bouquet of wildflowers for a rustic outdoor wedding.", 900000,
                    "https://images.unsplash.com/photo-1515934751635-c81c6bc9a2d8?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    wedding);
        }

        if (condolence != null) {
            createProduct("P012", "Peaceful White Chrysanthemum",
                    "White chrysanthemums arranged with care to express deep sympathy and peace.", 500000,
                    "https://images.unsplash.com/photo-1606041008023-472dfb5e530f?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    condolence);
            createProduct("P013", "Memory Lane Lilies", "Elegant white lilies to honor the memory of a loved one.",
                    650000,
                    "https://images.unsplash.com/photo-1591886960571-74d63a61f4fa?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    condolence);
        }

        if (grandOpening != null) {
            createProduct("P014", "Prosperity Orchid Pot",
                    "A magnificent pot of yellow orchids symbolizing wealth and prosperity for a new business.",
                    2500000,
                    "https://images.unsplash.com/photo-1572973801824-3f86e5898394?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    grandOpening);
            createProduct("P015", "Victory Sunflower Stand",
                    "A tall stand of vibrant sunflowers representing success and victory.", 1800000,
                    "https://images.unsplash.com/photo-1598284643093-6b320d757d53?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    grandOpening);
            createProduct("P016", "Lucky Bamboo Arrangement",
                    "An artistic arrangement of lucky bamboo to bring good fortune.", 880000,
                    "https://images.unsplash.com/photo-1579766060591-a537f5945899?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                    grandOpening);
        }

        log.info("Seeded products.");
    }

    private void createProduct(String code, String name, String desc, double price, String img, Category cat) {
        Optional<Product> existing = productRepository.findByProductCodeIgnoreCase(code);
        if (existing.isPresent()) {
            return;
        }
        Product p = new Product();
        p.setProductCode(code);
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setImageUrl(img);
        p.setCategory(cat);
        productRepository.save(p);
    }

    private void seedBlogs(Admin author) {
        if (blogRepository.count() > 0) {
            log.info("Blogs already exist.");
            return;
        }

        createBlog("5 Tips to Keep Flowers Fresh", "Here are 5 tips... 1. Change water daily. 2. Cut stems...",
                "How to make your bouquet last longer.",
                "https://images.unsplash.com/photo-1460518451285-97b6aa326961?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                author);
        createBlog("Meaning of different Rose colors", "Red means love, Yellow means friendship...",
                "Understanding the language of flowers.",
                "https://images.unsplash.com/photo-1496062031456-07b8f162a322?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80",
                author);

        log.info("Seeded blogs.");
    }

    private void createBlog(String title, String content, String summary, String img, Admin author) {
        Blog b = new Blog();
        b.setTitle(title);
        b.setContent(content);
        b.setSummary(summary);
        b.setImageUrl(img);
        b.setAuthor(author);
        b.setStatus(BlogStatus.PUBLISHED);
        blogRepository.save(b);
    }

    private Category findCategory(List<Category> categories, String... names) {
        for (String name : names) {
            Category found = categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name)
                            || c.getName().toLowerCase().contains(name.toLowerCase()))
                    .findFirst()
                    .orElse(null);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
