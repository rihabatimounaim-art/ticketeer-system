package com.ticketeer.ticketing.application.port;

/**
 * Port responsible for signing ticket payloads.
 */
public interface SignatureService {

    String sign(String payload);
}
