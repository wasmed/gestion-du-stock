package com.example.demo.service;

import com.example.demo.model.Commande;
import com.example.demo.model.LigneCommande;
import com.example.demo.model.TypeLigneCommande;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class EmailService {


    @Autowired
    private JavaMailSender javaMailSender;

    public void sendInvoiceEmail(String toEmail, Commande commande) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Votre Facture - Commande #" + commande.getId());

            String htmlContent = "<h2>Merci pour votre commande !</h2>" +
                    "<p>Votre commande #" + commande.getId() + " a été traitée avec succès.</p>" +
                    "<p>Vous trouverez votre facture en pièce jointe.</p>" +
                    "<br><br><p>L'équipe du Restaurant</p>";

            helper.setText(htmlContent, true);

            ByteArrayOutputStream pdfStream = generateInvoicePdf(commande);
            helper.addAttachment("Facture_" + commande.getId() + ".pdf", new ByteArrayResource(pdfStream.toByteArray()));

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail", e);
        }
    }

    private ByteArrayOutputStream generateInvoicePdf(Commande commande) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Le Bon Goût - Facture", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Commande #" + commande.getId(), normalFont));
            document.add(new Paragraph("Date: " + commande.getDateHeure().toString(), normalFont));
            document.add(new Paragraph("Type: " + (commande.getIsEmporter() ? "À emporter" : "Sur place"), normalFont));
            if (commande.getClient() != null) {
                document.add(new Paragraph("Client: " + commande.getClient().getFullName(), normalFont));
            }
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2f});

            addTableHeader(table);
            addRows(table, commande);

            document.add(table);

            document.add(new Paragraph(" "));

            double total = commande.getMontantTotal();
            double tva = total * 0.12;
            double subtotal = total - tva;

            Paragraph pSubtotal = new Paragraph(String.format("Sous-total HT: %.2f €", subtotal), normalFont);
            pSubtotal.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(pSubtotal);

            Paragraph pTva = new Paragraph(String.format("TVA (12%%): %.2f €", tva), normalFont);
            pTva.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(pTva);

            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph pTotal = new Paragraph(String.format("TOTAL TTC: %.2f €", total), boldFont);
            pTotal.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(pTotal);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
        return baos;
    }

    private void addTableHeader(PdfPTable table) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        table.addCell(new PdfPCell(new Phrase("Description", font)));
        table.addCell(new PdfPCell(new Phrase("Qté", font)));
        table.addCell(new PdfPCell(new Phrase("Prix U.", font)));
        table.addCell(new PdfPCell(new Phrase("Total", font)));
    }

    private void addRows(PdfPTable table, Commande commande) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        for (LigneCommande ligne : commande.getLignesCommande()) {
            String desc = ligne.getTypeLigne() == TypeLigneCommande.PLAT ? ligne.getPlat().getNom() : ligne.getMenu().getNom();
            double prixU = ligne.getTypeLigne() == TypeLigneCommande.PLAT ? ligne.getPlat().getPrix() : ligne.getMenu().getPrix();
            double totalLigne = prixU * ligne.getQuantite();

            table.addCell(new PdfPCell(new Phrase(desc, font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(ligne.getQuantite()), font)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f €", prixU), font)));
            table.addCell(new PdfPCell(new Phrase(String.format("%.2f €", totalLigne), font)));
        }
    }
}
