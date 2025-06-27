package com.zipline.security;

import com.zipline.entity.user.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails implements UserDetails {

  @Getter
  private final Long userUid;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(User user) {
    this.userUid = user.getUid();
    this.username = user.getId();
    this.password = user.getPassword();
    this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }


}