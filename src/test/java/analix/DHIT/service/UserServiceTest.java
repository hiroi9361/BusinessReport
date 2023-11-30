//package analix.DHIT.service;
//
//import analix.DHIT.input.UserCreateInput;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class UserServiceTest {
//    @Autowired UserService sut;
//
//    //結論:passwordのsha256処理はuserSeriveではできない！
//    @Test
//    void checkConvert()
//    {
//        UserCreateInput u = new UserCreateInput();
//        u.setPassword("hikida");
//        assertThat(u.equals("fe8fbe4b38777a71e0cedf0b6502a4bdd6fc9521ddf4c1e39c72250bfcf56c0d"));
//    }
//
//}