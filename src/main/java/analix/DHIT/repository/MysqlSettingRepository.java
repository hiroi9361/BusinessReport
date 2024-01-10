package analix.DHIT.repository;

import analix.DHIT.mapper.SettingMapper;
import analix.DHIT.mapper.UserMapper;
import analix.DHIT.model.Setting;
import analix.DHIT.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class MysqlSettingRepository implements SettingRepository {

    private final SettingMapper settingMapper;
    public MysqlSettingRepository(SettingMapper settingMapper){
        this.settingMapper=settingMapper;
    }
    @Override//就業時間を取得
    public Setting getSetting(int employeeCode){
        return this.settingMapper.selectSettingTime(employeeCode);
    }
    @Override//終業時間を更新
    public void update(Setting setting){
        this.settingMapper.updateSetting(setting);
    }
    @Override//新規作成
    public void save(Setting setting){
        this.settingMapper.insertSetting(setting);
    }
    @Override//削除
    public void deleteById(int employeeCode){
        this.settingMapper.deleteById(employeeCode);
    }

}
