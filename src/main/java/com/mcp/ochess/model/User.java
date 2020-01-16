package com.mcp.ochess.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User {
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

    public String getPassword() {
        return password;
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
}
