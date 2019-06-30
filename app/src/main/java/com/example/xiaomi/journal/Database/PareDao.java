package com.example.xiaomi.journal.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.xiaomi.journal.Pare;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface PareDao {

    @Query("SELECT * FROM pare")
    Flowable<List<Pare>> getAll();

    @Query("select * from pare where id =:id")
    Flowable<Pare> getPareById(long id);

    @Query("select id from pare where type = 51")
    Single<List<Long>> getLabIds();

    @Insert
    void insert(Pare pare);

    @Update
    void update(Pare pare);

    @Delete
    void delete(Pare pare);
}
