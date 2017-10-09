package com.example.justantitheft;


import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Properties;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class EmailSender  {

    String username = "justantitheft@gmail.com";
    String password = "ra12345678";
    String recUser;
    LatLng latLng;
    String file1;
    public EmailSender(String username, String file1, LatLng latLng){
    recUser=username;
        this.latLng=latLng;
        this.file1=file1;
    }
    public void sendEmail(){


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();
        props.put("mail.smtp.user","username");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.EnableSSL.enable","true");

        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recUser));
            String currentLocation="https://www.google.jo/maps/@"+latLng.latitude+","+latLng.longitude+",15z";

            message.setSubject(currentLocation);
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file1);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("current user");
            multipart.addBodyPart(messageBodyPart);
            message.setText(currentLocation);
            message.setContent(multipart);
            Transport.send(message);
            File ee=new File(file1);
            ee.delete();
            System.out.println();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }
}
