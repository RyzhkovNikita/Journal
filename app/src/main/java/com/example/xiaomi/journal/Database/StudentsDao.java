package com.example.xiaomi.journal.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.xiaomi.journal.Student;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface StudentsDao {

    @Query("SELECT * FROM student")
    Flowable<List<Student>> getAll();

    @Query("SELECT * FROM student")
    Single<Student> getFirstStudent();

    @Query("SELECT id FROM student where name = :name")
    int getIdByName(String name);

    @Insert
    long insert(Student student);

    @Insert
    void insert(List<Student> students);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("DELETE FROM student")
    void clearTable();
}