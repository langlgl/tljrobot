package com.jdragon.tljrobot.tlj.controller.home;

import com.alibaba.nacos.common.util.Md5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jdragon.tljrobot.tlj.mappers.UserMapper;
import com.jdragon.tljrobot.tlj.pojo.User;
import com.jdragon.tljrobot.tljutils.DateUtil;
import com.jdragon.tljrobot.tljutils.Local;
import com.jdragon.tljrobot.tljutils.Result;
import com.jdragon.tljrobot.tljutils.TimingMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/home")
@Api(tags = "无条件操作")
public class Security {
    @Autowired
    private UserMapper userMapper;
    @GetMapping(value = "/login")
    @ApiOperation("进入登录界面")
    public String Login(){
        return "login";
    }
    @GetMapping(value = "/register")
    @ApiOperation(value = "进入注册界面")
    public String register(){
        return "register";
    }
    @PostMapping("loginState/{userId}")
    @ApiOperation(value = "验证登录状态")
    public Result LoginState(@PathVariable String userId){
        if(Local.getSession(userId)!=null) {
            return Result.success("已登录");
        } else {
            return Result.success("未登录");
        }
    }
    @PostMapping(value = "/login/{username}/{password}")
    @ApiOperation(value = "登录接口")
    @ResponseBody
    public Result Login(@ApiParam(name = "username",value = "用户名")@PathVariable("username") String username,
                        @ApiParam(name = "password",value = "登录密码")@PathVariable("password") String password){
        password = Md5Utils.getMD5(password.getBytes());
        User user = userMapper.selectOne(new QueryWrapper<User>().eq(User.Def.USERNAME,username));
        if(user==null){
            return Result.success("无该用户");
        }else if(!user.getPassword().equals(password)){
            return Result.success("密码错误");
        }else{
            String userId = getToken(username);
            if(userId==null) {
                userId = Local.login(user);
            }
            user.setLastLoginDate(DateUtil.now());
            user.setToken(userId);
            userMapper.updateById(user);
            return Result.success("登录成功").setResult(userId);
        }
    }
    @PostMapping(value = "/loginMD5/{username}/{password}")
    @ApiOperation(value = "登录接口")
    @ResponseBody
    public Result LoginMD5(@ApiParam(name = "username",value = "用户名")@PathVariable("username") String username,
                        @ApiParam(name = "password",value = "登录密码")@PathVariable("password") String password){
        User user = userMapper.selectOne(new QueryWrapper<User>().eq(User.Def.USERNAME,username));
        if(user==null){
            return Result.success("无该用户");
        }else if(!user.getPassword().equals(password)){
            return Result.success("密码错误");
        }else{
            String userId = getToken(username);
            if(userId==null) {
                userId = Local.login(user);
            }
            user.setToken(userId);
            userMapper.updateById(user);
            return Result.success("登录成功").setResult(userId);
        }
    }
    public String getToken(String username){
        TimingMap<String, Object> loginMap = Local.getTokenMap();
        for(Map.Entry<String,Object> entry:loginMap.entrySet()){
            User temp = (User)entry.getValue();
            if(temp.getUsername().equals(username)){
                return entry.getKey();
            }
        }
        return null;
    }
    @PostMapping(value = "/logout/{userId}")
    @ApiOperation("退出登录")
    @ResponseBody
    public Result logout(@ApiParam(name = "userId",value = "使用userId退出")@PathVariable String userId){
        User user = (User)Local.getSession(userId);
        if(user!=null) {
            user.setToken("");
            userMapper.updateById(user);
//            Local.logout(userId);
        }
        return Result.success("退出成功");
    }

    @PostMapping(value = "/register/{username}/{password}")
    @ApiOperation(value = "注册接口")
    @ResponseBody
    public Result register(@ApiParam(name = "username",value = "用户名")@PathVariable String username,
                           @ApiParam(name = "password",value = "密码")@PathVariable String password){
        password = Md5Utils.getMD5(password.getBytes());
        User user = userMapper.selectOne(new QueryWrapper<User>().eq(User.Def.USERNAME,username));
        if(user==null){
            user = new User(username, password);
            if(userMapper.insert(user)>0)
                return Result.success("注册成功");
            else
                return Result.error("注册失败");
        }else {
            return Result.error("已存在");
        }
    }

    public static void main(String[] args) {
        String md5 = Md5Utils.getMD5("mhs4560".getBytes());
        System.out.println(md5);
    }
}
