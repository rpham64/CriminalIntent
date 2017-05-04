package rpham64.criminalintent.models.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.models.database.CrimeDbSchema.CrimeTable;

/**
 * Crime Database Singleton
 *
 * Created by Rudolf on 2/10/2016.
 */
public class CrimeLab {

    // Singleton Instance - Only one exists forever
    private static CrimeLab sCrimeLab;

    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mDatabase = CrimeBaseHelper.getInstance(context).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime crime){
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, contentValues);
    }

    public void deleteCrime(Crime crime) {

        // Get Crime UUID
        String uuidString = crime.getId().toString();

        // Delete crime's contentValues from database
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public List<Crime> getCrimes(){

        List<Crime> crimes = new ArrayList<>();

        // Cursor
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(null, null);

        try {

            // Start at the first row data
            crimeCursorWrapper.moveToFirst();

            // Traverse the database
            // Extract Crime from CrimeCursorWrapper
            // Add to crimes list
            while (!crimeCursorWrapper.isAfterLast()) {
                crimes.add(crimeCursorWrapper.getCrime());
                crimeCursorWrapper.moveToNext();    // Next row data
            }

        } finally {
            crimeCursorWrapper.close();             // To avoid cursor exceptions
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {

        // Cursor for UUID row data
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        // Return first row Crime data, if it exists
        try {

            // No crime with id found
            if (crimeCursorWrapper.getCount() == 0) return null;

            crimeCursorWrapper.moveToFirst();

            return crimeCursorWrapper.getCrime();

        } finally {
            crimeCursorWrapper.close();             // To avoid cursor exceptions
        }

    }

    public void updateCrime(Crime crime) {

        String uuidString = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);

        // Update database entry for crime
        mDatabase.update(CrimeTable.NAME, contentValues,
                CrimeTable.Cols.UUID + " = ?", new String[]{ uuidString });

    }

    public int getItemCount() {
        return getCrimes().size();
    }

    /**
     * Returns ContentValues for given Crime
     *
     * @param crime
     * @return
     */
    private static ContentValues getContentValues(Crime crime) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        contentValues.put(CrimeTable.Cols.NUMBER, crime.getNumber());

        return contentValues;
    }

    /**
     * Database Query for Crimes - Reads in data from SQLite
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {

        // Query method
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,               // null selects all columns
                whereClause,
                whereArgs,
                null,               // groupBy
                null,               // having
                null                // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }
}
