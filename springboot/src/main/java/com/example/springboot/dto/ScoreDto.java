package com.example.springboot.dto;

import com.example.springboot.entity.Score;
import lombok.Data;

@Data
public class ScoreDto extends Score {
    private String name;
    private int credits;
    private String type;

}
