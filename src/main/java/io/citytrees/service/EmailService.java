package io.citytrees.service;

import io.citytrees.configuration.properties.EmailProperties;
import io.citytrees.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailProperties emailProperties;

    @SneakyThrows
    public void send(EmailMessage message) {
        Properties prop = new Properties();
        prop.putAll(emailProperties.getSmtpProperties());

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getUser(), emailProperties.getPassword());
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(emailProperties.getSenderEmail()));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(message.getAddress())});
        msg.setSentDate(new Date());
        msg.setSubject(message.getSubject());
        msg.setContent(message.getText(), "text/html; charset=utf-8");

        Transport.send(msg);
    }
}
