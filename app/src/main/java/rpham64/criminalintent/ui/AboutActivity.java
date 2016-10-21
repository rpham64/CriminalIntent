package rpham64.criminalintent.ui;

import android.os.Bundle;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

import rpham64.criminalintent.R;

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
                .withAboutDescription(getResources().getString(R.string.about_description));

        setIntent(builder.intent(this));
        super.onCreate(savedInstanceState);
    }

}
