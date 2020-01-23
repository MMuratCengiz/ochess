package com.mcp.ochess.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(targetEntity = Lobby.class)
    @JoinColumn(name = "in_game", referencedColumnName = "id")
    private Lobby inGameLobby;

    @Column(name = "wins")
    private int wins;

    @Column(name = "losses")
    private int losses;

    @Column(name = "mmr")
    private int mmr;

    @Column(name = "last_played_game")
    private Timestamp lastPlayTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Lobby getInGameLobby() {
        return inGameLobby;
    }

    public void setInGameLobby(Lobby inGameLobbyId) {
        this.inGameLobby = inGameLobbyId;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getMmr() {
        return mmr;
    }

    public void setMmr(int mmr) {
        this.mmr = mmr;
    }

    public Timestamp getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(Timestamp lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }
}
