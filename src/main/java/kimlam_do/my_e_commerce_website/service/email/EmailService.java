package kimlam_do.my_e_commerce_website.service.email;

public interface EmailService {
    public void sendEmail(String from, String to, String subject, String text);
}