package rpham64.criminalintent.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ShareCompat;

import rpham64.criminalintent.R;

/**
 * Created by Rudolf on 5/3/2017.
 */

public class IntentUtils {

    public static Intent buildCrimeReportIntent(Activity activity, String crimeReport) {
        // Build Intent using ShareCompat.IntentBuilder
        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(activity);

        intentBuilder.setChooserTitle(R.string.send_report)
                .setType("text/plain")
                .setSubject(activity.getString(R.string.crime_report_subject))
                .setText(crimeReport);

        // Create chooser intent and send to OS
        Intent intent = Intent.createChooser(
                intentBuilder.getIntent(),
                activity.getString(R.string.send_report));

        return intent;
    }
}
