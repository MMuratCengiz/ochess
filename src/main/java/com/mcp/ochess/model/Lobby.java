package com.mcp.ochess.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "lobbies")
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "white_uid", referencedColumnName = "id")
    private User whiteUser;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "black_uid", referencedColumnName = "id")
    private User blackUser;

    @Column(name = "password")
    private String password;

    @Column(name = "type")
    private int type;

    @Column(name = "created_date")
    private Timestamp createdDate;

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

    public User getWhiteUser() {
        return whiteUser;
    }

    public void setWhiteUser(User whiteUser) {
        this.whiteUser = whiteUser;
    }

    public User getBlackUser() {
        return blackUser;
    }

    public void setBlackUser(User blackUser) {
        this.blackUser = blackUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
