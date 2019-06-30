package com.example.xiaomi.journal.Database.CallableRequests;

import com.example.xiaomi.journal.Database.StudentsDao;

import java.util.concurrent.Callable;

public class GetStudIdByNameCallable implements Callable<Integer> {

    private StudentsDao studentsDao;
    private String name;

    public GetStudIdByNameCallable(StudentsDao studentsDao, String name) {
        this.studentsDao = studentsDao;
        this.name = name;
    }

    @Override
    public Integer call() throws Exception {
        return studentsDao.getIdByName(name);
    }
}
