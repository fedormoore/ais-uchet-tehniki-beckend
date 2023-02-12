package ru.moore.AISUchetTehniki.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    private String emailTo;
    private String subject;
    private String message;


    public void send(String emailTo, String subject, String message) {
        this.emailTo = emailTo;
        this.subject = subject;
        this.message = message;

        AnotherRun anotherRun = new AnotherRun();
        Thread childTread = new Thread(anotherRun);
        childTread.start();
    }

    class AnotherRun implements Runnable {

        @Override
        public void run() {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(username);
            mailMessage.setTo(emailTo);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
        }
    }

}
