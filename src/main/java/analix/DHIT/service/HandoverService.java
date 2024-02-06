package analix.DHIT.service;

import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskHandoverCreateInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.TaskLog;
import analix.DHIT.repository.HandoverRepository;
import analix.DHIT.repository.TaskLogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HandoverService {
   private final HandoverRepository handoverRepository;

   public HandoverService(HandoverRepository handoverRepository) {
       this.handoverRepository = handoverRepository;
   }

   public void save(TaskHandoverCreateInput taskHandoverCreateInput){
       this.handoverRepository.save(taskHandoverCreateInput);
   }
   public void updateByDeleteKey(boolean deleteKey, int reportId){
       this.handoverRepository.updateByDeleteKey(deleteKey, reportId);
   }
   public List<TaskLog> selectTaskByAfter(int employeeCode){
       return this.handoverRepository.selectTaskByAfter(employeeCode);
   }
   public List<TaskLog> selectTaskByBefore(int employeeCode){
       return this.handoverRepository.selectTaskByBefore(employeeCode);
   }
   public boolean countByAfter(int employeeCode){
       int count = handoverRepository.countByAfter(employeeCode);
       return count > 0;
   }
}
