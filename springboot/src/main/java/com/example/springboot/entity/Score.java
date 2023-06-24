package com.example.springboot.entity;

import lombok.Data;

@Data
public class Score {

    private Long id;
    private String stuId;
    private String courId;
    private int score;
}
