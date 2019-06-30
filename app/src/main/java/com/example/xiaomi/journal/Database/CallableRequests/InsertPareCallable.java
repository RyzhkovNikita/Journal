package com.example.xiaomi.journal.Database.CallableRequests;

import com.example.xiaomi.journal.Database.PareDao;
import com.example.xiaomi.journal.Pare;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class InsertPareCallable implements Callable<Integer> {

    private PareDao pareDao;
    private Pare pare;

    public InsertPareCallable(PareDao pareDao, Pare pare) {
        this.pareDao = pareDao;
        this.pare = pare;
    }

    @Override
    public Integer call() throws Exception {
        pareDao.insert(pare);
        return 0;
    }
}
