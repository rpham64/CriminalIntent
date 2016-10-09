package bignerdranch.criminalintent.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import bignerdranch.criminalintent.models.Crime;
import bignerdranch.criminalintent.models.CrimeLab;
import bignerdranch.criminalintent.R;

/**
 * Hosting Activity for ViewPager
 *
 * Created by Rudolf on 2/12/2016.
 */
public class CrimePagerActivity extends AppCompatActivity {

    // Key for Crime ID (Extra)
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    // KEY for CrimeFragment
    private static final String KEY_CRIME_FRAGMENT = "crime fragment";

    // ViewPager
    private ViewPager mViewPager;

    // List of Crimes
    private List<Crime> mCrimes;

    Fragment crimeFragment;

    /**
     * Create new Intent with Extra
     *
     * @param packageContext
     * @param crimeID
     * @return
     */
    public static Intent newIntent(Context packageContext, UUID crimeID) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        // Retrieve Crime ID from Intent Extra
        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        // Inflate layout for ViewPager (get reference)
        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        // Get list of crimes from CrimeLab
        mCrimes = CrimeLab.get(this).getCrimes();

        // Get fragment manager and set this to the adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        // Set current index to clicked Crime's index
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeID)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

}
