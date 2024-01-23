package com.devfox.devfoxstudy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditingFields {

    //==Domain Data==//
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt; // 生成日時
    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    protected String createdBy; // 生成者
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedAt; // 修正日時
    @LastModifiedBy
    @Column(nullable = false, length = 100)
    protected String modifiedBy; // 修正者
    // JpaAuditing : insert, updateが起こる時自動で作成者と時間をRealTimeで入れる
    // 抽出して作成した理由 : もっと多くの方法を練習したいからです
    // 抽出せずに二つにすると、変更することがあった時にそのTableだけ変えればいいので、メンテナンスが簡単だという長所があります
    // @DateTimeFormat : parameterを受けてsettingする時、parsingを円滑にため
}
