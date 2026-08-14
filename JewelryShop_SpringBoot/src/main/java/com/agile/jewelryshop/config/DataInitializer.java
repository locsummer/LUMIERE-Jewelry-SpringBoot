package com.agile.jewelryshop.config;

import com.agile.jewelryshop.model.*;
import com.agile.jewelryshop.repository.*;
import com.agile.jewelryshop.util.SlugUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AppSettingRepository appSettingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CategoryRepository categoryRepository,
                           ProductRepository productRepository, OrderRepository orderRepository,
                           AppSettingRepository appSettingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.appSettingRepository = appSettingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        migrateCancelledPayments();
        if (categoryRepository.findBySlug(SlugUtils.toSlug("Nhẫn")).isEmpty()
                || appSettingRepository.findById("jewelry-seed-version").map(s -> !"1".equals(s.getValue())).orElse(true)) {
            seedProducts();
            appSettingRepository.save(new AppSetting("jewelry-seed-version", "1"));
        }
    }

    private void migrateCancelledPayments() {
        var orders = orderRepository.findByStatusAndPaymentStatus(OrderStatus.CANCELLED, PaymentStatus.PENDING);
        orders.forEach(order -> order.setPaymentStatus(PaymentStatus.CANCELLED));
        orderRepository.saveAll(orders);
    }

    private void seedUsers() {
        createUserIfMissing("Quản trị LUMIÈRE", "admin@lumiere.vn", "admin123", "0900000001", "Hà Nội", Role.ADMIN);
        createUserIfMissing("Nhân viên cửa hàng", "staff@lumiere.vn", "staff123", "0900000002", "Hà Nội", Role.STAFF);
        createUserIfMissing("Khách hàng mẫu", "user@lumiere.vn", "user123", "0900000003", "Hà Nội", Role.CUSTOMER);
    }

    private void createUserIfMissing(String name, String email, String password, String phone, String address, Role role) {
        if (userRepository.existsByEmailIgnoreCase(email)) return;
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        userRepository.save(user);
    }

    private void seedProducts() {
        Map<String, Category> c = new LinkedHashMap<>();
        c.put("rings", getOrCreateCategory("Nhẫn", "Nhẫn vàng, bạc và nhẫn đính đá tinh tế"));
        c.put("necklaces", getOrCreateCategory("Dây chuyền", "Dây chuyền thanh lịch cho mọi phong cách"));
        c.put("earrings", getOrCreateCategory("Bông tai", "Bông tai nhỏ xinh và thiết kế statement"));
        c.put("bracelets", getOrCreateCategory("Vòng tay", "Vòng tay và lắc tay sang trọng"));
        c.put("sets", getOrCreateCategory("Bộ trang sức", "Những set trang sức đồng điệu, nổi bật"));
        c.put("gifts", getOrCreateCategory("Quà tặng", "Món quà tinh tế dành cho người đặc biệt"));

        add("Nhẫn vàng 18K Halo", "Nhẫn vàng 18K thiết kế halo thanh lịch, điểm nhấn đá CZ lấp lánh.", 2890000, "halo-ring.svg", c.get("rings"), true);
        add("Nhẫn bạc Minimal", "Nhẫn bạc 925 kiểu dáng tối giản, dễ phối đồ hằng ngày.", 690000, "minimal-ring.svg", c.get("rings"), true);
        add("Nhẫn đính đá Sapphire", "Sapphire xanh nổi bật trên nền bạc 925 mạ rhodium sang trọng.", 3590000, "sapphire-ring.svg", c.get("rings"), false);
        add("Dây chuyền Pearl Drop", "Dây chuyền mảnh kết hợp ngọc trai nuôi, vẻ đẹp nữ tính và tinh tế.", 2290000, "pearl-necklace.svg", c.get("necklaces"), true);
        add("Dây chuyền Heart Gold", "Mặt dây trái tim vàng 14K nhỏ gọn, phù hợp làm quà tặng.", 3190000, "heart-necklace.svg", c.get("necklaces"), true);
        add("Dây chuyền Tennis Shine", "Dây chuyền đá sáng chạy quanh cổ, tạo hiệu ứng sang trọng.", 4290000, "tennis-necklace.svg", c.get("necklaces"), false);
        add("Bông tai Pearl Stud", "Bông tai ngọc trai cổ điển, nhẹ và dễ đeo mỗi ngày.", 1290000, "pearl-earrings.svg", c.get("earrings"), true);
        add("Bông tai Gold Drop", "Bông tai vàng 14K dáng giọt nước, tinh giản nhưng nổi bật.", 2490000, "gold-earrings.svg", c.get("earrings"), false);
        add("Bông tai Crystal Star", "Thiết kế ngôi sao đính đá pha lê, trẻ trung và bắt sáng.", 990000, "star-earrings.svg", c.get("earrings"), true);
        add("Lắc tay Gold Chain", "Lắc tay vàng 18K mắt xích nhỏ, thanh lịch và bền đẹp.", 3790000, "gold-bracelet.svg", c.get("bracelets"), true);
        add("Vòng tay Pearl Charm", "Vòng tay ngọc trai phối charm vàng, tạo điểm nhấn dịu dàng.", 1890000, "pearl-bracelet.svg", c.get("bracelets"), false);
        add("Vòng tay Silver Twist", "Vòng tay bạc 925 xoắn mềm, phong cách hiện đại.", 1190000, "silver-bracelet.svg", c.get("bracelets"), false);
        add("Set Classic Romance", "Bộ dây chuyền và bông tai đồng bộ, phù hợp tiệc cưới và dịp đặc biệt.", 4990000, "classic-set.svg", c.get("sets"), true);
        add("Set Golden Bloom", "Bộ trang sức lấy cảm hứng từ cánh hoa, tinh tế và nữ tính.", 5690000, "bloom-set.svg", c.get("sets"), true);
        add("Set Silver Elegance", "Set bạc 925 tối giản gồm vòng tay và dây chuyền.", 2790000, "silver-set.svg", c.get("sets"), false);
        add("Hộp quà LUMIÈRE", "Hộp quà cao cấp kèm thiệp viết tay, hoàn thiện trải nghiệm tặng quà.", 290000, "gift-box.svg", c.get("gifts"), true);
        add("Charm Letter A", "Charm chữ cái bằng bạc 925, cá nhân hóa vòng tay hoặc dây chuyền.", 490000, "letter-charm.svg", c.get("gifts"), false);
    }

    private Category getOrCreateCategory(String name, String description) {
        String slug = SlugUtils.toSlug(name);
        return categoryRepository.findBySlug(slug).orElseGet(() -> {
            Category category = new Category();
            category.setName(name);
            category.setSlug(slug);
            category.setDescription(description);
            category.setActive(true);
            return categoryRepository.save(category);
        });
    }

    private void add(String name, String description, long price, String image,
                     Category category, boolean featured) {
        String slug = SlugUtils.toSlug(name);
        var existing = productRepository.findBySlug(slug);
        if (existing.isPresent()) {
            Product p = existing.get();
            p.setImage("/images/jewelry/" + image);
            p.setFeatured(featured);
            p.setAvailable(true);
            p.setStock(50);
            productRepository.save(p);
            return;
        }
        Product p = new Product();
        p.setName(name);
        p.setSlug(slug);
        p.setDescription(description);
        p.setPrice(BigDecimal.valueOf(price));
        p.setDiscountPercent(featured ? 10 : 0);
        p.setImage("/images/jewelry/" + image);
        p.setCategory(category);
        p.setAvailable(true);
        p.setFeatured(featured);
        p.setPreparationMinutes(1);
        p.setStock(50);
        productRepository.save(p);
    }
}
