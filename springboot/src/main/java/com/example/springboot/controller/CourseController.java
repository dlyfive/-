package com.example.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.R;
import com.example.springboot.entity.Course;
import com.example.springboot.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Course> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Course::getName,name);
        queryWrapper.orderByAsc(Course::getType);
        courseService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Course course){ //加@Requestbody是因为前端发来的是json格式的数据 后端不能直接用 加了才能正常封装
//        //设置初始密码同时进行md5加密
//        course.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        courseService.save(course);

        return R.success("新增课程成功");
    }

    @PutMapping()
    public R<String> update(HttpServletRequest request,@RequestBody Course course){

        courseService.updateById(course);      //这个updateByid是已经封装好的直接用的，但是为什么会选择更新部分信息呢，更新时间更新人还需要自己去设置
        return R.success("课程信息修改成功");
    }

    //根据id来查询员工信息
    @GetMapping("/{id}")
    public R<Course> getById(@PathVariable Long id){   //@PathVariable表示方法的id是从路径中获得的  注意这种方法
        log.info("根据id查询员工信息");
        Course course = courseService.getById(id);
        if(course != null){
            return R.success(course);
        }
        return R.error("没有查询到对应课程信息");
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        courseService.removeById(ids);
        return R.success("删除课程成功");
    }
}
