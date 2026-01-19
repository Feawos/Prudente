package com.pfm.api;

public interface Exportable {
    String export();


    default String exportWithHeader(String header) {
        validate(header);
        return header + "\n" + export();
    }


    static Exportable csv(String payload) { return () -> payload; }


    private static void validate(String header) {
        if(header == null || header.isBlank()) throw new IllegalArgumentException("header required");
    }
}
