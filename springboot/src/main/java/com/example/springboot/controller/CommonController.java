package com.example.springboot.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.common.R;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    private UserService userService;

    @Value("${student.path}")
    private String basePath;

    @PostMapping("/login")
    public R<User> login(@RequestBody User user, HttpServletRequest request){
        log.info(user.toString());
        //1.将页面提交过来的密码做md5加密处理
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名和角色查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        queryWrapper.eq(User::getRole,user.getRole());
        User one = userService.getOne(queryWrapper);
        //3.如果没有查询到则返回登录失败的结果
        if(one == null){
            return R.error("登录失败");
        }
        //4.密码比对，如果不一致则返回登录失败结果
        if(!one.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //5.查看当前状态，如果员工为禁用状态，则返回员工已禁用结果
        if((one.getRole().equals("1") || one.getRole().equals("2")) && one.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //6.登陆成功，将用户id存入Session并返回登录结果
        request.getSession().setAttribute("user",one.getId());
        return R.success(one);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

    //文件上传和下载
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
