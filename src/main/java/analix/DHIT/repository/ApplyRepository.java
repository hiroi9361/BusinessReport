package analix.DHIT.repository;

import analix.DHIT.input.ApplySortInput;
import analix.DHIT.model.Apply;

import java.util.List;

public interface ApplyRepository {
    Apply findById(int applyId);
    void save(Apply apply);

//    void deleteById(int applyId);
//    void update(Apply apply);

    //追記*****************************************************
    //報告一覧表示----------------------------------
    List<Apply> findAll(int employeeCode);

    //ソート検索結果
    List<Apply> sortApply(ApplySortInput applySortInput);

//    int count(int applyId);

    //申請削除
    void deleteById(int applyId);

}
