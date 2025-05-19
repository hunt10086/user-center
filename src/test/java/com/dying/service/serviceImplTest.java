package com.dying.service;


import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.dying.mapper")
public class serviceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void Registertest() {
        long result = userService.userRegister("", "", "");
        System.out.println(result);

        long result2 = userService.userRegister("Dyi", "123456", "123456");
        System.out.println(result2);


        long result3 = userService.userRegister("Dying", "123456", "123456");
        System.out.println(result3);

        long result4 = userService.userRegister("Dyin*&$^g", "12345678", "12345678");
        System.out.println(result4);

        long result5 = userService.userRegister("dayligh", "12345678", "12345678");
        System.out.println(result5);

    }


//    @Test
//    public void Logintest() {
//        int result = userService.userLogin("", "123456",);
//        System.out.println(result);
//        int result2 = userService.userLogin("Dyi", "123456");
//        System.out.println(result2);
//        int result3 = userService.userLogin("Dying", "123456");
//        System.out.println(result3);
//        int result4 = userService.userLogin("Dyin*&$^g", "12345678");
//        System.out.println(result4);
//        int result5 = userService.userLogin("dayligh", "12345678");
//        System.out.println(result5);
//    }
}
