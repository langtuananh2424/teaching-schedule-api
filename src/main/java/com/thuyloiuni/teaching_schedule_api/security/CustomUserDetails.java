package com.thuyloiuni.teaching_schedule_api.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    @JsonIgnore // Important to avoid circular serialization
    private final Lecturer lecturer;

    public static CustomUserDetails create(Lecturer lecturer) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + lecturer.getRole().name()));
        return new CustomUserDetails(lecturer);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + lecturer.getRole().name()));
    }

    @Override
    public String getPassword() {
        return lecturer.getPassword();
    }

    @Override
    public String getUsername() {
        return lecturer.getEmail();
    }

    // The methods below are usually for account status checks
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(lecturer.getLecturerId(), that.lecturer.getLecturerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturer.getLecturerId());
    }
}
