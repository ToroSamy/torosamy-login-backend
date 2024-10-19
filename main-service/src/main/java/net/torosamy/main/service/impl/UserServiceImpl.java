package net.torosamy.main.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import net.torosamy.main.constant.MessageConstant;
import net.torosamy.main.domain.dto.UserDTO;
import net.torosamy.main.domain.po.User;

import net.torosamy.main.exception.PasswordErrorException;
import net.torosamy.main.exception.UserNotFoundException;
import net.torosamy.main.mapper.UserMapper;
import net.torosamy.main.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    public User login(UserDTO userDTO) {
        User user = lambdaQuery().eq(User::getUsername, userDTO.getUsername()).one();

        //处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new UserNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        if (!userDTO.getPassword().equals(user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }


        return user;
    }

    @Override
    public User register(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        return user;
    }

}
