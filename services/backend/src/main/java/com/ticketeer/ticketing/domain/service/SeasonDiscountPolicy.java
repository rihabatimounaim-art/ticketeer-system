package com.ticketeer.ticketing.domain.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Domain service that computes a ticket price discount based on:
 * <ul>
 *   <li>Saison basse (Jan, Fev, Nov) → -20%</li>
 *   <li>Weekend (Sam, Dim)           → -10%</li>
 *   <li>Reservation anticipee (+14j) → -15%</li>
 *   <li>Saison haute (Jul, Aout)     → 0%  (plein tarif)</li>
 * </ul>
 * Rules are cumulative, capped at 30%.
 */
public class SeasonDiscountPolicy {

    private static final int MAX_DISCOUNT_PERCENT = 30;

    /**
     * @param basePrice     Prix de base du trajet
     * @param departureTime Instant de depart du train
     * @param bookingTime   Instant au moment de l'achat (now)
     */
    public DiscountResult apply(double basePrice, Instant departureTime, Instant bookingTime) {
        final var departure = departureTime.atZone(ZoneOffset.UTC);
        final int month = departure.getMonthValue();
        final DayOfWeek day = departure.getDayOfWeek();
        final long daysUntilDeparture = Duration.between(bookingTime, departureTime).toDays();

        int totalDiscount = 0;
        final StringBuilder labelBuilder = new StringBuilder();

        // Rule 1 — Saison basse (jan=1, fev=2, nov=11)
        if (month == 1 || month == 2 || month == 11) {
            totalDiscount += 20;
            append(labelBuilder, "Saison basse");
        }

        // Rule 2 — Weekend
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            totalDiscount += 10;
            append(labelBuilder, "Weekend");
        }

        // Rule 3 — Reservation anticipee (plus de 14 jours avant)
        if (daysUntilDeparture > 14) {
            totalDiscount += 15;
            append(labelBuilder, "Anticipee");
        }

        // Cap
        totalDiscount = Math.min(totalDiscount, MAX_DISCOUNT_PERCENT);

        if (totalDiscount == 0) {
            return new DiscountResult(basePrice, basePrice, 0, "Plein tarif");
        }

        final double finalPrice = Math.round(basePrice * (1.0 - totalDiscount / 100.0) * 100.0) / 100.0;
        return new DiscountResult(basePrice, finalPrice, totalDiscount, labelBuilder.toString());
    }

    private void append(StringBuilder sb, String label) {
        if (!sb.isEmpty()) sb.append(" + ");
        sb.append(label);
    }
}
