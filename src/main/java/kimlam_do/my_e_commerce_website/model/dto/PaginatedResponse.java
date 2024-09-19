package kimlam_do.my_e_commerce_website.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> _embedded;

    private int totalPages;

    private long totalElements;
}