package com.agile.jewelryshop.util;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {
    private SlugUtils() {}

    public static String toSlug(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd').replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "mon-an" : normalized;
    }
}
