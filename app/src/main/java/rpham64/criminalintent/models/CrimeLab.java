package bignerdranch.criminalintent.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bignerdranch.criminalintent.models.database.CrimeBaseHelper;
import bignerdranch.criminalintent.models.database.CrimeCursorWrapper;
import bignerdranch.criminalintent.models.database.CrimeDbSchema.CrimeTable;

/**
 * Singleton to hold Crime data
 *
 * To implement: Create private constructor and a get() method as below
 *
 * Created by Rudolf on 2/10/2016.
 */
public class CrimeLab {

    // Singleton to hold Crime data
    private static CrimeLab sCrimeLab;

    // Context
    private Context mContext;

    // SQLite Database
    private SQLiteDatabase mDatabase;

    // Private constructor given Context parameter
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    /**
     * Returns CrimeLab with the given context
     *
     * @param context
     * @return
     */
    public static CrimeLab get(Context context) {

        // If instance already exists, return the instance
        // Else, call the constructor to create it
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    /**
     * Add Crime to database
     *
     * @param crime
     */
    public void addCrime(Crime crime){

        // Get ContentValues
        ContentValues contentValues = getContentValues(crime);

        // Insert crimes's contentValues into database
        mDatabase.insert(CrimeTable.NAME, null, contentValues);

    }

    /**
     * Delete crime from list
     *
     * @param crime
     */
    public void deleteCrime(Crime crime) {

        // Get Crime UUID
        String uuidString = crime.getId().toString();

        // Delete crime's contentValues from database
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }


    /**
     * Returns list of Crimes using database
     *
     * @return
     */
    public List<Crime> getCrimes(){

        // List of crimes (to return)
        List<Crime> crimes = new ArrayList<>();

        // Cursor
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(null, null);

        // Walk the Cursor algorithm
        try {

            // Start at the first row data
            crimeCursorWrapper.moveToFirst();

            // Traverse the database, Extract Crime from CrimeCursorWrapper, and
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

    /**
     * Returns first row Crime data from the database, if it exists
     *
     * @return
     */
    public Crime getCrime(UUID id) {

        // Cursor for UUID row data
        CrimeCursorWrapper crimeCursorWrapper = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        // Return first row Crime data, if it exists
        try {
            if (crimeCursorWrapper.getCount() == 0) return null;

            crimeCursorWrapper.moveToFirst();

            return crimeCursorWrapper.getCrime();

        } finally {
            crimeCursorWrapper.close();             // To avoid cursor exceptions
        }

    }

    /**
     * Finds the directory for storing pictures
     *
     * @param crime
     * @return File object that points to the right location
     */
    public File getPhotoFile(Crime crime) {

        // Get reference to external files directory for pictures
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Check: If there exists an external storage to save the pictures
        if (externalFilesDir == null) return null;

        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    /**
     * Updates Crime in database
     *
     * @param crime
     */
    public void updateCrime(Crime crime) {

        // Crime UUID
        String uuidString = crime.getId().toString();

        // ContentValues of crime
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

        return contentValues;
    }

    /**
     * Database Query for Crimes - Reading in data from SQLite
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
