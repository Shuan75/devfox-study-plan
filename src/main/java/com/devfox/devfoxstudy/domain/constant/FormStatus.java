package com.devfox.devfoxstudy.domain.constant;


import lombok.Getter;

public enum FormStatus {
    CREATE("貯蔵", false),
    UPDATE("修正", true);

    @Getter private final String description;
    @Getter private final Boolean update;

    FormStatus(String description, Boolean update) {
        this.description = description;
        this.update = update;
    }

}