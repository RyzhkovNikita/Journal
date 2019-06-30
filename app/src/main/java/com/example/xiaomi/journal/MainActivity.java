package com.example.xiaomi.journal;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.xiaomi.journal.Database.AppDatabase;
import com.example.xiaomi.journal.Database.CallableRequests.DeletePareCallable;
import com.example.xiaomi.journal.Database.CallableRequests.DeleteStudentCallable;
import com.example.xiaomi.journal.Database.CallableRequests.InsertPareCallable;
import com.example.xiaomi.journal.Database.CallableRequests.InsertStudCallable;
import com.example.xiaomi.journal.Database.CallableRequests.InsertVisitsCallable;
import com.example.xiaomi.journal.Database.PareDao;
import com.example.xiaomi.journal.Database.StudentsDao;
import com.example.xiaomi.journal.Database.VisitDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView studListView, pareListView;
    Button addStudentBtn, addPareBtn;
    List<Student> studentList;
    List<Pare> pareList;
    AppDatabase db;
    StudentsDao studentsDao;
    PareDao pareDao;
    VisitDao visitDao;
    Toolbar mActionBarToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBarToolbar = findViewById(R.id.toolbar);

        studListView = findViewById(R.id.stud_list);
        pareListView = findViewById(R.id.pare_list);

        addStudentBtn = findViewById(R.id.addStudBtn);
        addPareBtn = findViewById(R.id.addPareBtn);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .build();

        studentsDao = db.studentsDao();
        pareDao = db.pareDao();
        visitDao = db.visitDao();

        addStudentBtn.setOnClickListener(this);
        addPareBtn.setOnClickListener(this);

        Disposable setStudentAdapter = studentsDao.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Student>>() {
                    @Override
                    public void accept(List<Student> students) {
                        studentList = students;
                        Collections.sort(students, new Comparator<Student>() {
                            @Override
                            public int compare(Student o1, Student o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        Map<String, Object> m;
                        ArrayList<Map<String, Object>> studData = new ArrayList<>();
                        for (int studPos = 0; studPos < students.size(); studPos++) {
                            m = new HashMap<>();
                            m.put("name", (studPos + 1) + ". " + students.get(studPos).getName());
                            studData.add(m);
                        }
                        String[] from = {"name"};
                        int[] to = {R.id.tv_stud};
                        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), studData, R.layout.stud_item, from, to);
                        studListView.setAdapter(adapter);
                        studListView.setOnItemLongClickListener(onStudentLongClickListener);
                    }
                });
        Disposable setPareAdapter = pareDao
                .getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Pare>>() {
                    @Override
                    public void accept(List<Pare> pares) {
                        pareList = pares;
                        Collections.sort(pares, new Comparator<Pare>() {
                            @Override
                            public int compare(Pare o1, Pare o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                        Map<String, Object> m;
                        final ArrayList<Map<String, Object>> pareData = new ArrayList<>();
                        for (Pare pare : pares) {
                            m = new HashMap<>();
                            m.put("name", pare.getName());
                            m.put("type", pare.getTypeStr());
                            m.put("id", pare.getId());
                            m.put("teacherName", pare.getTeacher());
                            pareData.add(m);
                        }
                        String[] from = {"name", "type", "teacherName"};
                        int[] to = {R.id.tv_pareName, R.id.tv_pareType, R.id.tv_teacherName};
                        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), pareData, R.layout.pare_item, from, to);
                        pareListView.setAdapter(adapter);
                        pareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                                intent.putExtra("id", (int) pareData.get(position).get("id"));
                                startActivity(intent);
                            }
                        });
                        pareListView.setOnItemLongClickListener(OnPareLongClickListener);
                    }
                });

    }

    AdapterView.OnItemLongClickListener onStudentLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    deleteStudent(studentList.get(position));
                    return false;
                }
            });
            popupMenu.show();
            return false;
        }
    };

    AdapterView.OnItemLongClickListener OnPareLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.inflate(R.menu.pare_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.pare_delete_btn:
                            deletePare(pareList.get(position));
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CODE_ADD_PARE:
                    Pare pare = new Pare(data.getStringExtra("pareName"),
                            data.getIntExtra("type", 0),
                            data.getStringExtra("teacherName"));
                    Observable.fromCallable(new InsertPareCallable(pareDao, pare))
                            .subscribeOn(Schedulers.io())
                            .subscribe();

                    break;
                case AppConstants.REQUEST_CODE_ADD_STUDENT:
                    String name = data.getStringExtra("name");
                    final Student newStudent = new Student(name);

                    if (!studentList.isEmpty()) {
                        Disposable insertVisitsForNewStudent = studentsDao.getFirstStudent()
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Student>() {
                                    @Override
                                    public void accept(final Student student) throws Exception {
                                        Disposable insert = Observable.fromCallable(new InsertStudCallable(studentsDao, newStudent))
                                                .subscribeOn(Schedulers.io())
                                                .subscribe(new Consumer<Long>() {
                                                    @Override
                                                    public void accept(Long l_newStudId) throws Exception {
                                                        final int newStudId = l_newStudId.intValue();
                                                        Disposable addVisitsForNewStud = visitDao.getStudentAllVisits(student.getId())
                                                                .subscribeOn(Schedulers.io())
                                                                .subscribe(new Consumer<List<Visit>>() {
                                                                    @Override
                                                                    public void accept(List<Visit> visits) throws Exception {
                                                                        ArrayList<Visit> newVisits = new ArrayList<>();
                                                                        Visit newVisit;
                                                                        for (Visit visit : visits) {
                                                                            newVisit = new Visit(visit.getDay(),
                                                                                    visit.getMonth(),
                                                                                    visit.getYear(),
                                                                                    newStudId,
                                                                                    visit.getPareId(),
                                                                                    true);
                                                                            newVisits.add(newVisit);
                                                                        }
                                                                        Observable.fromCallable(new InsertVisitsCallable(visitDao, newVisits))
                                                                                .subscribeOn(Schedulers.io())
                                                                                .subscribe();
                                                                    }
                                                                });
                                                    }
                                                });

                                    }
                                });
                    } else {
                        Disposable insert = Observable.fromCallable(new InsertStudCallable(studentsDao, newStudent))
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }
                    break;
            }
            Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_infoList:
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPareBtn:
                Intent intent = new Intent(MainActivity.this, AddPareActivity.class);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_ADD_PARE);
                break;
            case R.id.addStudBtn:
                Intent intent2 = new Intent(MainActivity.this, AddStudentActivity.class);
                startActivityForResult(intent2, AppConstants.REQUEST_CODE_ADD_STUDENT);
                break;
        }
    }

    void deleteStudent(Student student) {
        Observable.fromCallable(new DeleteStudentCallable(studentsDao, student))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    void deletePare(Pare pare) {
        Observable.fromCallable(new DeletePareCallable(pareDao, pare))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}

