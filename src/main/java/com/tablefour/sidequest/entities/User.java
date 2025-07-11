package com.tablefour.sidequest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tablefour.sidequest.entities.enums.Gender;
import com.tablefour.sidequest.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String idCardNumber;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private String password;

    private String phoneNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities;

    @Builder.Default
    private LocalDate restrictedUntil = LocalDate.now();

    @Override
    public Collection<Role> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isRestricted();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isRestricted() {
        return restrictedUntil != null && restrictedUntil.isAfter(LocalDate.now());
    }

    @OneToMany(mappedBy = "ratedUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Rating> receivedRatings;

    @OneToMany(mappedBy = "raterUser", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Rating> givenRatings;

    @Formula("(SELECT COALESCE(AVG(r.value), 0.0) FROM application.ratings r WHERE r.rated_user_id = id)")
    private double rating;
}
