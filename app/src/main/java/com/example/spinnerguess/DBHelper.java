package com.example.spinnerguess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "result.db";
    private static final String TABLE_RECORD = "tblresult";
    private static final int DATABASEVERSION = 2;

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_UNAME = "u_name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SCORE = "score";

    private static final String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_UNAME, COLUMN_PASSWORD, COLUMN_SCORE};

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " +
            TABLE_RECORD + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_UNAME + " TEXT," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_SCORE + " INTEGER );";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        onCreate(sqLiteDatabase);
    }

    public ModelUser insert(ModelUser user) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getUserName());
        values.put(COLUMN_UNAME, user.getuName());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_SCORE, user.getScore());

        long id = database.insert(TABLE_RECORD, null, values);
        user.setId(id);
        database.close();
        return user;
    }

    public void update(ModelUser user) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getUserName());
        values.put(COLUMN_UNAME, user.getuName());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_SCORE, user.getScore());

        database.update(TABLE_RECORD, values, COLUMN_ID + " = ?", new String[]{String.valueOf(user.getId())});
        database.close();
    }

    public ArrayList<ModelUser> selectAll() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<ModelUser> users = new ArrayList<>();
        String sortOrder = COLUMN_SCORE + " DESC";

        Cursor cursor = database.query(TABLE_RECORD, allColumns, null, null, null, null, sortOrder);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String uName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNAME));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                    int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));

                    ModelUser user = new ModelUser(name, uName, password, score, id);
                    users.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return users;
    }

    public ArrayList<ModelUser> selectByUName(String uName) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<ModelUser> users = new ArrayList<>();
        String selection = COLUMN_UNAME + " = ?";
        String[] values = {uName};

        Cursor cursor = database.query(TABLE_RECORD, allColumns, selection, values, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String dbUName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNAME));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                    int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));

                    ModelUser user = new ModelUser(name, dbUName, password, score, id);
                    users.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return users;
    }
}