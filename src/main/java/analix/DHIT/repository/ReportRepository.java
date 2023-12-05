package analix.DHIT.repository;

import analix.DHIT.model.Report;

import java.util.List;

public interface ReportRepository {
    Report findById(int reportId);
    void save(Report report);

    void deleteById(int reportId);
    void update(Report report);

    //追記*****************************************************
    //報告一覧表示----------------------------------
    List<Report> findAll(int employeeCode);

}
