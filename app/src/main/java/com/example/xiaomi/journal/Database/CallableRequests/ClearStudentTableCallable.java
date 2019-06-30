package com.example.xiaomi.journal.Database.CallableRequests;

import com.example.xiaomi.journal.Database.StudentsDao;

import java.util.concurrent.Callable;

public class ClearStudentTableCallable implements Callable<Integer> {

    private StudentsDao studentsDao;

    ClearStudentTableCallable(StudentsDao studentsDao) {
        this.studentsDao = studentsDao;
    }

    @Override
    public Integer call() throws Exception {
        studentsDao.clearTable();
        return 0;
    }
}
