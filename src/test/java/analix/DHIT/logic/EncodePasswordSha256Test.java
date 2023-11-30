package analix.DHIT.logic;

import analix.DHIT.input.UserCreateInput;
import analix.DHIT.input.UserEditInput;
import analix.DHIT.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EncodePasswordSha256Test {

    @Autowired
    EncodePasswordSha256 sut = new EncodePasswordSha256();
    @Test
    void testUserCreateInputEncodePasswordSha256()
    {
        UserCreateInput userCreateInput = new UserCreateInput();
        userCreateInput.setPassword("hikida");

        String expectedHash = "fe8fbe4b38777a71e0cedf0b6502a4bdd6fc9521ddf4c1e39c72250bfcf56c0d";
        String actualHash = sut.encodePasswordSha256(userCreateInput.getPassword());
        assertEquals(expectedHash,actualHash);
        //assertThat(userCreateInput.equals("fe8fbe4b38777a71e0cedf0b6502a4bdd6fc9521ddf4c1e39c72250bfcf56c0d"));
    }
    @Test
    void testUserEditInputEncodePasswordSha256()
    {
        UserEditInput userEditInput = new UserEditInput();
        userEditInput.setPassword("hikida");

        String expectedHash = "fe8fbe4b38777a71e0cedf0b6502a4bdd6fc9521ddf4c1e39c72250bfcf56c0d";
        String actualHash = sut.encodePasswordSha256(userEditInput.getPassword());
        assertEquals(expectedHash,actualHash);
    }

//    @ParameterizedTest
//    @CsvSource({
//            "PADLOCK,1024",
//            "BUTTON,10000",
//            "DIAL,30000",
//            "FINGER,1000000"
//    })
//    void getItem(String keyType,int loopCount)
//    {
//        StrongBox<Cleric> sut = new StrongBox<>(KeyType.valueOf(keyType));
//        Cleric cleric = new Cleric("ゆう",10,10);
//        sut.put(cleric);
//        //sut.get();
//        for (int i = 1; i <=loopCount-1 ; i++) {
//            assertNull(sut.get());
//
//        }
//        assertSame(sut.get(), cleric);
//    }

}