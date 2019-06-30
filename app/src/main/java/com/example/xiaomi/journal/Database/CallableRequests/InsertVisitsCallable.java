package com.example.xiaomi.journal.Database.CallableRequests;

import com.example.xiaomi.journal.Database.VisitDao;
import com.example.xiaomi.journal.Visit;

import java.util.List;
import java.util.concurrent.Callable;

public class InsertVisitsCallable implements Callable<Integer> {
    private VisitDao visitDao;
    private List<Visit> visitList;

    public InsertVisitsCallable(VisitDao visitDao, List<Visit> visitList) {
        this.visitDao = visitDao;
        this.visitList = visitList;
    }

    @Override
    public Integer call() throws Exception {
        visitDao.insertVisits(visitList);
        return 0;
    }
}
