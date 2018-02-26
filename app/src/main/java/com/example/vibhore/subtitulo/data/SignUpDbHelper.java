package com.example.vibhore.subtitulo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vibhore on 23/2/18.
 */
public class SignUpDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "details.db";
    private static final int DATABASE_VERSION = 2;

    public SignUpDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the table
        String SQL_CREATE_TABLE =  "CREATE TABLE " + ProductContract.SignUpEntry.TABLE_NAME + " ("
                + ProductContract.SignUpEntry.COLUMN_SIGNUP_PASSWORD + " INTEGER, "
                + ProductContract.SignUpEntry.COLUMN_SIGNUP_NAME + " TEXT NOT NULL, "
                + ProductContract.SignUpEntry.COLUMN_SIGNUP_MAIL + " TEXT NOT NULL, "
                + ProductContract.SignUpEntry.COLUMN_SIGNUP_ADDRESS + " TEXT NOT NULL, "
                + ProductContract.SignUpEntry.COLUMN_SIGNUP_PHONE + " numeric(10) NOT NULL, "
                + ProductContract.SignUpEntry.COLUMN_SIGNUP_USERNAME + " TEXT NOT NULL PRIMARY KEY );";

        //execute the sql query
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

