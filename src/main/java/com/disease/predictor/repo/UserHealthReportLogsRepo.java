package com.disease.predictor.repo;

import com.disease.predictor.entity.UserHealthReportLogs;
import com.disease.predictor.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHealthReportLogsRepo extends JpaRepository<UserHealthReportLogs,Long> {
    UserHealthReportLogs findTop1ByUserId(Users users);

    List<UserHealthReportLogs> findByUserId(Users users);
}
