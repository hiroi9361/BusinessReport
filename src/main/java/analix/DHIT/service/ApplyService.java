package analix.DHIT.service;

import analix.DHIT.mapper.ApplyMapper;
import analix.DHIT.model.Apply;
import analix.DHIT.repository.ApplyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

}
