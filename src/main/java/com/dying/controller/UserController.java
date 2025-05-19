package com.dying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.constant.UserConstant;
import com.dying.domain.User;
import com.dying.domain.request.UserLoginRequest;
import com.dying.domain.request.UserRegisterRequest;
import com.dying.exception.BusinessException;
import com.dying.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author 666
 */
@RestController()
@RequestMapping("/user")
@CrossOrigin(origins = {"http://123.249.124.78:80"},allowCredentials = "true")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(userPassword) || StringUtils.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空");
        }
        long l = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(l);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if(currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User saftyUser = userService.getSaftyUser(user);
        return ResultUtils.success(saftyUser);
    }


    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户或密码为空");
        }
        log.info("登陆成功");
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> getUser(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTO,"权限不足");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_name", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSaftyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTO,"权限不够");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    public Integer userLogout(HttpServletRequest request) {
        if(request==null){
            return null;
        }
        return userService.userLogout(request);

    }

    public boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null || user.getUserRole() != UserConstant.ADMIN_ROLE) {
            return false;
        }
        return true;
    }

}
