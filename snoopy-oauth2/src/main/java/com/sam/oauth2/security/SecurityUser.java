package com.sam.oauth2.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {

    private String username;
    private String password;
    private String role;

    //角色列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : role.split(",")) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    //是否可用 ,禁用无法验证
    @Override
    public boolean isEnabled() {
        return true;
    }

    //是否未过期,过期无法验证
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //是否未过期,过期无法验证
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //是否解锁,锁定无法验证
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

}
