package com.example.xiaomi.journal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Student {
    @Ignore
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    public Student(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
