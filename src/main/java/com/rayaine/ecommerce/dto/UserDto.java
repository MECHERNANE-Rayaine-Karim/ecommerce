package com.rayaine.ecommerce.dto;


import com.rayaine.ecommerce.model.User;

public class UserDto {

    private String username;
    private String contact;

    public UserDto( User user ){
        this.username = user.getUsername();
        this.contact = user.getContact();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
