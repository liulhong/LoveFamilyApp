package org.ar.call.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class PersonDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    private val createPerson = "create table Person (" +
            "id integer primary key," +
            "callId integer," +
            "name text," +
            "image Blob," +
            "callFree boolean," +
            "insertTime text)"
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createPerson)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists Person")
        onCreate(db)

    }
}