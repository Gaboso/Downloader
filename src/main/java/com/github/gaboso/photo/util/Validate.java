package com.github.gaboso.photo.util;

public class Validate {

    private Validate() {
    }

    /**
     * Metodo para validar se a string e nula ou vazia
     *
     * @param text - string a ser validada
     * @return true se for nula ou vazia ou false caso contrario
     */
    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty() || text.trim().isEmpty();
    }

}