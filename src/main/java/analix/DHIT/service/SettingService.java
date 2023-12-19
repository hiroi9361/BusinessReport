package analix.DHIT.service;


import analix.DHIT.input.SettingInput;
import analix.DHIT.mapper.SettingMapper;
import analix.DHIT.model.Setting;
import analix.DHIT.repository.SettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//DBに接続するための処理を記述するところ

@Service
@Transactional(rollbackFor = Exception.class)
public class SettingService {
   private final SettingMapper settingMapper;
   private final SettingRepository settingRepository;

   public SettingService(SettingMapper settingMapper, SettingRepository settingRepository){
       this.settingMapper=settingMapper;
       this.settingRepository=settingRepository;
   }

   public Setting getSettingTime(){
       Setting setting = settingRepository.getSetting();
       return setting;
   }
   public void update(SettingInput settingInput){
       Setting setting = this.settingMapper.selectSettingTime();

       setting.setStartTime(settingInput.getStartTime());
       setting.setEndTime(settingInput.getEndTime());

       this.settingRepository.update(setting);
   }
}
