/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;

/**
 *
 * @author emayorga
 */
public class SendMail {
    private static Logger log = Logger.getLogger(SendMail.class);
    
    public SendMail()
    {
    }

    public static void sendEmail(String smtpServ, String recipients[], String subject, String message, String from, String user, String password)
    {
        boolean debug = true;
        try
        {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpServ);
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(debug);
            Message msg = new MimeMessage(session);
            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);
            for(int i = 0; recipients[i] != null; i++)
            {
                msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipients[i]));
                msg.addHeader("MyHeaderName", "myHeaderValue");
                msg.setSubject(subject);
                msg.setContent(message, "text/plain");
                Transport tr = session.getTransport("smtp");
                tr.connect(smtpServ, user, password);
                Transport _tmp = tr;
                Transport.send(msg);
            }

        }
        catch(Exception e)
        {
            log.info("Excepción: "+ e.getMessage(), e);
        }
    }

    public static void sendEmailHtml(String smtpServ, String recipients[], String subject, String message, String from, final String user, final String password, String rutaImg)
    {
        boolean debug = true;
        try
        {
            log.info("" + smtpServ + " \n" + from+ " \n" +user+ " \n" + password+ " \n"+ rutaImg);
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpServ);
            props.put("mail.smtp.auth", "true");
            //Session session = Session.getDefaultInstance(props, null);
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            session.setDebug(debug);
            Message msg = new MimeMessage(session);
            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);
            msg.setSubject(subject);
            for(int i = 0; i<recipients.length; i++)
            {
                msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipients[i]));
                MimeMultipart multipart = new MimeMultipart("related");
                BodyPart messageBodyPart = new MimeBodyPart();
                String htmlText = (new StringBuilder()).append("<img src=\"cid:image\">").append(message).toString();
                messageBodyPart.setContent(htmlText, "text/html");
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                javax.activation.DataSource fds = new FileDataSource(rutaImg);
                messageBodyPart.setDataHandler(new DataHandler(fds));
                messageBodyPart.setHeader("Content-ID", "<image>");
                multipart.addBodyPart(messageBodyPart);
                msg.setContent(multipart);
//              Transport tr = session.getTransport("smtp");
//              tr.connect(smtpServ, user, password);
//              tr.send(msg);
//              Transport _tmp = tr;
                Transport.send(msg);
            }

        }
        catch(Exception e)
        {
            log.info("Excepción: "+ e.getMessage(), e);
        }
    }    
}
