package com.rayaine.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public enum Role {
        ADMIN,USER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false, unique = true ,updatable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String contact;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,updatable = false)
    private Role role;

    public User(){

    }


    public Long getUserId() {
        return userId;
    }


    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Role getRole() {
        return role;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
