package bignerdranch.criminalintent;

import android.support.v4.app.Fragment;

/**
 * CrimeActivity class for hosting CrimeFragment
 *
 */
public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }

}
