package com.example.xiaomi.journal.Database.CallableRequests;

import com.example.xiaomi.journal.Database.StudentsDao;
import com.example.xiaomi.journal.Student;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class InsertStudCallable implements Callable<Long> {

    private StudentsDao studentsDao;
    private Student student;

    public InsertStudCallable(StudentsDao studentsDao, Student student){
        this.studentsDao = studentsDao;
        this.student = student;
    }

    @Override
    public Long call() throws Exception {
        return studentsDao.insert(student);
    }
}
