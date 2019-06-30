package com.example.xiaomi.journal;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.xiaomi.journal.Database.AppDatabase;
import com.example.xiaomi.journal.Database.PareDao;
import com.example.xiaomi.journal.Database.StudentsDao;
import com.example.xiaomi.journal.Database.VisitDao;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class InfoActivity extends AppCompatActivity {

    AppDatabase db;
    StudentsDao studentsDao;
    PareDao pareDao;
    VisitDao visitDao;
    TextView titleTv;
    TableLayout table;
    int labId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .build();

        studentsDao = db.studentsDao();
        pareDao = db.pareDao();
        visitDao = db.visitDao();

        table = findViewById(R.id.info_table);
        titleTv = findViewById(R.id.title_tv);

        Intent intent = getIntent();
        String pareName = intent.getStringExtra("pareName");

        titleTv.setText(pareName);

        Disposable fillTable = pareDao.getLabIds()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Long>>() {
                    @Override
                    public void accept(final List<Long> labIdes) {
                        Disposable getVisits = visitDao.getAllVisits()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<Visit>>() {
                                    @Override
                                    public void accept(final List<Visit> visitList) {
                                        Disposable getStudList = studentsDao.getAll()
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<List<Student>>() {
                                                    @Override
                                                    public void accept(List<Student> studentList) {
                                                        Collections.sort(studentList, new Comparator<Student>() {
                                                            @Override
                                                            public int compare(Student o1, Student o2) {
                                                                return o1.getName().compareTo(o2.getName());
                                                            }
                                                        });
                                                        int lecSemTotal = 0, labTotal = 0, lecSemMissedTotal = 0, labMissedTotal = 0;
                                                        for (int i = 0; i < studentList.size(); i++) {
                                                            int lecSemStudMissed = 0,
                                                                    labStudMissed = 0,
                                                                    lecSemStudTotal = 0,
                                                                    labStudTotal = 0;
                                                            for (Visit visit : visitList) {
                                                                if (visit.getStudId() == studentList.get(i).getId()) {
                                                                    if (labIdes.contains((long)visit.getPareId())) {
                                                                        labStudTotal++;
                                                                        if (!visit.getPresence())
                                                                            labStudMissed++;
                                                                    } else {
                                                                        lecSemStudTotal++;
                                                                        if (!visit.getPresence())
                                                                            lecSemStudMissed++;
                                                                    }
                                                                }
                                                            }
                                                            lecSemTotal += lecSemStudTotal;
                                                            labTotal += labStudTotal;
                                                            lecSemMissedTotal += lecSemStudMissed;
                                                            labMissedTotal += labStudMissed;
                                                            TableRow tableRow = getRowBy((i + 1) + ". " + studentList.get(i).getName(),
                                                                    lecSemStudTotal,
                                                                    lecSemStudMissed,
                                                                    labStudTotal,
                                                                    labStudMissed);
                                                            table.addView(tableRow);
                                                        }
                                                        TableRow tableRow = getFinalRowBy(getResources().getString(R.string.averageByGroup),
                                                                lecSemTotal,
                                                                lecSemMissedTotal,
                                                                labTotal,
                                                                labMissedTotal,
                                                                studentList.size());
                                                        table.addView(tableRow);
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    void findLabId(List<Long> pareIdes) {
        for (long id : pareIdes) {
            Disposable disposable = pareDao.getPareById(id)
                    .subscribe(new Consumer<Pare>() {
                        @Override
                        public void accept(Pare pare) throws Exception {
                            if (pare.getType() == AppConstants.LAB)
                                labId = pare.getId();
                        }
                    });
        }
    }

    TableRow getRowBy(String rowTitle, int lecSemTotal, int lecSemMissed, int labTotal, int labMissed) {
        TableRow tableRow = new TableRow(InfoActivity.this);


        TextView titleTv = new TextView(InfoActivity.this);
        titleTv.setPadding(2, 2, 2, 2);
        titleTv.setGravity(Gravity.START);
        titleTv.setBackground(getDrawable(R.drawable.cell_shape));
        titleTv.setText(String.valueOf(rowTitle));

        tableRow.addView(titleTv);

        if (lecSemTotal == 0)
            return tableRow;

        TextView lecSemMissTv = new TextView(InfoActivity.this);
        lecSemMissTv.setPadding(2, 2, 2, 2);
        lecSemMissTv.setGravity(Gravity.CENTER);
        lecSemMissTv.setBackground(getDrawable(R.drawable.cell_shape));
        lecSemMissTv.setText(String.valueOf(lecSemMissed));

        TextView lecSemPercentMissTv = new TextView(InfoActivity.this);
        lecSemPercentMissTv.setPadding(2, 2, 2, 2);
        lecSemPercentMissTv.setGravity(Gravity.CENTER);
        lecSemPercentMissTv.setBackground(getDrawable(R.drawable.cell_shape));
        String percentMissed = new DecimalFormat("#0.00").format((double) (lecSemMissed * 100 / lecSemTotal)) + "%";
        lecSemPercentMissTv.setText(percentMissed);

        TextView labMissTv = new TextView(InfoActivity.this);
        labMissTv.setPadding(2, 2, 2, 2);
        labMissTv.setGravity(Gravity.CENTER);
        labMissTv.setBackground(getDrawable(R.drawable.cell_shape));
        labMissTv.setText(String.valueOf(labMissed));

        TextView percentCompleteTv = new TextView(InfoActivity.this);
        percentCompleteTv.setPadding(2, 2, 2, 2);
        percentCompleteTv.setGravity(Gravity.CENTER);
        percentCompleteTv.setBackground(getDrawable(R.drawable.cell_shape));
        String percentComplete = new DecimalFormat("#0.00").format((100 - 100 * (double) (labMissed + lecSemMissed) / (double) (labTotal + lecSemTotal))) + "%";
        percentCompleteTv.setText(percentComplete);

        tableRow.addView(lecSemMissTv);
        tableRow.addView(lecSemPercentMissTv);
        tableRow.addView(labMissTv);
        tableRow.addView(percentCompleteTv);

        return tableRow;
    }

    TableRow getFinalRowBy(String rowTitle, int lecSemTotal, int lecSemMissed, int labTotal, int labMissed, int studCount) {
        TableRow tableRow = new TableRow(InfoActivity.this);


        TextView titleTv = new TextView(InfoActivity.this);
        titleTv.setPadding(2, 2, 2, 2);
        titleTv.setGravity(Gravity.START);
        titleTv.setBackground(getDrawable(R.drawable.cell_shape));
        titleTv.setText(String.valueOf(rowTitle));

        tableRow.addView(titleTv);

        if (lecSemTotal == 0)
            return tableRow;

        TextView lecSemMissTv = new TextView(InfoActivity.this);
        lecSemMissTv.setPadding(2, 2, 2, 2);
        lecSemMissTv.setGravity(Gravity.CENTER);
        lecSemMissTv.setBackground(getDrawable(R.drawable.cell_shape));
        String lecSemMissAverage = new DecimalFormat("#0.00").format((double)lecSemMissed/studCount);
        lecSemMissTv.setText(lecSemMissAverage);

        TextView lecSemPercentMissTv = new TextView(InfoActivity.this);
        lecSemPercentMissTv.setPadding(2, 2, 2, 2);
        lecSemPercentMissTv.setGravity(Gravity.CENTER);
        lecSemPercentMissTv.setBackground(getDrawable(R.drawable.cell_shape));
        String percentMissed = new DecimalFormat("#0.00").format((double) (lecSemMissed * 100 / lecSemTotal)) + "%";
        lecSemPercentMissTv.setText(percentMissed);

        TextView labMissTv = new TextView(InfoActivity.this);
        labMissTv.setPadding(2, 2, 2, 2);
        labMissTv.setGravity(Gravity.CENTER);
        labMissTv.setBackground(getDrawable(R.drawable.cell_shape));
        String labMissAverage = new DecimalFormat("#0.00").format((double)labMissed/studCount);
        labMissTv.setText(labMissAverage);

        TextView percentCompleteTv = new TextView(InfoActivity.this);
        percentCompleteTv.setPadding(2, 2, 2, 2);
        percentCompleteTv.setGravity(Gravity.CENTER);
        percentCompleteTv.setBackground(getDrawable(R.drawable.cell_shape));
        String percentComplete = new DecimalFormat("#0.00").format((100 - 100 * (double) (labMissed + lecSemMissed) / (double) (labTotal + lecSemTotal))) + "%";
        percentCompleteTv.setText(percentComplete);

        tableRow.addView(lecSemMissTv);
        tableRow.addView(lecSemPercentMissTv);
        tableRow.addView(labMissTv);
        tableRow.addView(percentCompleteTv);

        return tableRow;
    }
}
