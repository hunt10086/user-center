package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.User;
import com.dying.exception.BusinessException;
import com.dying.service.UserService;
import com.dying.mapper.UserMapper;
import com.dying.utils.MD5Utils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Pattern;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 666
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-05-10 13:59:54
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "Dying";
    //private static final String USER_LOGIN_STATE = "userLoginState";
    @Override
    public long userRegister(String userAccount, String password, String checkPassword) {
        //账号密码不能为空
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码为空");
        }
        if (StringUtils.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"第二次输入密码为空");
        }
        //账号大于四位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }
        //密码不小于八位
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不小于八位");
        }
        //账号不能重复
        User flag = userMapper.findAllByUserAccountBoolean(userAccount);
        if (flag!=null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能重复");
        }
        //不允许出现特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        String str = Pattern.compile(regEx).matcher(userAccount).replaceAll("").trim();
        if (!userAccount.equals(str)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不允许出现特殊字符");
        }

        if(!checkPassword.equals(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不相同");
        }

        String  newpassword = MD5Utils.string2MD5(SALT + password + SALT);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newpassword);
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        userMapper.insert(user);
        log.info("用户创建成功");
        return 0;
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //账号密码不能为空
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码为空");
        }
        //账号大于四位z
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号大于四位");
        }
        //密码不小于八位
        if (password.length() < 8) {
          throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码小于八位");
        }
        //不允许出现特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        String str = Pattern.compile(regEx).matcher(userAccount).replaceAll("").trim();
        if (!userAccount.equals(str)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不允许出现特殊字符");
        }
        //检验用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount).eq("user_password",MD5Utils.string2MD5(SALT+password+SALT));
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            log.info("用户或密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户或密码错误");
        }
        //用户脱敏
        User saftyUser=getSaftyUser(user);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,saftyUser);

        return saftyUser;
    }
    @Override
    public User getSaftyUser(User originUser) {
        if(originUser==null){
            return null;
        }
        User saftyUser = new User();
        saftyUser.setId(originUser.getId());
        saftyUser.setUserName(originUser.getUserName());
        saftyUser.setUserAccount(originUser.getUserAccount());
        saftyUser.setAvatarUrl(originUser.getAvatarUrl());
        saftyUser.setGender(originUser.getGender());
        saftyUser.setPhone(originUser.getPhone());
        saftyUser.setEmail(originUser.getEmail());
        saftyUser.setUserStatus(originUser.getUserStatus());
        saftyUser.setCreateTime(originUser.getCreateTime());
        saftyUser.setUserRole(originUser.getUserRole());
        return saftyUser;
    }

    @Override
    public boolean userUpdate(User user) {
        if(user==null){
            return false;
        }
        userMapper.updateById(user);
        return true;
    }




    @Override
    public int userLogout(HttpServletRequest request) {
        if(request == null){
            return 0;
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




