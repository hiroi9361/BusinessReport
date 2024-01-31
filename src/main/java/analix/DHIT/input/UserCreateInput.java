package analix.DHIT.input;

import analix.DHIT.model.TaskLog;
import analix.DHIT.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


public class UserCreateInput {
    private Integer employeeCode;
    private String name;
    private String email;

    private String password;
    private String role;
    private MultipartFile icon;
    private String convertIcon;

    private List<UserAllCreateInput> userAllCreateInputs = new ArrayList<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<UserAllCreateInput> getUserAllCreateInputs() {
        return userAllCreateInputs;
    }

    public void setUserAllCreateInputs(List<UserAllCreateInput> userAllCreateInputs) {
        this.userAllCreateInputs = userAllCreateInputs;
    }
}