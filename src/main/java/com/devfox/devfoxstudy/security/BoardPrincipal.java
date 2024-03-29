package com.devfox.devfoxstudy.security;

import com.devfox.devfoxstudy.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        String memo
) implements UserDetails { // User.javaを使わない理由は必要ない設定たちがたくさんあるから

    // 実際に使う開発者は権限情報を入れる必要がない事をこのmethodを通じてわかる
    // 初めての認証が実行された時ConstructorからRoleTypeが作られる
    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        // もし権限情報をCollectionDataで貯蔵する時、同じ権限を持つ必要がないからSet
        // RoleType => 後で拡張を考慮して作ったもの
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new BoardPrincipal(
                username,
                password,
                roleTypes.stream() // authoritiesを使わず、roletypeを使ったからstream利用
                        .map(RoleType::getName) // nameを持ってくる
                        .map(SimpleGrantedAuthority::new) // GrantedAuthorityの基本具現体
                        .collect(Collectors.toUnmodifiableSet()),
                email,
                nickname,
                memo
        );
    }

    // 逆にUserAccountDtoからBoardPrincipalを作る場合
    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    // BoardPrincipalを受けてUserAccountDtoで作る
    // instanceが作られた状況なのでstaticを使わない
    // このmethodで会員情報に貯蔵可能
    public UserAccountDto toDto() {
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 権限に対する内容 ( admin service )
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum RoleType {
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}
