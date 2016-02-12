package bignerdranch.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * CrimeActivity class for hosting CrimeFragment
 *
 */
public class CrimeActivity extends SingleFragmentActivity {

    // Key for Crime ID (Extra)
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    /**
     * Create new Intent with Extra
     *
     * @param packageContext
     * @param crimeID
     * @return
     */
    public static Intent newIntent(Context packageContext, UUID crimeID) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        // Retrieve UUID crimeID from Intent's Extra
        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        // Return new fragment instance using CrimeFragment.newInstance(UUID)
        return CrimeFragment.newInstance(crimeID);
    }

}
