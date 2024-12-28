package com.markndevon.cardgames.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
@Entity
public class CardGameUser {
    @Id
    public int id;

    public String username;
    public String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString(){
        return "User: " + username + " Password: " + password;
    }
}
