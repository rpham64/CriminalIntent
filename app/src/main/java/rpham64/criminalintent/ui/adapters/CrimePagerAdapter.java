package rpham64.criminalintent.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.ui.CrimeFragment;

/**
 * Created by Rudolf on 10/9/2016.
 */

public class CrimePagerAdapter extends FragmentStatePagerAdapter {

    private List<Crime> mCrimes;

    public CrimePagerAdapter(FragmentManager fm, List<Crime> crimes) {
        super(fm);
        this.mCrimes = crimes;
    }

    @Override
    public Fragment getItem(int position) {
        Crime crime = mCrimes.get(position);
        return CrimeFragment.newInstance(crime.getId());
    }

    @Override
    public int getCount() {
        return mCrimes.size();
    }
}
