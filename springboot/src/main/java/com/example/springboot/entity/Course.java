package com.example.springboot.entity;

import lombok.Data;

@Data
public class Course {

    private Long id;
    private String name;
    private String tecName;
    private String time;
    private String hours;
    private int credits;
    private String type;
}
