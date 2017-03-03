package com.blackstone.goldenquran.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blackstone.goldenquran.models.AhadeethModel;
import com.blackstone.goldenquran.models.Ayah;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseManager {

    private static final String TAG = "DataAdapter";
    private Medina1OpenHelper mDbHelper;
    private AhadithHelper mAhadithHelper;
    private SQLiteDatabase mDb;
    private SQLiteDatabase mDbAhadith;


    public DataBaseManager(Context context) {
        mDbHelper = new Medina1OpenHelper(context);
        mAhadithHelper = new AhadithHelper(context);
    }

    public DataBaseManager createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
            mAhadithHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataBaseManager open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
            mAhadithHelper.openDataBase();
            mAhadithHelper.close();
            mDbAhadith = mAhadithHelper.getReadableDatabase();

        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public ArrayList<Ayah> getPagePoints(int pageNumber) {

        Cursor cursor = mDb.query("page", null, "page_number = ? and id > 10000", new String[]{String.valueOf(pageNumber)}, null, null, null);

        ArrayList<Ayah> ayah = new ArrayList<>();

        while (cursor.moveToNext()) {
            ayah.add(new Ayah(
                    cursor.getFloat(cursor.getColumnIndex("x")),
                    cursor.getFloat(cursor.getColumnIndex("y")),
                    cursor.getFloat(cursor.getColumnIndex("width")),
                    cursor.getFloat(cursor.getColumnIndex("height")),
                    cursor.getFloat(cursor.getColumnIndex("upper_left_x")),
                    cursor.getFloat(cursor.getColumnIndex("upper_left_y")),
                    cursor.getFloat(cursor.getColumnIndex("upper_right_x")),
                    cursor.getFloat(cursor.getColumnIndex("upper_right_y")),
                    cursor.getFloat(cursor.getColumnIndex("lower_right_x")),
                    cursor.getFloat(cursor.getColumnIndex("lower_right_y")),
                    cursor.getFloat(cursor.getColumnIndex("lower_left_x")),
                    cursor.getFloat(cursor.getColumnIndex("lower_left_y")),
                    cursor.getFloat(cursor.getColumnIndex("ayah")),
                    cursor.getFloat(cursor.getColumnIndex("line")),
                    cursor.getFloat(cursor.getColumnIndex("surah")),
                    cursor.getFloat(cursor.getColumnIndex("page_number")),
                    cursor.getFloat(cursor.getColumnIndex("id"))
            ));
        }
        return ayah;
    }

    public int getPageNumber(String surahNumber) {

        Cursor cursor = mDb.query("page", null, "surah = ? and id > 10000", new String[]{surahNumber}, null, null, "ayah ASC");
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("page_number"));
    }


    public ArrayList<AhadeethModel> getAhadith() {

        ArrayList<AhadeethModel> ahadith = new ArrayList<>();

        Cursor cursor = mDbAhadith.query("HadithTable", null, "HadithGroupID = 0", null, null, null, null);

        while (cursor.moveToNext()) {
            ahadith.add(new AhadeethModel(
                    cursor.getString(cursor.getColumnIndex("HadithSummary")),
                    cursor.getString(cursor.getColumnIndex("HadithFullText"))
            ));
        }
        return ahadith;
    }

    public String getOnFinishQuran() {


        Cursor cursor = mDbAhadith.query("HadithTable", null, "HadithGroupID = 1", null, null, null, null);

        String ahadith = "";

        while (cursor.moveToNext()) {
            ahadith = cursor.getString(cursor.getColumnIndex("HadithFullText"));
        }
        return ahadith;
    }


}