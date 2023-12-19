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
    @Override
    public Setting getSetting(){
        return this.settingMapper.selectSettingTime();
    }
    @Override
    public void update(Setting setting){
        this.settingMapper.updateSetting(setting);
    }
}
