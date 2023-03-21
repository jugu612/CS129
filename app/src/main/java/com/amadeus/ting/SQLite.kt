package com.amadeus.ting

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(context: Context): SQLiteOpenHelper(context,"MA", null, 1) {

    companion object {
        private const val DATABASE_NAME = "TASKS"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE TASKS(TASKID INTEGER PRIMARY KEY AUTOINCREMENT, TASKNAME TEXT, TASKDETAILS TEXT, DEADLINE  DATE, LABEL TEXT)")
        db?.execSQL("INSERT INTO TASKS(TASKID, TASKNAME, TASKDETAILS, DEADLINE, LABEL) VALUES(0, 'Task #1','Kalamazoo', 2023/03/31, 'Label #1')")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


}