package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Score;
import com.example.springboot.mapper.ScoreMapper;
import com.example.springboot.service.ScoreService;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
}
