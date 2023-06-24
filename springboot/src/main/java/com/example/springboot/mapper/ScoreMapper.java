package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Score;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
}
