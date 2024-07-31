package com.cmpt213.finalProject.SYNC.service;

public class friendDTO {
    private Integer id;
    private String login;

    // Constructors, getters, and setters

    public friendDTO() {}

    public friendDTO(Integer id, String login) {
        this.id = id;
        this.login = login;
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
}

