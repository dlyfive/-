package com.example.springboot.entity;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private String image;
    private int status;
    private String college;
    private String compulsoryCredits;
    private String electiveCredits;
    private String totalCredits;


}
