package analix.DHIT.repository;

import analix.DHIT.model.Report;
import analix.DHIT.model.Setting;
import analix.DHIT.model.User;

import java.util.List;

public interface SettingRepository {

//    就業時間を取得
    Setting getSetting(int employeeCode);

//    終業時間を更新
    void update(Setting setting);

//    新規作成
    void save(Setting setting);

//    削除
    void deleteById(int employeeCode);
}
