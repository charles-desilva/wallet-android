package Process_Classes;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
    public void doSend(final String subject, final String reciever, final String htmlcontent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailData mailData=new MailData();

                    final String sender_mail = mailData.getEmail();
                    final String sender_password = mailData.getPassword();


                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.host", mailData.getHost());
                    props.put("mail.smtp.port", mailData.getPort());

                    Session session = Session.getInstance(props, new javax.mail.Authenticator() {

                        protected PasswordAuthentication getPasswordAuthentication() {

                            return new PasswordAuthentication(sender_mail, sender_password);

                        }

                    });
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(sender_mail));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciever));

                    message.setSubject(subject);
//                         message.setText(htmlcontent);
                    message.setContent(htmlcontent, "text/html;charset=utf-8");

                    Transport.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
