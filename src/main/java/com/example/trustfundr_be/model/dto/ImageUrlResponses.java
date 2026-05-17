package com.example.trustfundr_be.model.dto;

/** Keeps list/browse API payloads small when activities store embedded hero images. */
public final class ImageUrlResponses {

    private static final int MAX_LIST_IMAGE_URL_LENGTH = 2048;

    private ImageUrlResponses() {
    }

    public static String forBrowseList(String imageUrl) {
        if (imageUrl == null) {
            return null;
        }
        String trimmed = imageUrl.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.startsWith("data:") || trimmed.length() > MAX_LIST_IMAGE_URL_LENGTH) {
            return null;
        }
        return trimmed;
    }
}
