package com.tupelo.wellness.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Abhishek Singh Arya on 19-09-2015.
 */
public class AssetsDbAdapter extends SQLiteOpenHelper {
    private Context context;
    private static String DB_NAME = "city_country_db.sqlite";
    private static String DB_PATH = "/data/data/com.tupelo.wellness/databases/";
    private static String PATH = DB_PATH + DB_NAME;
    public SQLiteDatabase sqLiteDatabase;


    public AssetsDbAdapter(Context context) throws IOException {
        super(context, DB_NAME, null, 1);
        this.context = context;
        if (checkDatabase()) {
            openDatabase();
        } else {
            createDatabase();
        }
    }

    public void createDatabase() throws IOException {
        if (!checkDatabase()) {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase() {

        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);

            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copyDatabase() throws IOException {
        //Open your local db as the input stream
        InputStream inputStream = context.getAssets().open("database/" + DB_NAME);

        // Path to the just created empty db

        //Open the empty db as the output stream
        OutputStream outputStream = new FileOutputStream(PATH);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = inputStream.read(buffer)) > 0) {
//            outputStream.write(buffer, 0, length);
//        }

        while (inputStream.read(buffer)>0) {
            outputStream.write(buffer, 0, buffer.length);
        }


        //Close the streams
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void openDatabase() throws SQLException {
        //Open the database
        sqLiteDatabase = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public Cursor fetchQuery(String TABLE_NAME) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        final SQLiteDatabase readableDatabase = getReadableDatabase();
        final Cursor cursor = readableDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchQuery(String TABLE_NAME, String[] selectionArgs) {
        String COLUMN_NAME = "country";
        Log.e("in fetch query", selectionArgs[0]);
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?";
        final SQLiteDatabase readableDatabase = getReadableDatabase();
        final Cursor cursor = readableDatabase.rawQuery(selectQuery, selectionArgs);
        Log.e("after fetch query", " ");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}