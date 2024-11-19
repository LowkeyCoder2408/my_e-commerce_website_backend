package kimlam_do.my_e_commerce_website.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kimlam_do.my_e_commerce_website.model.entity.Brand;
import kimlam_do.my_e_commerce_website.model.entity.Category;
import kimlam_do.my_e_commerce_website.model.entity.Product;
import kimlam_do.my_e_commerce_website.model.entity.ProductImage;
import kimlam_do.my_e_commerce_website.repository.BrandRepository;
import kimlam_do.my_e_commerce_website.repository.CategoryRepository;
import kimlam_do.my_e_commerce_website.repository.ProductRepository;
import kimlam_do.my_e_commerce_website.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findByCurrentPriceBetween(int page, int size, String sortBy, String sortDir, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCurrentPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> getProductsByCategoryId(int page, int size, String sortBy, String sortDir, int categoryId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCategory_Id(categoryId, pageable);
    }

    @Override
    public Page<Product> findProductsByPriceDifferencePrice(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return productRepository.findProductsByPriceDifferencePrice(pageable);
    }

    @Override
    public Page<Product> findByNameContainingAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String productName, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByNameContainingAndCurrentPriceBetween(productName, minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByCategory_AliasAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String categoryAlias, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCategory_AliasAndCurrentPriceBetween(categoryAlias, minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByNameContainingAndCategory_AliasAndCurrentPriceBetween(int page, int size, String sortBy, String sortDir, String productName, String categoryAlias, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByNameContainingAndCategory_AliasAndCurrentPriceBetween(productName, categoryAlias, minPrice, maxPrice, pageable);
    }

    @Override
    public ObjectNode addAProduct(String productName, String categoryName, String brandName, int listedPrice, int currentPrice, int quantity, String operatingSystem, Optional<Float> weight, Optional<Float> length, Optional<Float> width, Optional<Float> height, String shortDescription, String fullDescription, MultipartFile mainImageFile, MultipartFile[] relatedImagesFiles) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        // Kiểm tra tên sản phẩm
        if (productName == null || productName.isEmpty()) {
            response.put("message", "Tên sản phẩm không thể trống");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra giá
        if (listedPrice <= 0 || currentPrice <= 0) {
            response.put("message", "Giá sản phẩm không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra số lượng
        if (quantity <= 0) {
            response.put("message", "Số lượng sản phẩm không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra ảnh chính
        if (mainImageFile == null || mainImageFile.isEmpty()) {
            response.put("message", "Vui lòng tải lên một tệp ảnh chính hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra category và brand
        Category category = categoryRepository.findByName(categoryName);
        Brand brand = brandRepository.findByName(brandName);

        if (category == null) {
            response.put("message", "Danh mục sản phẩm không hợp lệ");
            response.put("status", "error");
            return response;
        }
        if (brand == null) {
            response.put("message", "Thương hiệu không hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra định dạng ảnh chính
        if (!isImageFile(mainImageFile)) {
            response.put("message", "Ảnh chính phải là một file ảnh hợp lệ");
            response.put("status", "error");
            return response;
        }

        // Lưu ảnh chính lên Cloudinary
        Map<String, String> uploadMainImageResult = cloudinaryService.uploadImage(mainImageFile);
        if (uploadMainImageResult == null || !uploadMainImageResult.containsKey("imageUrl")) {
            response.put("message", "Có lỗi khi tải ảnh chính lên Cloudinary");
            response.put("status", "error");
            return response;
        }

        String mainImageUrl = uploadMainImageResult.get("imageUrl");
        String mainImagePublicId = uploadMainImageResult.get("publicId");

        // Tạo sản phẩm mới
        Product product = new Product();
        product.setName(productName);
        product.setListedPrice(listedPrice);
        product.setCurrentPrice(currentPrice);
        product.setDiscountPercent((listedPrice - currentPrice) * 100 / listedPrice);
        product.setQuantity(quantity);
        product.setOperatingSystem(operatingSystem);

        // Cập nhật các thông số kích thước và trọng lượng (Optional)
        product.setWeight(weight.orElse(0.0f));  // Nếu không có giá trị, dùng 0.0
        product.setLength(length.orElse(0.0f));  // Nếu không có giá trị, dùng 0.0
        product.setWidth(width.orElse(0.0f));    // Nếu không có giá trị, dùng 0.0
        product.setHeight(height.orElse(0.0f));  // Nếu không có giá trị, dùng 0.0

        product.setShortDescription(shortDescription);
        product.setFullDescription(fullDescription);
        product.setCategory(category);
        product.setBrand(brand);
        product.setMainImage(mainImageUrl);
        product.setMainImagePublicId(mainImagePublicId);
        product.setCreatedTime(LocalDateTime.now());
        product.setUpdatedTime(LocalDateTime.now());
        product.setEnabled(true);
        product.setSoldQuantity(0);  // Giá trị mặc định có thể thay đổi sau này

        // Kiểm tra số lượng ảnh liên quan (tối đa 5 ảnh)
        if (relatedImagesFiles != null && relatedImagesFiles.length > 5) {
            response.put("message", "Số lượng ảnh liên quan không được vượt quá 5 ảnh");
            response.put("status", "error");
            return response;
        }

        // Kiểm tra và lưu các ảnh liên quan nếu có
        List<ProductImage> productImages = new ArrayList<>();
        if (relatedImagesFiles != null && relatedImagesFiles.length > 0) {
            for (MultipartFile relatedImageFile : relatedImagesFiles) {
                if (relatedImageFile != null && !relatedImageFile.isEmpty()) {
                    // Kiểm tra xem có phải ảnh hợp lệ không
                    if (!isImageFile(relatedImageFile)) {
                        response.put("message", "Có file không phải ảnh trong ảnh liên quan");
                        response.put("status", "error");
                        return response;
                    }

                    // Tải ảnh liên quan lên Cloudinary
                    Map<String, String> uploadRelatedImageResult = cloudinaryService.uploadImage(relatedImageFile);
                    if (uploadRelatedImageResult == null || !uploadRelatedImageResult.containsKey("imageUrl")) {
                        response.put("message", "Có lỗi khi tải ảnh liên quan lên Cloudinary");
                        response.put("status", "error");
                        return response;
                    }

                    String relatedImageUrl = uploadRelatedImageResult.get("imageUrl");
                    String relatedImagePublicId = uploadRelatedImageResult.get("publicId");

                    // Tạo ảnh liên quan
                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(product);
                    productImage.setUrl(relatedImageUrl);
                    productImage.setPublicId(relatedImagePublicId);
                    productImage.setName("Ảnh cho sản phẩm " + product.getName());
                    productImages.add(productImage);
                }
            }
        }

        // Nếu có ảnh liên quan thì lưu ảnh liên quan vào sản phẩm
        if (!productImages.isEmpty()) {
            product.setImages(productImages);
        }

        // Lưu sản phẩm vào cơ sở dữ liệu
        Product savedProduct = productRepository.save(product);

        // Trả về thông báo thành công
        response.put("message", "Sản phẩm đã được thêm thành công");
        response.put("status", "success");

        return response;
    }

    private boolean isImageFile(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("image");
    }
}