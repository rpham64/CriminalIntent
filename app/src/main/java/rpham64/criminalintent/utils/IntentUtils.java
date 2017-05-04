package rpham64.criminalintent.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;

import rpham64.criminalintent.R;

import static rpham64.criminalintent.utils.Permissions.REQUEST_CONTACT;

/**
 * Created by Rudolf on 5/3/2017.
 */

public final class IntentUtils {

    private IntentUtils() {
        // This utility class is not publicly instantiable
    }

    public static void pickContact(Fragment fragment) {
        final Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        fragment.startActivityForResult(pickContactIntent, REQUEST_CONTACT);
    }

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
