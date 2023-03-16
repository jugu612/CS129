package com.amadeus.ting

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(context: Context, DATABASE_NAME: String?, DATABASE_VERSION: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val  DATABASE_VERSION = 1
            private const val DATABASE_NAME = "task_list.db"
        }

    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("NOT YET IMPLEMENTED")
    }
}


T