package com.github.gaboso.photo.util;

public class Validate {

    private Validate() {
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty() || text.trim().isEmpty();
    }

}
