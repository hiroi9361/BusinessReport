package analix.DHIT.repository;

import analix.DHIT.model.Setting;
import analix.DHIT.model.User;

import java.util.List;

public interface SettingRepository {

    Setting getSetting();
    void update(Setting setting);
}
