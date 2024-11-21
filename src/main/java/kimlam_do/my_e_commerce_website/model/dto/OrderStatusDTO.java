package kimlam_do.my_e_commerce_website.model.dto;

public class OrderStatusDTO {
    private String status;
    private String description;

    public OrderStatusDTO(String status, String description) {
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}