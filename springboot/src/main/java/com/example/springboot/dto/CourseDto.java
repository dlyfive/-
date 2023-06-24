package com.example.springboot.dto;

import com.example.springboot.entity.Course;
import lombok.Data;

@Data
public class CourseDto extends Course {
    private Long count;
}
