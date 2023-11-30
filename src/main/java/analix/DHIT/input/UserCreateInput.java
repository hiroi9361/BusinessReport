package analix.DHIT.input;

import analix.DHIT.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;


public class UserCreateInput {
    private Integer employeeCode;
    private String name;
    private String password;
    private String role;
    private MultipartFile icon;
    private String convertIcon;

    public Integer getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(Integer employeeCode) {
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

    public MultipartFile getIcon() {
        return icon;
    }

    public void setIcon(MultipartFile icon) {
        this.icon = icon;
    }

    public String getConvertIcon() {
        return convertIcon;
    }

    public void setConvertIcon(String convertIcon) {
        this.convertIcon = convertIcon;
    }
}
