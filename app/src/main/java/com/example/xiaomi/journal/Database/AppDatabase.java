package com.example.xiaomi.journal.Database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.example.xiaomi.journal.Pare;
import com.example.xiaomi.journal.Student;
import com.example.xiaomi.journal.Visit;

@Database(entities = {Student.class, Pare.class, Visit.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentsDao studentsDao();

    public abstract PareDao pareDao();

    public abstract VisitDao visitDao();

    public static Migration getMigration() {
        return new Migration(3, 4) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
            }
        };
    }
}