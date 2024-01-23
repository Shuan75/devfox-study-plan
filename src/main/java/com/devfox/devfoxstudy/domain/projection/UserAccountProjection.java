package com.devfox.devfoxstudy.domain.projection;

import com.devfox.devfoxstudy.domain.UserAccount;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDateTime;

// DBの必要なPropertyのみを照会
@Projection(name = "withoutPassword", types = UserAccount.class)
public interface UserAccountProjection {
    String getUserId();

    String getEmail();

    String getNickname();

    String getMemo();

    LocalDateTime getCreatedAt();

    String getCreatedBy();

    LocalDateTime getModifiedAt();

    String getModifiedBy();
}
