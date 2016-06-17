package bignerdranch.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import bignerdranch.criminalintent.Model.Crime;
import bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Implementation for wrapping a Cursor and adding new methods
 *
 * Created by Rudolf on 2/25/2016.
 */
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Extract relevant Crime column data from SQLite Database
     *
     * @return
     */
    public Crime getCrime() {

        // Get reference to relevant Crime column data
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        // Create new Crime with above data
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }

}
