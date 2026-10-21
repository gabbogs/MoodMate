package com.example.moodmate

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_USERS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_FULL_NAME TEXT, $COLUMN_USERNAME TEXT UNIQUE, $COLUMN_PASSWORD TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(fullName: String, username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, PasswordHasher.hash(password))

        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun authenticateUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ID, $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?",
            arrayOf(username)
        )

        var valid = false
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val stored = cursor.getString(1) ?: ""

            if (PasswordHasher.isLegacy(stored)) {
                // Account from before hashing: compare once, then upgrade it to a hash
                valid = stored == password
                if (valid) {
                    val values = ContentValues().apply { put(COLUMN_PASSWORD, PasswordHasher.hash(password)) }
                    db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
                }
            } else {
                valid = PasswordHasher.verify(password, stored)
            }
        }
        cursor.close()
        db.close()
        return valid
    }

    fun getUserId(username: String): Int {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        var userId = -1

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return userId
    }

    companion object {
        private const val DATABASE_NAME = "MoodMateDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FULL_NAME = "full_name"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }
}
