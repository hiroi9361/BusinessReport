package analix.DHIT.repository;

//import analix.DHIT.input.ReportSortInput;
import analix.DHIT.input.ApplySortInput;
import analix.DHIT.input.ReportSortInput;
import analix.DHIT.mapper.ApplyMapper;
import analix.DHIT.model.Apply;
import analix.DHIT.model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlApplyRepository implements ApplyRepository {
    private final ApplyMapper applyMapper;

    public MysqlApplyRepository(ApplyMapper applyMapper) {
        this.applyMapper = applyMapper;
    }

    @Override
    public Apply findById(int applyId) {
        return this.applyMapper.SelectById(applyId);
    }

    @Override
    public void save(Apply apply) {
        this.applyMapper.insertApply(apply);
    }

    @Override
    public List<Apply> findAll(int employeeCode) {
        return null;
    }

    @Override
    public List<Apply> sortApply(ApplySortInput applySortInput) {
        return this.applyMapper.sortApply(applySortInput);
    }


}
