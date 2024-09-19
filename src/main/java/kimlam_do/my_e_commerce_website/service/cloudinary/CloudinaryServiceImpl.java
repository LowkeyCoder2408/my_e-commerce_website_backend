package kimlam_do.my_e_commerce_website.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        // Tải lên hình ảnh và lấy kết quả trả về
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Lấy publicId và URL từ kết quả trả về
        String publicId = (String) uploadResult.get("public_id");
        String imageUrl = (String) uploadResult.get("url");

        // Tạo và trả về Map chứa imageUrl và publicId
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("publicId", publicId);

        return response;
    }

    @Override
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}