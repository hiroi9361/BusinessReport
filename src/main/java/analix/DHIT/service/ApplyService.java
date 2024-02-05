package analix.DHIT.service;

import analix.DHIT.input.ReportSortInput;
import analix.DHIT.mapper.ApplyMapper;
import analix.DHIT.input.ApplySortInput;
import analix.DHIT.model.Apply;
import analix.DHIT.model.Report;
import analix.DHIT.repository.ApplyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ApplyService {
    private final ApplyMapper applyMapper;
    private final ApplyRepository applyRepository;

    public ApplyService(ApplyMapper applyMapper, ApplyRepository applyRepository) {
        this.applyMapper = applyMapper;
        this.applyRepository = applyRepository;
    }

    public int create(
            int employeeCode,
            int applicationType,
            int attendanceType,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime,
            String reason,
            int approval,
            LocalDateTime createdDate) {

        Apply newApply = new Apply();

        newApply.setEmployeeCode(employeeCode);
        newApply.setApplicationType(applicationType);
        newApply.setAttendanceType(attendanceType);
        newApply.setStartDate(startDate);
        newApply.setEndDate(endDate);
        newApply.setStartTime(startTime);
        newApply.setEndTime(endTime);
        newApply.setReason(reason);
        newApply.setApproval(approval);
        newApply.setCreatedDate(createdDate);

        this.applyRepository.save(newApply);
        return newApply.getId();
    }

    public  List<Apply> getfindAll(int employeeCode){
        List<Apply> applys = applyRepository.findAll(employeeCode);
        if(applys != null) {
            //申請日付を軸に降順に並び替える
            Collections.sort(applys, Comparator.comparing(Apply::getCreatedDate).reversed());
            // createdDate をフォーマットする
            applys.forEach(apply -> apply.setFormattedCreatedDate(formatDateTime(apply.getCreatedDate())));
        }
        return  applys;
    }

    public String searchId(int employeeCode, LocalDateTime createdDate) {
        return applyMapper.selectIdByEmployeeCodeAndCreatedDate(employeeCode, createdDate);
    }

    public  List<Apply> getSortApply(ApplySortInput applySortInput) {
        List<Apply> applys = applyRepository.sortApply(applySortInput);
        Collections.sort(applys, Comparator.comparing(Apply::getCreatedDate).reversed());
        return  applys;
    }


//    public boolean count(int applyId){
//        int count = applyRepository.count(applyId);
//        return count > 0;
//    }
=======
    public Apply findById(int applyId) {
        return this.applyRepository.findById(applyId);
    }

    public void deleteById(int applyId){ this.applyRepository.deleteById(applyId); }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return dateTime.format(formatter);
    }

}
