package com.ticketeer.ticketing.domain.service;

/**
 * Result of a discount computation.
 */
public record DiscountResult(
        double originalPrice,
        double finalPrice,
        int discountPercent,
        String discountLabel
) {
    public boolean hasDiscount() {
        return discountPercent > 0;
    }
}
