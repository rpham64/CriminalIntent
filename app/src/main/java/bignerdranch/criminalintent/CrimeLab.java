package bignerdranch.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    // List of Crimes
    private List<Crime> mCrimes;

    // Private constructor given Context parameter
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
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
     * Add crime to list
     *
     * @param crime
     */
    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    // Getter method for list mCrimes
    public List<Crime> getCrimes() {
        return mCrimes;
    }

    /**
     * Returns Crime with the given ID
     *
     * @return
     */
    public Crime getCrime(UUID id) {

        // For all Crimes in mCrimes
        // If current crime's id equals the given id, return the crime
        // Return null if not found
        for (Crime crime : mCrimes) {
            if (crime.getID().equals(id)) { return crime; }
        }

        return null;
    }
}
