package analix.DHIT.repository;

import analix.DHIT.input.ReportSortInput;
import analix.DHIT.mapper.ReportMapper;
import analix.DHIT.model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlReportRepository implements ReportRepository {
    private final ReportMapper reportMapper;

    public MysqlReportRepository(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public Report findById(int reportId) {
        return this.reportMapper.SelectById(reportId);
    }

    @Override
    public void save(Report report) {
        this.reportMapper.insertReport(report);
    }

    @Override
    public void deleteById(int reportId) {
        this.reportMapper.deleteById(reportId);
    }

    @Override
    public void update(Report report) {
        this.reportMapper.updateReport(report);
    }

    //追記*****************************************************
    //報告一覧表示----------------------------------
    @Override
    public  List<Report> findAll(int employeeCode){
        return this.reportMapper.selectAll(employeeCode);
    };

    @Override
    public List<Report> sortReport(ReportSortInput reportSortInput) {
        return this.reportMapper.sortReport(reportSortInput);
    }
}
