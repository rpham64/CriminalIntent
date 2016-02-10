package bignerdranch.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Activity that hosts a list of Crime objects
 *
 * Created by Rudolf on 2/10/2016.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
