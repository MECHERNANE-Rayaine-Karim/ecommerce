package com.rayaine.ecommerce.dto;

import com.rayaine.ecommerce.model.User;

public class RegisterRequest {

    private String username;
    private String password;
    private String contact;
    private User.Role role;

    public RegisterRequest(){
    }

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
}
