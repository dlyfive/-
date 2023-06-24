package com.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.CustomException;
import com.example.springboot.common.R;
import com.example.springboot.dto.CourseDto;
import com.example.springboot.dto.ScoreDto;
import com.example.springboot.entity.Course;
import com.example.springboot.entity.Score;
import com.example.springboot.entity.User;
import com.example.springboot.service.CourseService;
import com.example.springboot.service.ScoreService;
import com.example.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stud")
@Slf4j
public class StudentController {

    @Autowired
    private UserService userService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<User> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole,1);
        queryWrapper.like(name != null,User::getName,name);
        userService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PostMapping
    public R<String> save(@RequestBody User user){
//        int compulsory = 0;
//        int select = 0;
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setRole("1");
        user.setStatus(1);

        //刚加进去的学生学分应该就是0的啊
        user.setCompulsoryCredits("0");
        user.setElectiveCredits("0");
        user.setTotalCredits("0");

        userService.save(user);
        return R.success("新增学生成功");
    }

    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id){   //@PathVariable表示方法的id是从路径中获得的  注意这种方法
        User user = userService.getById(id);
        if(user != null){
            return R.success(user);
        }
        return R.error("没有查询到对应学生信息");
    }

    @PutMapping()
    public R<String> update(HttpServletRequest request, @RequestBody User user){
        userService.updateById(user);      //这个updateByid是已经封装好的直接用的，但是为什么会选择更新部分信息呢，更新时间更新人还需要自己去设置
        return R.success("学生信息修改成功");
    }

    //学生选课
    @PostMapping("/selectCourse")
    public R<String> selectCourse(Long ids,HttpServletRequest request){
        Score score = new Score();
        //检查看看有没有已经选过这个课了，如果已经选了就报错
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getStuId,request.getSession().getAttribute("user").toString());
        queryWrapper.eq(Score::getCourId,ids.toString());
        long count = scoreService.count(queryWrapper);
        if (count != 0){
            return R.error("该课已选");
        }
        //如果count=0，则还没有选过这个课
        score.setStuId(request.getSession().getAttribute("user").toString());
        score.setCourId(ids.toString());
        scoreService.save(score);
        return R.success("选课成功");
    }

    //选课结果查询
    @GetMapping("/pageCR")
    public R<Page> pageCR(int page,int pageSize,HttpServletRequest request){
        Page<Course> pageInfo = new Page<>(page,pageSize);
        Page<CourseDto> pageDto = new Page<>();

        //根据用户id得到用户选的所有课
        String  stuId = request.getSession().getAttribute("user").toString();
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getStuId,stuId);
        List<Score> list = scoreService.list(queryWrapper);

        List<String> ids = new ArrayList<>();
        for (Score item1 : list) {
            String courId = item1.getCourId();
            ids.add(courId);
        }

        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.in(Course::getId,ids);
        courseLambdaQueryWrapper.orderByAsc(Course::getType);
        courseService.page(pageInfo,courseLambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Course> courseList = pageInfo.getRecords();

        List<CourseDto> courseDtoList = courseList.stream().map((item) ->{
            CourseDto dto = new CourseDto();
            BeanUtils.copyProperties(item,dto);
            LambdaQueryWrapper<Score> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Score::getCourId,dto.getId().toString());
            long count = scoreService.count(queryWrapper1);
            dto.setCount(count);
            return dto;
        }).collect(Collectors.toList());
        pageDto.setRecords(courseDtoList);
        log.info(courseDtoList.toString());
        return R.success(pageDto);
    }

    //学生课程成绩查询
    @GetMapping("/studScore")
    public R<Page> getStudScoreList(int page,int pageSize,HttpServletRequest request){
        String  stuId = request.getSession().getAttribute("user").toString();
        Page<Score> scorePage = new Page<>(page,pageSize);
        Page<ScoreDto> scoreDtoPage = new Page<>();
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getStuId,stuId);
        scoreService.page(scorePage,queryWrapper);

        BeanUtils.copyProperties(scorePage,scoreDtoPage,"records");
        List<Score> records = scorePage.getRecords();
        List<ScoreDto> scoreDtoList = records.stream().map((item)->{
            ScoreDto scoreDto = new ScoreDto();
            LambdaQueryWrapper<Course> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Course::getId,item.getCourId());
            Course course = courseService.getOne(queryWrapper1);
            scoreDto.setName(course.getName());
            scoreDto.setCredits(course.getCredits());
            scoreDto.setType(course.getType());

            //获得分数
            LambdaQueryWrapper<Score> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Score::getStuId,stuId);
            queryWrapper2.eq(Score::getCourId,course.getId().toString());
            Score score = scoreService.getOne(queryWrapper2);
            scoreDto.setScore(score.getScore());
            return scoreDto;
        }).collect(Collectors.toList());
        scoreDtoPage.setRecords(scoreDtoList);
        return R.success(scoreDtoPage);
    }
}
