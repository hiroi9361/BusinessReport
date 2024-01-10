package analix.DHIT.service;


import analix.DHIT.input.SettingInput;
import analix.DHIT.mapper.SettingMapper;
import analix.DHIT.model.Report;
import analix.DHIT.model.Setting;
import analix.DHIT.repository.SettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

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

   //就業時間を取得
   public Setting getSettingTime(int employeeCode){
       Setting setting = settingRepository.getSetting(employeeCode);
       return setting;
   }
   //就業時間を更新
   public void update(SettingInput settingInput,int employeeCode){
       Setting setting = this.settingMapper.selectSettingTime(employeeCode);

       setting.setStartTime(settingInput.getStartTime());
       setting.setEndTime(settingInput.getEndTime());
       setting.setEmployeeCode(employeeCode);

       this.settingRepository.update(setting);
   }
   //就業時間を作成
   //就業時間を削除
}
