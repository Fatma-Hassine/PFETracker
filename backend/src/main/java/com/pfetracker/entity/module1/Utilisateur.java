 package com.pfetracker.entity.module1;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pfetracker.entity.module1.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, unique = true)
    private String email;
 
    @Column(nullable = false)
    private String motDePasse;
 
    @Column(nullable = false)
    private String nomComplet;
 
 
    @Column(nullable = false)
    private boolean mustChangePassword = true;
 
    private boolean accountLocked = false;
    private int failedLoginAttempts = 0;
    private LocalDateTime lockTime;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
 
    // activé par le responsable de département
    @Column(nullable = false)
    private boolean enabled = false; 
    
    // Relations
    
    /** OneToMany : un utilisateur peut recevoir plusieurs notifications */
    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationM1> notifications;
 
    /** OneToMany : un utilisateur génère plusieurs entrées de log */
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogAudit> logs;
 
    // UserDetails 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
 
    @Override 
    public String getPassword()  { return motDePasse; }
    @Override
    public String getUsername()  { return email; }
    @Override
    public boolean isAccountNonLocked() { return !accountLocked; }
    @Override
    public boolean isEnabled()   { return enabled; }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
