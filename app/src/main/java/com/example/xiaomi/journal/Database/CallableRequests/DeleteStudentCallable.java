package com.example.xiaomi.journal.Database.CallableRequests;

import com.example.xiaomi.journal.Database.StudentsDao;
import com.example.xiaomi.journal.Student;

import java.util.concurrent.Callable;

public class DeleteStudentCallable implements Callable<Integer> {
    private StudentsDao studentsDao;
    private Student student;

    public DeleteStudentCallable(StudentsDao studentsDao, Student student) {
        this.studentsDao = studentsDao;
        this.student = student;
    }

    @Override
    public Integer call() throws Exception {
        studentsDao.delete(student);
        return 0;
    }
}
