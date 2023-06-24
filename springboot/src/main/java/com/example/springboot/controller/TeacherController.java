package com.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.R;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ScoreService scoreService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<User> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getRole,2);
        queryWrapper.like(name != null,User::getName,name);
        userService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PostMapping
    public R<String> save(@RequestBody User user){
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setRole("2");
        user.setStatus(1);

        userService.save(user);
        return R.success("新增教师成功");
    }

    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id){   //@PathVariable表示方法的id是从路径中获得的  注意这种方法
        User user = userService.getById(id);
        if(user != null){
            return R.success(user);
        }
        return R.error("没有查询到对应教师信息");
    }

    @PutMapping()
    public R<String> update(HttpServletRequest request, @RequestBody User user){
        userService.updateById(user);      //这个updateByid是已经封装好的直接用的，但是为什么会选择更新部分信息呢，更新时间更新人还需要自己去设置
        return R.success("学生信息修改成功");
    }

    //老师登录账号，显示老师教授的课程
    @GetMapping("/pageCQ")
    public R<Page> pageCQ(int page, int pageSize, String name,HttpServletRequest request){
        Page<Course> pageInfo = new Page<>(page,pageSize);
        String  teacherId = request.getSession().getAttribute("user").toString();
        User teacher = userService.getById(teacherId);
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getTecName,teacher.getName());
        queryWrapper.like(name != null,Course::getName,name);
        courseService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    //老师查看教授课程的学生的个人信息,吧选了这个课的所有学生信息返回回去
    @GetMapping("/pageST")
    public R<Page> pageST(int page, int pageSize, Long id){
        Page<Score> scorePage = new Page<>(page,pageSize);
        Page<User> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getCourId,id.toString());
        scoreService.page(scorePage,queryWrapper);
        List<Score> records = scorePage.getRecords();

        BeanUtils.copyProperties(scorePage,pageInfo,"records");
        List<User> userList = records.stream().map((item)->{
            LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(User::getId,item.getStuId());
            User one = userService.getOne(queryWrapper1);
            return one;
        }).collect(Collectors.toList());

        pageInfo.setRecords(userList);

        return R.success(pageInfo);
    }

    //教师打分
    @PutMapping("/score")
    public R<String> score(@RequestBody Score score){
        UpdateWrapper<Score> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("stu_id",score.getStuId())
                .eq("cour_id",score.getCourId())
                .set("score",score.getScore());
        scoreService.update(queryWrapper);
        return R.success("分数添加成功");
    }
}
