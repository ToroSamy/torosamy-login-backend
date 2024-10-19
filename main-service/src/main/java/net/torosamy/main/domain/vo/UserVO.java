package net.torosamy.main.domain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserVO {
    private Long id;

    private String username;

    private String qq;

    private String token;
}
