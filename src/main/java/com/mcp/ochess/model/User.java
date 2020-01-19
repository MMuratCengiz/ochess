package com.mcp.ochess.model;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Length(min = 5, message = "User can not be empty or less than 5 characters.")
    @Column(name = "name")
    private String name;

    @Length(min = 9, message = "Password must be at least 9 characters.")
    @Column(name = "password")
    private String password;

    @Transient
    private String matchingPassword;

    @Column(name = "privilege_code")
    private int privilegeCode;

    @OneToOne(targetEntity = Player.class)
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> l = new ArrayList<>();
        l.add(new SimpleGrantedAuthority(getPrivilegeCode() + ""));

        return l;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
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

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMatchingPassword(String password) {
        this.matchingPassword = password;
    }

    public int getPrivilegeCode() {
        return privilegeCode;
    }

    public void setPrivilegeCode(int privilegeCode) {
        this.privilegeCode = privilegeCode;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
