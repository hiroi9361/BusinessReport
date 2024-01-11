package analix.DHIT.mapper;

import analix.DHIT.input.UserCreateInput;
import analix.DHIT.input.UserEditInput;
import analix.DHIT.model.User;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE employee_code = #{employeeCode}")
    User findByEmployeeCode(int employeeCode);

    @Select("SELECT * FROM user where role = 'USER'")
    List<User> selectAllMember();

    @Select("SELECT * FROM user WHERE name LIKE CONCAT('%', #{searchCharacters}, '%') and role = 'USER'")
    List<User> selectMemberBySearchCharacters(@Param("searchCharacters") String searchCharacters);

    //employeeCodeの重複をチェック
    @Select("SELECT employee_Code FROM user WHERE employee_Code = #{employeeCode};")
    Integer duplicateCode(int employeeCode);

    //社員情報をDBへ
    @Insert("INSERT INTO USER(employee_code,name,password,role,icon)" + "VALUES(#{employeeCode},#{name},#{password},#{role},#{convertIcon})")
    void insertEmployeeInformation(UserCreateInput userCreateInput);

    @Select("SELECT * FROM user")
    List<User> selectAllEmployeeInfo();

    //削除機能の最後に使うuser削除
    @Delete("DELETE FROM user WHERE employee_code = #{employeeCode}")
    void deleteById(int employeeCode);

    //既存ユーザー編集
    @Update("UPDATE user SET name=#{name}, password=#{password}, role=#{role}, icon=#{convertIcon} WHERE employee_code=#{employeeCode}")
    void editEmployeeInfomation(UserEditInput userEditInput);

    @Select("SELECT name FROM user WHERE employee_code = #{employeeCode}")
    String getUserName(int employeeCode);

    @Select("SELECT * FROM user WHERE name LIKE CONCAT('%', #{searchWords}, '%')")
    List<User> getUserByName(@Param("searchWords")String searchWords);

    @Select("SELECT * FROM user WHERE role LIKE CONCAT('%', #{searchWords}, '%')")
    List<User> getUserByRole(@Param("searchWords")String searchWords);

    @Select("SELECT * FROM user WHERE employee_code LIKE CONCAT(#{searchWords}, '%')")
    List<User> getUserByCode(@Param("searchWords")int searchWords);

    // /////////// 2024/01/10 START 富山 //////////
    @Select("SELECT a.is_manager " +
            "FROM user u JOIN assignment a ON u.employee_code = a.employee_code " +
            "WHERE u.employee_code = #{employeeCode}")
    Boolean isManager(@Param("employeeCode") int employeeCode);

    // /////////// 2024/01/10 END 富山 //////////

}
