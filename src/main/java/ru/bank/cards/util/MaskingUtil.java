package ru.bank.cards.util;

/**
 * Утилиты маскирования PAN.
 */
public final class MaskingUtil {
    private MaskingUtil() {
    }

    /**
     * Маскирует номер карты, оставляя последние 4 цифры.
     */
    public static String maskPan(String pan) {
        if (pan == null || pan.isBlank()) return "**** **** **** ****";
        String digits = pan.replaceAll("\\s+", "");
        String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;
        return "**** **** **** " + last4;
    }
}
