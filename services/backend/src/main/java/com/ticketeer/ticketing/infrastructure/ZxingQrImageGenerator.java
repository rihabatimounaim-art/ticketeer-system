package com.ticketeer.ticketing.infrastructure;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ticketeer.ticketing.application.port.QrImageGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ZxingQrImageGenerator implements QrImageGenerator {

    @Override
    public byte[] generatePng(final String content, final int width, final int height) {
        try {
            final Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            final BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
            );

            final BufferedImage image = toBufferedImage(bitMatrix);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ImageIO.write(image, "PNG", outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate QR PNG image", e);
        }
    }

    private BufferedImage toBufferedImage(final BitMatrix matrix) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        return image;
    }
}
