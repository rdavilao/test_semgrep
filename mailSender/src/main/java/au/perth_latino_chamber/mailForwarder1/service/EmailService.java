package au.perth_latino_chamber.mailForwarder1.service;

import au.perth_latino_chamber.mailForwarder1.api.dto.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailService {

    private String msgError;
    private final String contactorEmail = "admin@lccwa.au";

    @Autowired
    private JavaMailSender emailSender;

    public EmailService() {
    }

    public void sendEmail(EmailDTO emailDTO) throws Exception {
        try {
            log.info("Sending email to: " + emailDTO.getEmail());
            log.info("Email info: "+ emailDTO);

            String subjectForContactor = "LCCWA: New CONTACT! Entry - "
                    + emailDTO.getName() + " !";
            String subjectForInterested = "Latino Chamber of Commerce WA - Confirmation of message received";
            //ClassPathResource imageResource = new ClassPathResource("/static/images/SellMyCarWa-blue.png");

            MimeMessage mimeMessageContactor = this.emailSender.createMimeMessage();
            MimeMessageHelper helperContactor = new MimeMessageHelper(mimeMessageContactor, true);
            helperContactor.setFrom(this.contactorEmail, "Latino Chamber of Commerce WA");
            helperContactor.setTo(this.contactorEmail);
            helperContactor.setSubject(subjectForContactor);
            helperContactor.setText(this.generateTextContactorMessage(emailDTO), true);
            //helperContactor.addInline("footerImage", imageResource);
            emailSender.send(mimeMessageContactor);
            log.info("Correct mail delivery for contactor");

            MimeMessage mimeMessageInterested = this.emailSender.createMimeMessage();
            MimeMessageHelper helperInterested = new MimeMessageHelper(mimeMessageInterested, true);
            helperInterested.setFrom(this.contactorEmail, "Latino Chamber of Commerce WA");
            helperInterested.setTo(emailDTO.getEmail());
            helperInterested.setSubject(subjectForInterested);
            helperInterested.setText(this.generateTextInterestedMessage(emailDTO.getName()), true);
            // helperInterested.addInline("footerImage", imageResource);
            emailSender.send(mimeMessageInterested);
            log.info("Correct mail delivery for interested");

        } catch (Exception ex) {
            msgError = "Incorrect mail delivery";
            log.error(msgError);
            throw new Exception(ex);
        }
    }

    private String generateTextInterestedMessage(String name) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<h4>").append("Thanks ").append(name).append(" for contacting us! We will be in touch with you shortly.").append("</h4>");
        htmlContent.append("<br><br>");
        //htmlContent.append("<img src='cid:footerImage'/>");
        return htmlContent.toString();
    }

    private String generateTextContactorMessage(EmailDTO emailDTO) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<h2>").append("Interested Customer Details:").append("</h2>");
        htmlContent.append("<table border='1'>");
        htmlContent.append("<thead>");
        htmlContent.append("<tr>");
        htmlContent.append("<th>").append("Name").append("</th>");
        htmlContent.append("<th>").append("Email").append("</th>");
        htmlContent.append("<th>").append("Message").append("</th>");
        htmlContent.append("</tr>");
        htmlContent.append("</thead>");
        htmlContent.append("<tbody>");
        htmlContent.append("<tr>");
        htmlContent.append("<td>").append(emailDTO.getName()).append("</td>");
        htmlContent.append("<td>").append(emailDTO.getEmail()).append("</td>");
        htmlContent.append("<td>").append(emailDTO.getMessage()).append("</td>");
        htmlContent.append("</tr>");
        htmlContent.append("</tbody>");
        htmlContent.append("</table>");
        htmlContent.append("<br><br>");
        //htmlContent.append("<img src='cid:footerImage'/>");
        return htmlContent.toString();
    }
}
