package com.cmpt213.finalProject.SYNC.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cmpt213.finalProject.SYNC.models.UserModel;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendOtpToMailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;
    
    public void sendOtpService(String email, String siteURL, UserModel user){
        //send otp to email
        //String otp = generateotp();
        //String token = generateToken();

        // UserOTP userOTP = new UserOTP(email, otp);
        // userOTPRepository.save(userOTP);

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Verify your Sync account");

            //String verificationLink = "http://localhost:8080/login" + token;
            String emailContent = "<p>welcome to SYNC!</p>"
                + "<p>Please verify your email address by clicking the link below:</p>"
                + "<p><a href=\"[[URL]]\">Verify Email</a></p>"
                + "<p>Thank you!</p>";

            String verificationLink = siteURL + "/verify?code=" + user.getToken();
            emailContent = emailContent.replace("[[URL]]", verificationLink);
            mimeMessageHelper.setText(emailContent, true);
            //mimeMessageHelper.setText("Your OTP is " + otp);
            javaMailSender.send(mimeMessage);
        }catch(MessagingException e){
            throw new RuntimeException("unable to sendotp");
        }
    }

    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
    
    // private String generateotp(){
    //     //generate otp
    //     SecureRandom random = new SecureRandom();
    //     int otp = 100000 + random.nextInt(900000);
    //     return String.valueOf(otp);
    // }

    // private void sendOtpToMail(String email, String otp) throws MessagingException {
    //     MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    //     MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
    //     mimeMessageHelper.setFrom(sender);
    //     mimeMessageHelper.setTo(email);
    //     mimeMessageHelper.setSubject("OTP");
    //     mimeMessageHelper.setText("Your OTP is " + otp);
    //     javaMailSender.send(mimeMessage);
    // }

    // public boolean verifyOTP(String email, String otp) {
    //     //return sentCode.equals(userEnteredCode);
    //     Optional<UserOTP> userOTPOptional = userOTPRepository.findByEmail(email);
    //     if(userOTPOptional.isPresent()){
    //         UserOTP userOTP = userOTPOptional.get();
    //         if(userOTP.getOtp().equals(otp)){
    //             userOTPRepository.delete(userOTP);
    //             return true;
    //             }
    //     }
    //     return false;
    // }
}
