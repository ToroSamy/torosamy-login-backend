package net.torosamy.main.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.torosamy.main.domain.dto.UserDTO;
import net.torosamy.main.domain.po.User;
import net.torosamy.main.domain.vo.Result;
import net.torosamy.main.domain.vo.UserVO;
import net.torosamy.main.exception.UserNotFoundException;
import net.torosamy.main.service.IUserService;
import net.torosamy.main.service.WebRedisService;
import net.torosamy.main.utils.WebSecurityUtil;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final WebRedisService webRedisService;

    @PostMapping("/register")
    public Result registerUser(@RequestBody UserDTO userDTO) {
        log.info("a user tries to register a torosamy world profile: {}", userDTO);

        User user = userService.register(userDTO);


        userService.save(user);
        log.info("a user register a torosamy world profile successfully: {}", user);


        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserVO> loginUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        log.info("a user tries to login torosamy world: {}",userDTO);

        User user = userService.login(userDTO);
        String token = WebSecurityUtil.genLoginToken(user);
        webRedisService.saveUserToken(user.getUsername(),token);

        UserVO userVO = new UserVO()
                .setId(user.getId())
                .setQq(user.getQq())
                .setUsername(user.getUsername())
                .setToken(token);
        log.info("a user logins torosamy world successfully: {}", userVO);
        return Result.success(userVO);
    }

    @PutMapping("/logout/{id}")
    public Result logoutUser(@PathVariable Long id) {
        log.info("a user: {} tries to logout", id);
        User user = userService.getById(id);

        if (user == null) {throw new UserNotFoundException();}
        webRedisService.removeUserToken(user.getUsername());


        return Result.success();
    }

}