package com.devfox.devfoxstudy.dto;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        String content,
        String createdBy,
        LocalDateTime createdAt,
        String modifiedBy,
        LocalDateTime modifiedAt
) {
    public static ArticleCommentDto of(String content, String createdBy, LocalDateTime createdAt, String modifiedBy, LocalDateTime modifiedAt) {
        return new ArticleCommentDto(content, createdBy, createdAt, modifiedBy, modifiedAt);
    }
}
