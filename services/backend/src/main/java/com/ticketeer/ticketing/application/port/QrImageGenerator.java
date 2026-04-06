package com.ticketeer.ticketing.application.port;

/**
 * Port responsible for generating PNG QR images from QR content.
 */
public interface QrImageGenerator {

    byte[] generatePng(String content, int width, int height);
}
