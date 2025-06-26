package tda.darkarmy.acharwala.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tda.darkarmy.acharwala.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


@Service
public class MailSenderService {

    private final JavaMailSender javaMailSender;


    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(User user, String mailBody) throws MessagingException {

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
        mimeMessageHelper.setFrom("tset4598t@gmail.com");
        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setSubject("Registration Successful");

//        mimeMessageHelper.setText("Hi "+user.getName());

        mimeMessageHelper.setText(mailBody, true);
        javaMailSender.send(mimeMessageHelper.getMimeMessage());
        System.out.println("Mail sent successfully");

    }

    private String generateMailBody(String verificationUrl, User  user){
        return "<h4>Hi " +user.getEmail()+", </h4><br>"+
                "<h5>Please click on below button to verify the user account</h5>"+
                "<a href="+verificationUrl+"><button style='color: 'blue''>Verify Account</button></a><br>"+
                "<h5>Warm Regards</h5>"+
                "<h5>Rateberg</h5>";
    }

    public void sendOtpEmail(String toEmail, String userName, String otpCode) throws MessagingException, IOException, IOException {
        // Load HTML template
        Resource resource = new ClassPathResource("templates/otp-email-template.html");
        String htmlContent = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

        // Replace placeholders
        htmlContent = htmlContent
                .replace("{{USER_NAME}}", userName)
                .replace("{{OTP_CODE}}", otpCode);

        // Create email
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setTo(toEmail);
        helper.setSubject("Your OTP for First-Buy.in Verification");
        helper.setFrom("firstbuyrewards.check@gmail.com");
        helper.setText(htmlContent, true); // true = isHtml

        javaMailSender.send(message);
    }
}
