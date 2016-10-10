package bignerdranch.criminalintent.ui;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

import bignerdranch.criminalintent.R;

/**
 * Created by Rudolf on 10/9/2016.
 */

public class AboutActivity extends LibsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        LibsBuilder builder = new LibsBuilder()
                .withActivityTheme(com.mikepenz.aboutlibraries.R.style.AboutLibrariesTheme_Light)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutAppName(getResources().getString(R.string.app_name))
                .withAboutDescription(
                        "A simple list for keeping track of \"office crimes\" in the workplace. " +
                                "Great for catching those troublemakers!");

        setIntent(builder.intent(this));
        super.onCreate(savedInstanceState);
    }

}
