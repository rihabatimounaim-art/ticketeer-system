package com.ticketeer.ticketing.infrastructure;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.ticketeer.ticketing.application.port.PdfTicketGenerator;
import com.ticketeer.ticketing.domain.model.Ticket;

import java.io.ByteArrayOutputStream;

public class OpenPdfTicketGenerator implements PdfTicketGenerator {

    @Override
    public byte[] generate(final Ticket ticket, final byte[] qrPng) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final Document document = new Document(new Rectangle(595, 842), 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            final Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Ticketeer - Ticket", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Ticket ID: " + ticket.getId(), bodyFont));
            document.add(new Paragraph("Holder ID: " + ticket.getHolderId(), bodyFont));
            document.add(new Paragraph("Status: " + ticket.getStatus().name(), bodyFont));
            document.add(new Paragraph("Valid from: " + ticket.getValidityWindow().getStart(), bodyFont));
            document.add(new Paragraph("Valid until: " + ticket.getValidityWindow().getEnd(), bodyFont));
            document.add(new Paragraph("Issued at: " + ticket.getIssuedAt(), bodyFont));
            document.add(new Paragraph(" "));

            final Image qrImage = Image.getInstance(qrPng);
            qrImage.scaleToFit(220, 220);
            document.add(qrImage);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate ticket PDF", e);
        }
    }
}
