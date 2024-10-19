package net.torosamy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.torosamy.main.domain.po.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<User> {
}
