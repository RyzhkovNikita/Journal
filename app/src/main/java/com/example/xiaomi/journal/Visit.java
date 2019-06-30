package com.example.xiaomi.journal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {@ForeignKey(entity = Student.class, parentColumns = "id", childColumns = "studId", onDelete = CASCADE),
        @ForeignKey(entity = Pare.class, parentColumns = "id", childColumns = "pareId", onDelete = CASCADE)})
public class Visit {
    public Visit(int day, int month, int year, int studId, int pareId, boolean presence) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.studId = studId;
        this.pareId = pareId;
        this.presence = presence;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int day;
    private int month;
    private int year;
    private int studId;
    private int pareId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public String getDateStr() {
        return month < 10 ?
                this.getDay() + ".0" + this.getMonth() : this.getDay() + "." + this.getMonth();
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getStudId() {
        return studId;
    }

    public void setStudId(int studId) {
        this.studId = studId;
    }

    public int getPareId() {
        return pareId;
    }

    public void setPareId(int pareId) {
        this.pareId = pareId;
    }

    public boolean getPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    private boolean presence;
}
