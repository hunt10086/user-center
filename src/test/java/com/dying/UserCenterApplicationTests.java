package com.dying;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dying.domain.User;
import com.dying.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.List;

@SpringBootTest
@MapperScan("com.dying.mapper")
class UserCenterApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    void delete() {
        long id = userMapper.deleteById(2);
        System.out.println(id);
    }

}
