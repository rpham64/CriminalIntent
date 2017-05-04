package rpham64.criminalintent.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.orhanobut.logger.Logger;

/**
 * Created by Rudolf on 5/2/2017.
 */

public final class ContactUtils {

    private ContactUtils() {
        // This utility class is not publicly instantiable
    }

    public static String getContactName(Context context, Uri contactUri) {

        String name = null;

        // Store field for Contact name
        String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

        // Perform query using mContactUri as "whereClause"
        Cursor cursor = context.getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {

            if (cursor.getCount() == 0) return null;

            // Pull out suspect name data from first row, first column
            cursor.moveToFirst();
            name = cursor.getString(0);

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Logger.e("Cursor in CrimeFragment is null.");

        } finally {
            cursor.close();                 // To avoid Cursor exceptions
        }

        return name;
    }

    public static String getContactNumber(Context context, Uri contactUri) {

        String contactNumber = null;
        String contactId = getContactId(context, contactUri);

        // Use mContactId to retrieve contact phone number
        contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        String[] selectionArgs = new String[] {contactId};

        Cursor cursorPhone = context.getContentResolver().query(
                contactUri, queryFields, selection, selectionArgs, null);

        try {

            if (cursorPhone.moveToFirst()) {

                int columnIndex = cursorPhone.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);
                contactNumber = cursorPhone.getString(columnIndex);

            }

        } finally {
            cursorPhone.close();
        }

        return contactNumber;
    }

    private static String getContactId(Context context, Uri contactUri) {

        String contactId = null;

        // Store field for Contact number
        String[] queryFields = new String[] {ContactsContract.Contacts._ID};

        // Perform query using mContactUri as "whereClause"
        Cursor cursorID = context.getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {

            if (cursorID.moveToFirst()) {

                int columnIndex = cursorID.getColumnIndex(ContactsContract.Contacts._ID);
                contactId = cursorID.getString(columnIndex);

            }

        } finally {
            cursorID.close();                 // To avoid Cursor exceptions
        }

        return contactId;
    }
}
