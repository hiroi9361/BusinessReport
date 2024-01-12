package analix.DHIT.model;

import jakarta.persistence.*;

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
    private String password;
    private String role;
    private String icon;

    private String mail;

    private List<Integer> myIntegers;


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


    public String getMail(){return mail;}

    public void setMail(String mail){this.mail= mail;}

    public List<String> roleList() {
        return List.of(getRole());
    }

    public List<Integer> getMyIntegers() {
        return myIntegers;
    }

    public void setMyIntegers(List<Integer> myIntegers) {
        this.myIntegers = myIntegers;
    }
}

