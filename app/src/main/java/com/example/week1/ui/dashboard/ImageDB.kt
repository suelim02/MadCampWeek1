package com.example.week1.ui.dashboard

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log

class ImageDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    //       "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "$IMAGE TEXT," +
                    "$DESCRIPTION TEXT," +
                    "$DATE TEXT," +
                    "$TYPE TEXT," +
                    "$VICTORY TEXT)"
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // 주석 처리를 해야 db가 유지됨
        //db.execSQL(SQL_DELETE_ENTRIES)
        //onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertImage(image: Uri, description: String, date: String, type: String, victory: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(IMAGE, image.toString())
        values.put(DESCRIPTION, description)
        values.put(DATE, date)
        values.put(TYPE, type)
        values.put(VICTORY, if (victory) 1 else 0)
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllImages(): List<ImageDiary> {
        val userList = mutableListOf<ImageDiary>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val user = ImageDiary(
                    imageUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(IMAGE))),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE)),
                    victory = cursor.getInt(cursor.getColumnIndexOrThrow(VICTORY)) == 1
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList.reversed()
    }

    fun updateImage(imageDiary: ImageDiary, prevdescription: String, prevdate: String) {
        Log.d("ImageDB", "Updating: ${imageDiary.description}, ${imageDiary.date}, ${imageDiary.type}, ${imageDiary.victory}")
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DESCRIPTION, imageDiary.description)
            put(DATE, imageDiary.date)
            put(TYPE, imageDiary.type)
            put(VICTORY, if (imageDiary.victory) 1 else 0)
        }
        val abc = db.update(TABLE_NAME, values, "$IMAGE=? AND $DESCRIPTION = ? AND $DATE = ?",
            arrayOf(imageDiary.imageUri.toString(), prevdescription, prevdate))
        if(abc > 0)
            Log.d("ImageDB", "Successfully updated image: ${imageDiary.imageUri}")
    }

    fun getRowCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        var rowCount = 0
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return rowCount
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "Image.db"
        const val TABLE_NAME = "Image"
        const val IMAGE = "ImageURI"
        const val DESCRIPTION = "description"
        const val DATE = "date"
        const val TYPE = "type"
        const val VICTORY = "victory"
    }
}