package com.example.xiaomi.journal;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Pare {
    public Pare(String name, int type, String teacher) {
        this.name = name;
        this.type = type;
        this.teacher = teacher;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    String getTypeStr() {
        switch (type) {
            case AppConstants.LECTION:
                return AppConstants.STR_LECTION;
            case AppConstants.SEM:
                return AppConstants.STR_SEM;
            case AppConstants.LAB:
                return AppConstants.STR_LAB;
            default:
                return AppConstants.STR_LECTION;
        }
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int type;
    private String teacher;
}
