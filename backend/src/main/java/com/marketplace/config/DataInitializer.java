package com.marketplace.config;

import com.marketplace.catalog.domain.AttributeType;
import com.marketplace.catalog.domain.Brand;
import com.marketplace.catalog.domain.Category;
import com.marketplace.catalog.domain.CategoryAttribute;
import com.marketplace.catalog.repository.BrandRepository;
import com.marketplace.catalog.repository.CategoryAttributeRepository;
import com.marketplace.catalog.repository.CategoryRepository;
import com.marketplace.coupon.domain.Coupon;
import com.marketplace.coupon.domain.DiscountType;
import com.marketplace.coupon.repository.CouponRepository;
import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.domain.CustomerAddress;
import com.marketplace.customer.repository.CustomerAddressRepository;
import com.marketplace.customer.repository.CustomerRepository;
import com.marketplace.identity.domain.Role;
import com.marketplace.identity.domain.User;
import com.marketplace.identity.domain.UserStatus;
import com.marketplace.identity.repository.RoleRepository;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.inventory.domain.Inventory;
import com.marketplace.inventory.repository.InventoryRepository;
import com.marketplace.product.domain.Product;
import com.marketplace.product.domain.ProductImage;
import com.marketplace.product.domain.ProductStatus;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.product.repository.ProductImageRepository;
import com.marketplace.product.repository.ProductRepository;
import com.marketplace.product.repository.ProductVariantRepository;
import com.marketplace.security.RoleEnum;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.domain.SellerBankAccount;
import com.marketplace.seller.domain.SellerStatus;
import com.marketplace.seller.domain.SellerVerification;
import com.marketplace.seller.domain.VerificationStatus;
import com.marketplace.seller.repository.SellerBankAccountRepository;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.seller.repository.SellerVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final SellerVerificationRepository verificationRepository;
    private final SellerBankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CategoryAttributeRepository attributeRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final InventoryRepository inventoryRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data bootstrap.");
            return;
        }

        log.info("Starting enterprise marketplace seed data initialization...");

        // 1. Seed Roles
        Map<RoleEnum, Role> roles = new EnumMap<>(RoleEnum.class);
        for (RoleEnum roleEnum : RoleEnum.values()) {
            Role r = roleRepository.save(Role.builder()
                    .name(roleEnum.name())
                    .description("System authority: " + roleEnum.name())
                    .build());
            roles.put(roleEnum, r);
        }

        // 2. Seed Super Admin
        User adminUser = User.builder()
                .email("admin@marketplace.com")
                .passwordHash(passwordEncoder.encode("AdminPass123!"))
                .firstName("Platform")
                .lastName("SuperAdmin")
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(Set.of(roles.get(RoleEnum.ROLE_ADMIN), roles.get(RoleEnum.ROLE_SUPER_ADMIN)))
                .build();
        userRepository.save(adminUser);

        // 3. Seed Verified Demo Sellers
        User seller1User = User.builder()
                .email("seller@apex.com")
                .passwordHash(passwordEncoder.encode("SellerPass123!"))
                .firstName("Alex")
                .lastName("Vance")
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(Set.of(roles.get(RoleEnum.ROLE_SELLER)))
                .build();
        userRepository.save(seller1User);

        Seller seller1 = Seller.builder()
                .user(seller1User)
                .businessName("Apex Innovations LLC")
                .storeSlug("apex-innovations")
                .displayName("Apex Tech Flagship")
                .description("Official premier electronics and precision hardware developer.")
                .logoUrl("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=200")
                .bannerUrl("https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200")
                .contactEmail("support@apex-innovations.com")
                .contactPhone("+1 (555) 234-5678")
                .status(SellerStatus.APPROVED)
                .commissionRateOverride(BigDecimal.valueOf(8.50))
                .ratingAverage(BigDecimal.valueOf(4.92))
                .ratingCount(128)
                .build();
        sellerRepository.save(seller1);

        verificationRepository.save(SellerVerification.builder()
                .seller(seller1)
                .legalBusinessName("Apex Innovations LLC")
                .taxIdEin("XX-XXXX7890")
                .businessRegistrationNumber("DE-893274")
                .documentType("CERTIFICATE_OF_INCORPORATION")
                .documentUrl("https://storage.marketplace.internal/docs/apex_kyc.pdf")
                .status(VerificationStatus.APPROVED)
                .reviewedBy(adminUser)
                .reviewedAt(Instant.now())
                .build());

        bankAccountRepository.save(SellerBankAccount.builder()
                .seller(seller1)
                .bankName("JPMorgan Chase")
                .accountHolderName("Apex Innovations LLC")
                .routingNumber("021000021")
                .accountNumberLast4("9012")
                .encryptedAccountToken("tok_ach_apex_9012")
                .primary(true)
                .build());

        // 4. Seed Buyer Customer
        User buyerUser = User.builder()
                .email("buyer@example.com")
                .passwordHash(passwordEncoder.encode("BuyerPass123!"))
                .firstName("Sarah")
                .lastName("Connor")
                .phoneNumber("+1 (555) 987-6543")
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(Set.of(roles.get(RoleEnum.ROLE_CUSTOMER)))
                .build();
        userRepository.save(buyerUser);

        Customer customer = Customer.builder()
                .user(buyerUser)
                .currencyPreference("USD")
                .localePreference("en_US")
                .marketingOptIn(true)
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        CustomerAddress address = CustomerAddress.builder()
                .customer(savedCustomer)
                .addressTitle("Home")
                .recipientName("Sarah Connor")
                .phoneNumber("+1 (555) 987-6543")
                .streetLine1("742 Evergreen Terrace")
                .streetLine2("Apt 4B")
                .city("San Francisco")
                .stateProvince("CA")
                .postalCode("94102")
                .countryCode("US")
                .defaultShipping(true)
                .defaultBilling(true)
                .build();
        addressRepository.save(address);

        // 5. Seed Category Tree
        Category electronics = categoryRepository.save(Category.builder()
                .name("Electronics & Gadgets")
                .slug("electronics")
                .description("Cutting-edge consumer tech, audio, and personal devices")
                .iconUrl("Laptop")
                .imageUrl("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600")
                .path("electronics")
                .level(1)
                .displayOrder(1)
                .commissionRate(BigDecimal.valueOf(8.00))
                .active(true)
                .build());

        Category audio = categoryRepository.save(Category.builder()
                .parent(electronics)
                .name("Audio & Headphones")
                .slug("audio-headphones")
                .description("Studio monitors, wireless noise-canceling headphones, and DACs")
                .iconUrl("Headphones")
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600")
                .path("electronics/audio-headphones")
                .level(2)
                .displayOrder(1)
                .commissionRate(BigDecimal.valueOf(9.00))
                .active(true)
                .build());

        Category fashion = categoryRepository.save(Category.builder()
                .name("Apparel & Fashion")
                .slug("fashion")
                .description("Designer apparel, handcrafted leatherwear, and footwear")
                .iconUrl("Shirt")
                .imageUrl("https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=600")
                .path("fashion")
                .level(1)
                .displayOrder(2)
                .commissionRate(BigDecimal.valueOf(12.00))
                .active(true)
                .build());

        // 6. Seed Brands
        Brand sonyBrand = brandRepository.save(Brand.builder()
                .name("Sony")
                .slug("sony")
                .description("World leader in professional audio, imaging, and gaming.")
                .websiteUrl("https://sony.com")
                .active(true)
                .build());

        Brand appleBrand = brandRepository.save(Brand.builder()
                .name("Apple")
                .slug("apple")
                .description("Innovator in consumer electronics and personal computing.")
                .websiteUrl("https://apple.com")
                .active(true)
                .build());

        // 7. Seed Dynamic Attributes
        attributeRepository.save(CategoryAttribute.builder()
                .category(audio)
                .name("Connectivity")
                .code("connectivity")
                .attributeType(AttributeType.SELECT)
                .required(true)
                .filterable(true)
                .optionsJson("[\"Bluetooth 5.3\", \"3.5mm Wired\", \"USB-C Lossless\"]")
                .build());

        attributeRepository.save(CategoryAttribute.builder()
                .category(audio)
                .name("Noise Cancellation")
                .code("anc")
                .attributeType(AttributeType.BOOLEAN)
                .required(true)
                .filterable(true)
                .build());

        // 8. Seed Master Products & Inventory
        Product headphones = Product.builder()
                .seller(seller1)
                .category(audio)
                .brand(sonyBrand)
                .title("Sony WH-1000XM5 Wireless Noise Canceling Headphones")
                .slug("sony-wh-1000xm5-wireless-headphones")
                .sku("SONY-WH1000XM5-M")
                .shortDescription("Industry-leading noise canceling with two processors and 8 microphones for unprecedented call clarity.")
                .description("Engineered to perfection, the Sony WH-1000XM5 wireless headphones deliver pure silence and unmatched Hi-Res Audio clarity. Featuring Auto NC Optimizer, 30-hour battery life with ultra-fast charging, and touch sensor controls.")
                .basePrice(BigDecimal.valueOf(399.99))
                .compareAtPrice(BigDecimal.valueOf(449.99))
                .currency("USD")
                .status(ProductStatus.ACTIVE)
                .ratingAverage(BigDecimal.valueOf(4.90))
                .ratingCount(84)
                .totalSales(240)
                .build();

        Product savedProduct = productRepository.save(headphones);

        // Product Images
        imageRepository.save(ProductImage.builder()
                .product(savedProduct)
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800")
                .thumbnailUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200")
                .altText("Sony WH-1000XM5 Midnight Black Studio View")
                .displayOrder(1)
                .primary(true)
                .build());

        imageRepository.save(ProductImage.builder()
                .product(savedProduct)
                .imageUrl("https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800")
                .thumbnailUrl("https://images.unsplash.com/photo-1484704849700-f032a568e944?w=200")
                .altText("Sony WH-1000XM5 Silver Edition angled")
                .displayOrder(2)
                .primary(false)
                .build());

        // Variants
        ProductVariant blackVariant = variantRepository.save(ProductVariant.builder()
                .product(savedProduct)
                .sku("SONY-WH1000XM5-BLK")
                .barcode("027242923140")
                .title("Midnight Black")
                .priceAdjustment(BigDecimal.ZERO)
                .attributesJson("{\"color\": \"Midnight Black\", \"finish\": \"Matte\"}")
                .active(true)
                .build());

        inventoryRepository.save(Inventory.builder()
                .variant(blackVariant)
                .onHand(50)
                .reserved(0)
                .lowStockThreshold(5)
                .build());

        ProductVariant silverVariant = variantRepository.save(ProductVariant.builder()
                .product(savedProduct)
                .sku("SONY-WH1000XM5-SLV")
                .barcode("027242923157")
                .title("Platinum Silver")
                .priceAdjustment(BigDecimal.valueOf(10.00))
                .attributesJson("{\"color\": \"Platinum Silver\", \"finish\": \"Satin\"}")
                .active(true)
                .build());

        inventoryRepository.save(Inventory.builder()
                .variant(silverVariant)
                .onHand(35)
                .reserved(0)
                .lowStockThreshold(5)
                .build());

        // 9. Seed Active Coupons
        couponRepository.save(Coupon.builder()
                .code("WELCOME10")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(10.00))
                .minimumCartValue(BigDecimal.valueOf(50.00))
                .maxDiscountCap(BigDecimal.valueOf(50.00))
                .usageLimit(1000)
                .perUserLimit(1)
                .active(true)
                .startsAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .expiresAt(Instant.now().plus(365, ChronoUnit.DAYS))
                .build());

        couponRepository.save(Coupon.builder()
                .code("SUPERDEAL20")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(BigDecimal.valueOf(20.00))
                .minimumCartValue(BigDecimal.valueOf(100.00))
                .usageLimit(500)
                .perUserLimit(1)
                .active(true)
                .startsAt(Instant.now().minus(1, ChronoUnit.DAYS))
                .expiresAt(Instant.now().plus(180, ChronoUnit.DAYS))
                .build());

        log.info("Marketplace initial bootstrap complete! Demo accounts ready:");
        log.info(" -> Admin: admin@marketplace.com / AdminPass123!");
        log.info(" -> Seller: seller@apex.com / SellerPass123!");
        log.info(" -> Buyer: buyer@example.com / BuyerPass123!");
    }
}
