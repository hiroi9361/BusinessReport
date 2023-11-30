package analix.DHIT.input;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import analix.DHIT.model.User;


public class UserEditInput {

    private int employeeCode;
    private String name;
    private String password;
    private String role;
    private MultipartFile icon;
    private String convertIcon;

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getConvertIcon() {
        return convertIcon;
    }

    public void setIcon(MultipartFile icon) {
        this.icon = icon;
    }

    public MultipartFile getIcon() {
        return icon;
    }

    public void setConvertIcon(String convertIcon) {
        this.convertIcon = convertIcon;
    }
}
