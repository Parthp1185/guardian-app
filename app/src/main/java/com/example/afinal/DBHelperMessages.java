package com.example.afinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelperMessages extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sos_messages.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "messages";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MESSAGE = "message";

    public DBHelperMessages(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE + " TEXT);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addMessage(String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Delete existing message to keep only one
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getAllMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void deleteMessage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
