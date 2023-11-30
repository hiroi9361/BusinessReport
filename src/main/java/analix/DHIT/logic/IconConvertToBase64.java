package analix.DHIT.logic;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class IconConvertToBase64 {

    public String iconConvertToBase64(MultipartFile input) {
        try {
            byte[] iconfileBytes = input.getBytes();
            return Base64.getEncoder().encodeToString(iconfileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}