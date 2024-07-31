package com.cmpt213.finalProject.SYNC.service;

public class UserDTO {
    private Integer id;
    private String login;
    private String email;

    
    public UserDTO(Integer id, String login, String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }
   
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and setters
}
