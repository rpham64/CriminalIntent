package rpham64.criminalintent.models.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Crime Database Helper Singleton
 *
 * Source: http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
 *
 * Created by Rudolf on 2/25/2016.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    private static CrimeBaseHelper sInstance;

    private CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

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

    public static synchronized CrimeBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CrimeBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }
}
