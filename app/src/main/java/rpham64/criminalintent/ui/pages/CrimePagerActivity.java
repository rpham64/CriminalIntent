package rpham64.criminalintent.ui.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import rpham64.criminalintent.R;
import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.models.database.CrimeLab;
import rpham64.criminalintent.ui.adapters.CrimePagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Hosting Activity for ViewPager
 *
 * Created by Rudolf on 2/12/2016.
 */
public class CrimePagerActivity extends AppCompatActivity {

    interface Extras {
        String crimeId = "CrimePagerActivity.crimeId";
        String crimeFragment = "CrimeFragment";
    }

    @BindView(R.id.activity_crime_pager_view_pager) ViewPager mViewPager;

    private CrimePagerAdapter mPagerAdapter;

    private UUID mCrimeId;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID crimeID) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(Extras.crimeId, crimeID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            mCrimeId = (UUID) getIntent().getSerializableExtra(Extras.crimeId);
        }

        mCrimes = CrimeLab.get(this).getCrimes();
        mPagerAdapter = new CrimePagerAdapter(getSupportFragmentManager(), mCrimes);

        mViewPager.setAdapter(mPagerAdapter);

        // Set current index to clicked Crime's index
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(mCrimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
