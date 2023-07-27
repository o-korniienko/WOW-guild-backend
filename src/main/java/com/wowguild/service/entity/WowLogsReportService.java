package com.wowguild.service.entity;

import com.wowguild.entity.rank.Report;
import com.wowguild.repos.WOWLogsReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WowLogsReportService implements EntityService<Report> {

    private final WOWLogsReportRepo reportRepo;

    @Override
    public void save(Report report) {
        reportRepo.save(report);
    }

    @Override
    public List<Report> getAll() {
        return reportRepo.findAll();
    }

    @Override
    public List<Report> saveAll(List<Report> reports) {
        return reportRepo.saveAll(reports);
    }

    @Override
    public List<Report> findAll() {
        return reportRepo.findAll();
    }

    @Override
    public void delete(Report report) {
        reportRepo.delete(report);
    }

    @Override
    public List<Report> sort(List<Report> reports, Comparator<Report> comparator1, Comparator<Report> comparator2) {
        return null;
    }
}
