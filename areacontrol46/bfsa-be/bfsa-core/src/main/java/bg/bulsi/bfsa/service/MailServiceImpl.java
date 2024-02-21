package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.VerificationToken;
import freemarker.template.Configuration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MailServiceImpl implements MailService {

    private static final String SUPPORT_EMAIL = "support.email";
    private static final String REGISTER_USER_VERIFY_TOKEN_URL = "jwt.registerUserVerifyTokenUrl";
    private static final String FORGOT_PASSWORD_VERIFY_TOKEN_URL = "jwt.forgotPasswordVerifyTokenUrl";
    public static final String LINE_BREAK = "<br>";
    public final static String BASE_URL = "baseUrl";

    private final JavaMailSender mailSender;
    private final Environment env;
    private final Configuration freemarkerConfiguration;

    private final MessageResourceService msgRes;

    // TODO Pass dos instead of entities
    //    @Async This broke the transaction from previous method
    @Override
    public void sendRegisterUserVerificationToken(final VerificationToken verificationToken, final Language language) {
        final String confirmationUrl = env.getProperty(REGISTER_USER_VERIFY_TOKEN_URL)
                + verificationToken.getVerificationToken() + "&lang=" + language.getLanguageId();
        sendVerificationTokenHtmlEmail(msgRes.get("email.register.user.verification.subject", language),
                msgRes.get("email.register.user.verification.message", language)
                + LINE_BREAK + confirmationUrl, verificationToken, language);
    }

    // TODO Pass dos instead of entities
    //    @Async This broke the transaction from previous method
    @Override
    public void sendForgotPasswordVerificationToken(final VerificationToken verificationToken, final Language language) {
        final String confirmationUrl = env.getProperty(FORGOT_PASSWORD_VERIFY_TOKEN_URL)
                + verificationToken.getVerificationToken() + "&lang=" + language.getLanguageId();
        sendVerificationTokenHtmlEmail(msgRes.get("email.forgot.password.verification.subject", language),
                msgRes.get("email.forgot.password.verification.message", language)
                        + LINE_BREAK + confirmationUrl, verificationToken, language);
    }

    // TODO Pass dos instead of entities
//    @Async This broke the transaction from previous method
    @Override
    public void sendApplicationPaymentNotification(final BigDecimal price, final Record record,
                                                   final Language language, final String paymentCode) {
        Map<String, Object> model = new HashMap<>();
//        model.put("title", null);
        model.put("branch", record.getBranch().getI18n(language).getName());
        model.put("name", record.getRequestor().getFullName());
        model.put("entryNumber", record.getEntryNumber());
        model.put("serviceTypeCode", record.getServiceType().name());
        model.put("serviceName", msgRes.get(record.getServiceType().name() + ".name", language)); // име на услугата
        model.put("price", price);
        model.put("paymentCode", paymentCode);
//        model.put(BASE_URL, env.getProperty(REGISTER_USER_VERIFY_TOKEN_URL));

        try {
            sendHtmlMail(env.getProperty(SUPPORT_EMAIL),
                    record.getRequestor().getEmail(),
                    msgRes.get("email.application.payment.notification.subject", language),
                    getFreeMarkerTemplateContent(model,
                            "mail/application_payment_notification_" + language.getLanguageId() + ".ftl"));
        } catch (MessagingException e) {
            log.error("Failed to send mail", e);
        }
    }

    // TODO Pass dos instead of entities
    //    @Async This broke the transaction from previous method
    @Override
    public void sendApplicationCorrectionNotification(final Record record, final String message, final Language language) {
        Map<String, Object> model = new HashMap<>();
        model.put("branch", record.getBranch().getI18n(language).getName());
        model.put("name", record.getRequestor().getFullName());
        model.put("message", message);
        model.put("entryNumber", record.getEntryNumber());
        model.put("serviceTypeCode", record.getServiceType().name());
        model.put("serviceName", msgRes.get(record.getServiceType().name() + ".name", language)); // име на услугата
        model.put("date", record.getEntryDate().plusDays(10L));

        try {
            //String subject = "Връщане за корекция.";
            sendHtmlMail(env.getProperty(SUPPORT_EMAIL),
                    record.getRequestor().getEmail(),
                    msgRes.get("email.application.correction.notification.subject", language),
              //      subject,
                    getFreeMarkerTemplateContent(model,
                            "mail/application_correction_notification_" + language.getLanguageId() + ".ftl"));
        } catch (MessagingException e) {
            log.error("Failed to send mail", e);
        }
    }

    private void sendVerificationTokenHtmlEmail(final String subject, final String msg,
                                                final VerificationToken verificationToken, final Language language) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", verificationToken.getFullName());
        model.put("msg", msg);
        model.put("title", subject);
        model.put(BASE_URL, env.getProperty(REGISTER_USER_VERIFY_TOKEN_URL));
        try {
            sendHtmlMail(env.getProperty(SUPPORT_EMAIL), verificationToken.getEmail(), subject,
                    getFreeMarkerTemplateContent(model, "mail/verification_" + language.getLanguageId() + ".ftl"));
        } catch (MessagingException e) {
            log.error("Failed to send mail", e);
        }
    }

    private String getFreeMarkerTemplateContent(final Map<String, Object> model, final String templateName) {
        StringBuilder content = new StringBuilder();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(templateName), model));
            return content.toString();
        } catch (Exception e) {
            System.out.println("Exception occurred while processing fm-template:" + e.getMessage());
        }
        return "";
    }

    public void sendHtmlMail(final String from, final String to, final String subject, final String body) throws MessagingException {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, StandardCharsets.UTF_8.name());
        helper.setFrom(from);
        if (to.contains(",")) {
            helper.setTo(to.split(","));
        } else {
            helper.setTo(to);
        }
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(mail);
        log.info("Sent mail: {0}", subject);
    }
}
