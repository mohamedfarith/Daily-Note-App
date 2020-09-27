package com.example.farith.dailynotes.ModelClass;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "layoutStatus")
public class StatusEntityClass {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "statusId")
    int id;

    @ColumnInfo(name = "status")
    public String status;


    public String getStatus() {
        return status;
    }
}
