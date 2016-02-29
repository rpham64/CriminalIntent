package bignerdranch.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by Rudolf on 2/25/2016.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    // Version
    public static final int VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "crimeBase.db";

    // Constructor
    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * For creating the initial database
     *
     * Uses the CrimeTable inner class from CrimeDbSchema.java
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create CrimeTable in SQLite
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SUSPECT +
                ")"
        );

    }

    /**
     * For upgrading the database (not needed for this app)
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
