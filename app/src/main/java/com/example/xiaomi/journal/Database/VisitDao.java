package com.example.xiaomi.journal.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.xiaomi.journal.Visit;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface VisitDao {
    @Query("select * from visit")
    Flowable<List<Visit>> getAllVisits();

    @Query("Select * FROM visit WHERE pareId = :pareId")
    Flowable<List<Visit>> getVisitsForPare(int pareId);

    @Query("Select * from visit where studId = :studId")
    Single<List<Visit>> getStudentAllVisits(int studId);

    @Query("select * from visit where id = :id")
    Single<Visit> getById(int id);

    @Query("delete from visit where day = :day and month = :month and year = :year")
    int deleteVisitsByDate (int day, int month, int year);

    @Insert
    void insertVisits(List<Visit> visitList);

    @Update
    void updateVisit(Visit visit);

    @Delete
    void deleteVisits(List<Visit> visitList);
}
