package net.torosamy.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.torosamy.main.domain.dto.UserDTO;
import net.torosamy.main.domain.po.User;

public interface IUserService extends IService<User> {
    User login(UserDTO userDTO);
    User register(UserDTO userDTO);
}
