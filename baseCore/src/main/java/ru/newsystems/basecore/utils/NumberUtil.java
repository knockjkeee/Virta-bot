package ru.newsystems.basecore.utils;

public class NumberUtil {
    private static final int LENGTH_CHAR_BY_ID = 14;
    private static final int LENGTH_CHAR_BY_TICKET_NUMBER = 15;

    public static long getId(String text) {
        if (isCurrentLength(text, LENGTH_CHAR_BY_ID)) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static String getIdByTicketNumber(String text) {
        if (isCurrentLength(text, LENGTH_CHAR_BY_TICKET_NUMBER)) {
            return text;
        }
        return "0";
    }

    public static boolean isCurrentLength(String text, int len) {
        if (len == LENGTH_CHAR_BY_ID) {
            return text.length() <= len;
        }
        if (len == LENGTH_CHAR_BY_TICKET_NUMBER) {
            return text.length() >= len;
        }
        return false;
    }
}
