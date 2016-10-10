package bignerdranch.criminalintent.models.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Crime Database
 *
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
        db.execSQL("create table " + CrimeDbSchema.CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeDbSchema.CrimeTable.Cols.UUID + ", " +
                CrimeDbSchema.CrimeTable.Cols.TITLE + ", " +
                CrimeDbSchema.CrimeTable.Cols.DATE + ", " +
                CrimeDbSchema.CrimeTable.Cols.SOLVED + ", " +
                CrimeDbSchema.CrimeTable.Cols.SUSPECT +
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
