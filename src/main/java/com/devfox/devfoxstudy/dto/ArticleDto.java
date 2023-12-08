package com.devfox.devfoxstudy.dto;

import java.time.LocalDateTime;

public record ArticleDto(
        String title,
        String content,
        String hashtag,
        String createdBy,
        LocalDateTime createdAt
) {
    public static ArticleDto of(String title, String content, String hashtag, String createdBy, LocalDateTime createdAt) {
        return new ArticleDto(title, content, hashtag, createdBy, createdAt);
        // constructorは既にあるからstatic method
    }
}
