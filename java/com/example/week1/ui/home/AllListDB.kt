package com.example.week1.ui.home

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray

class allListDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    //       "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "$NAME TEXT," +
                    "$STADIUM TEXT," +
                    "$TYPE TEXT," +
                    "$LOCATION TEXT," +
                    "$SEAT TEXT," +
                    "$TICKET TEXT)"
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun saveJsonToDatabase(jsonArray: JSONArray) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val contentValues = ContentValues().apply {
                    put("team", jsonObject.getString("Name"))
                    put("stadium", jsonObject.getString("Stadium"))
                    put("type", jsonObject.getString("Type"))
                    put("location", jsonObject.getString("Location"))
                    put("seat", jsonObject.getInt("Seat"))
                    put("ticket", jsonObject.getString("Ticket"))
                }
                db.insert(TABLE_NAME, null, contentValues)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllTeams(): List<Team> {
        val userList = mutableListOf<Team>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val user = Team(
                    name = cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    stadium = cursor.getString(cursor.getColumnIndexOrThrow(STADIUM)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(LOCATION)),
                    seat = cursor.getString(cursor.getColumnIndexOrThrow(SEAT)),
                    ticket = cursor.getString(cursor.getColumnIndexOrThrow(TICKET))
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "FeedReader.db"
        const val TABLE_NAME = "allTeam"
        const val NAME = "team"
        const val STADIUM = "stadium"
        const val TYPE = "type"
        const val LOCATION = "location"
        const val SEAT = "seat"
        const val TICKET = "ticket"
    }
}