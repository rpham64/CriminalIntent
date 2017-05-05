package rpham64.criminalintent.models.database;

import android.content.Context;
import android.database.Cursor;
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

    private static CrimeBaseHelper sInstance;

    private static final int VERSION = 5;
    private static final String DATABASE_NAME = "crimeBase.db";

    private static final String DATABASE_ON_CREATE =
            "create table " + CrimeDbSchema.CrimeTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            CrimeDbSchema.CrimeTable.Cols.UUID + ", " +
            CrimeDbSchema.CrimeTable.Cols.TITLE + ", " +
            CrimeDbSchema.CrimeTable.Cols.DATE + ", " +
            CrimeDbSchema.CrimeTable.Cols.SOLVED + ", " +
            CrimeDbSchema.CrimeTable.Cols.SUSPECT + ", " +
            CrimeDbSchema.CrimeTable.Cols.NUMBER +
            ")";

    private static final String DATABASE_ADD_COLUMN_NUMBER = "ALTER TABLE " + CrimeDbSchema.CrimeTable.NAME
            + " ADD COLUMN " + CrimeDbSchema.CrimeTable.Cols.NUMBER + " string;";

    private CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_ON_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Create cursor for all data
        Cursor cursor = db.rawQuery("SELECT * FROM " + CrimeDbSchema.CrimeTable.NAME, null);
        int numberColumnIndex = cursor.getColumnIndex(CrimeDbSchema.CrimeTable.Cols.NUMBER);

        switch (oldVersion) {

            case 1:
                if (numberColumnIndex < 0) {   // Missing column "number", so add to table
                    db.execSQL(DATABASE_ADD_COLUMN_NUMBER);
                }
            case 2:
            case 3:
            case 4:

        }
    }

    public static synchronized CrimeBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CrimeBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }
}
