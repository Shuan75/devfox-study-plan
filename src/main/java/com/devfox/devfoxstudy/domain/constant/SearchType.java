package com.devfox.devfoxstudy.domain.constant;

import lombok.Getter;

public enum SearchType {
    TITLE("題目"),
    CONTENT("内容"),
    ID("UserId"),
    NICKNAME("Nickname"),
    HASHTAG("Hashtag");

    @Getter
    private final String description; //実際にSearchTypeでDescriptionを読みたいとGetterが必要

    SearchType(String description) {
        this.description = description;
    }
}
