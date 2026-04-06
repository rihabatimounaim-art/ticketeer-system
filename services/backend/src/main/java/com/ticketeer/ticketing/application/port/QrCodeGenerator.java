package com.ticketeer.ticketing.application.port;

/**
 * Port responsible for building QR content from a payload and its signature.
 */
public interface QrCodeGenerator {

    String generate(String payload, String signature);
}
