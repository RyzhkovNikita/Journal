package com.example.xiaomi.journal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiaomi.journal.Database.AppDatabase;
import com.example.xiaomi.journal.Database.CallableRequests.InsertVisitsCallable;
import com.example.xiaomi.journal.Database.PareDao;
import com.example.xiaomi.journal.Database.StudentsDao;
import com.example.xiaomi.journal.Database.VisitDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TableActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TableLayout visitTable, studTable;
    TextView pareTv, teacherTv;
    AppDatabase database;
    StudentsDao studentsDao;
    PareDao pareDao;
    VisitDao visitDao;
    Button addColumnBtn;
    int pareId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        visitTable = findViewById(R.id.visit_table);
        studTable = findViewById(R.id.stud_table);
        addColumnBtn = findViewById(R.id.addvisit_btn);
        pareTv = findViewById(R.id.pare_name_tv);
        teacherTv = findViewById(R.id.pare_teacher_tv);

        pareId = getIntent().getIntExtra("id", 1000);
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database")
                .build();
        studentsDao = database.studentsDao();
        pareDao = database.pareDao();
        visitDao = database.visitDao();

        Disposable setPareAttributes = pareDao.getPareById(pareId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pare>() {
                    @Override
                    public void accept(Pare pare) throws Exception {
                        String pareTitle = pare.getName() + " (" + pare.getTypeStr() + ")";
                        pareTv.setText(pareTitle);
                        teacherTv.setText(pare.getTeacher());
                    }
                });

        Disposable getStudentList = studentsDao.getAll()
                .observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<List<Student>>() {
                            @Override
                            public void accept(final List<Student> studentList) throws Exception {
                                Collections.sort(studentList, new Comparator<Student>() {
                                    @Override
                                    public int compare(Student o1, Student o2) {
                                        return o1.getName().compareTo(o2.getName());
                                    }
                                });
                                Disposable getVisitList = visitDao.getVisitsForPare(pareId)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<List<Visit>>() {
                                            @Override
                                            public void accept(List<Visit> visitList) throws Exception {
                                                visitTable.removeAllViews();
                                                if (visitList.isEmpty()) {
                                                    final TableRow titleRow = new TableRow(TableActivity.this);
                                                    TextView titleTv = new TextView(TableActivity.this);
                                                    titleTv.setTextSize(16);
                                                    titleTv.setGravity(Gravity.CENTER);
                                                    titleTv.setBackgroundColor(getResources().getColor(R.color.BackgroundColor));
                                                    titleTv.setText("Нет посещений");
                                                    titleRow.addView(titleTv);
                                                    visitTable.addView(titleRow);
                                                } else {
                                                    sortByDate(visitList);
                                                    List<Visit> datesVisits = getVisitsForStudent(visitList);
                                                    final TableRow titleRow = new TableRow(TableActivity.this);
                                                    TextView titleTv = new TextView(TableActivity.this);
                                                    titleTv.setText("Студенты");
                                                    titleTv.setTextSize(16);
                                                    titleTv.setGravity(Gravity.CENTER);
                                                    titleTv.setBackgroundColor(getResources().getColor(R.color.BackgroundColor));
                                                    titleRow.addView(titleTv);
                                                    visitTable.addView(titleRow);
                                                    int studId = studentList.get(0).getId();
                                                    for (Visit visit : visitList) {
                                                        if (visit.getStudId() == studId) {
                                                            TextView dateTv = new TextView(TableActivity.this);
                                                            dateTv.setBackgroundColor(getResources().getColor(R.color.BackgroundColor));
                                                            dateTv.setText(visit.getDateStr());
                                                            dateTv.setTextSize(16);
                                                            dateTv.setPadding(20, 20, 20, 20);

                                                            dateTv.setId(visit.getId());
                                                            dateTv.setOnLongClickListener(dateListener);
                                                            titleRow.addView(dateTv);
                                                        }
                                                    }
                                                    for (int studPos = 0; studPos < studentList.size(); studPos++) {

                                                        final TableRow tableRow = new TableRow(TableActivity.this);
                                                        TextView tvStudName = new TextView(TableActivity.this);
                                                        String studTitle = (studPos + 1) + ". " + studentList.get(studPos).getName();
                                                        tvStudName.setText(studTitle);
                                                        tvStudName.setTextSize(16);
                                                        tvStudName.setBackgroundColor(getResources().getColor(R.color.BackgroundColor));
                                                        tvStudName.setPadding(20, 20, 20, 20);
                                                        tableRow.addView(tvStudName);
                                                        for (Visit visit : visitList) {
                                                            if (studentList.get(studPos).getId() == visit.getStudId()) {
                                                                TextView visitTv = new TextView(TableActivity.this);
                                                                visitTv.setBackgroundColor(Color.WHITE);
                                                                visitTv.setPadding(20, 20, 20, 20);
                                                                visitTv.setGravity(Gravity.CENTER);
                                                                visitTv.setTextSize(20);
                                                                if (visit.getPresence())
                                                                    visitTv.setText("+");
                                                                else
                                                                    visitTv.setText("н");
                                                                visitTv.setId(visit.getId());
                                                                visitTv.setOnLongClickListener(visitListener);
                                                                tableRow.addView(visitTv);
                                                            }
                                                        }
                                                        visitTable.addView(tableRow);
                                                    }
                                                }
                                            }
                                        });
                            }
                        });


        addColumnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "Time Picker");
            }
        });
    }

    public void sortByDate(List<Visit> visitList) {
        Collections.sort(visitList, new Comparator<Visit>() {
            @Override
            public int compare(Visit o1, Visit o2) {
                if (o1.getYear() > o2.getYear())
                    return 1;
                else if (o1.getYear() < o1.getYear())
                    return -1;
                else if (o1.getMonth() > o2.getMonth())
                    return 1;
                else if (o1.getMonth() < o2.getMonth())
                    return -1;
                else
                    return Integer.compare(o1.getDay(), o2.getDay());
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
        Disposable addVisits = studentsDao.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Student>>() {
                    @Override
                    public void accept(List<Student> studentList) throws Exception {
                        Visit visit;
                        ArrayList<Visit> visitArrayList = new ArrayList<>();
                        for (int i = 0; i < studentList.size(); i++) {
                            int studId = studentList.get(i).getId();
                            visit = new Visit(dayOfMonth, month + 1, year, studId, pareId, true);
                            visitArrayList.add(visit);
                        }
                        Observable.fromCallable(new InsertVisitsCallable(visitDao, visitArrayList))
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }
                });
        Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show();
    }

    View.OnLongClickListener visitListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final int visitId = v.getId();
            Disposable updateVisit = visitDao.getById(visitId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Visit>() {
                        @Override
                        public void accept(Visit visit) throws Exception {
                            visit.setPresence(!visit.getPresence());
                            visitDao.updateVisit(visit);
                            vibrate(70);
                        }
                    });
            return false;
        }
    };

    View.OnLongClickListener dateListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View v) {
            PopupMenu popupMenu = new PopupMenu(TableActivity.this, v);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Disposable getNeedVisit = visitDao.getById(v.getId())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Consumer<Visit>() {
                                @Override
                                public void accept(Visit visit) throws Exception {
                                    visitDao.deleteVisitsByDate(visit.getDay(), visit.getMonth(), visit.getYear());
                                }
                            });
                    return false;
                }
            });
            popupMenu.show();
            return false;
        }
    };

    public static class DatePickerFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    (DatePickerDialog.OnDateSetListener) getActivity(),
                    year, month, day);
        }

    }

    List<Visit> getVisitsForStudent(List<Visit> visitList) {
        Visit visit = visitList.get(0);
        int studId = visit.getStudId();
        List<Visit> visits = new ArrayList<>();
        for (Visit studVisit : visitList) {
            if (studVisit.getStudId() == studId)
                visits.add(studVisit);
        }
        return visits;
    }


    void vibrate(int milliSeconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(milliSeconds);
        }
    }
}
