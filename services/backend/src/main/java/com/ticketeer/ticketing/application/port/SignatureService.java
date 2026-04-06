package com.ticketeer.ticketing.application.port;

/**
 * Port responsible for signing and verifying ticket payloads.
 */
public interface SignatureService {

    String sign(String payload);

    boolean verify(String payload, String signature);
}
