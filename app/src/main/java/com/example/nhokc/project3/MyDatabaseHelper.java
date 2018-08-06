package com.example.nhokc.project3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper{
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Prefix_Manager";
    private static final String TABLE_PREFIX = "Prefix_Table";

    private static final String COLUMN_PREFIX ="Prefix";
    public MyDatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        String script = "CREATE TABLE " + TABLE_PREFIX + "("
                + COLUMN_PREFIX + " TEXT" + ")";
        sqLiteDatabase.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFIX);

        onCreate(sqLiteDatabase);
    }

    public void addPrefix(String prefix) {
        Log.i(TAG, "MyDatabaseHelper.addNote ... " + prefix);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PREFIX, prefix);

        db.insert(TABLE_PREFIX, null, values);

        db.close();
    }

    public List<String> getAllPrefix() {
        Log.i(TAG, "MyDatabaseHelper.getAllPrefix ... " );

        List<String> noteList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PREFIX;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                noteList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return noteList;
    }

    public int getNotesCount() {
        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_PREFIX;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public int updatePrefix(String prefix) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... "  + prefix);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PREFIX, prefix);
        return db.update(TABLE_PREFIX, values, COLUMN_PREFIX + " = ?",
                new String[]{String.valueOf(prefix)});
    }

    public void deleteNote(String prefix) {
        Log.i(TAG, "MyDatabaseHelper.updateNote ... " + prefix );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PREFIX, COLUMN_PREFIX + " = ?",
                new String[] { String.valueOf(prefix) });
        db.close();
    }
}
