package bignerdranch.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by Rudolf on 2/10/2016.
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    // Abstract member variable (to be implemented in subclasses)
    protected abstract Fragment createFragment();

    /** Called when the activity is first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // Get FragmentManager
        // Check FragmentManager if CrimeFragment exists
        // If null, create new CrimeFragment and add to FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
