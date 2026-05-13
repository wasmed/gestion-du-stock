package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendInvoiceEmail(String toEmail, Long commandeId, String invoiceUrl) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Votre Facture - Commande #" + commandeId);

            String htmlContent = "<h2>Merci pour votre commande !</h2>" +
                    "<p>Votre commande #" + commandeId + " a été traitée avec succès.</p>" +
                    "<p>Vous pouvez consulter et imprimer votre facture en cliquant sur le lien ci-dessous :</p>" +
                    "<a href=\"" + invoiceUrl + "\">Voir ma facture</a>" +
                    "<br><br><p>L'équipe du Restaurant</p>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail", e);
        }
    }
}
