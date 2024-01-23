package com.devfox.devfoxstudy.repository;

import com.devfox.devfoxstudy.domain.UserAccount;
import com.devfox.devfoxstudy.domain.projection.UserAccountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = UserAccountProjection.class)
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}
