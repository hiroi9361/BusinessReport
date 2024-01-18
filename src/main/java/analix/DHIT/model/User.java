package analix.DHIT.model;

import jakarta.persistence.*;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="user")
public class User implements Serializable {

    @Id
    @Column(name="employee_code")
    private int employeeCode;

    @Column(name="name", length=50)
    private String name;

    private String email;
    private String password;
    private String role;
    private String icon;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Assignment> assignments;

    public User() {
    }

    public int getEmployeeCode()
    {
        return employeeCode;
    }
    public void setEmployeeCode(int employeeCode)
    {
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> roleList() {
        return List.of(getRole());
    }

    private boolean readReport = true;
    public boolean getReadReport(){
        return readReport;
    }
    public void setReadReport(boolean readReport){
        this.readReport = readReport;
    }

}

