package rpham64.criminalintent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rudolf on 10/9/2016.
 */

public final class TimeUtils {

    private TimeUtils() {
        // This utility class is not publicly instantiable
    }

    /**
     * Convert Date to String using specified format
     *
     * Ex: "Wednesday February 13, 2016"
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        String format = "EEEE MMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * Convert time to String using specified format
     *
     * Ex: "04:01 PM"
     *
     * @param time
     * @return
     */
    public static String formatTime(Date time) {
        String format = "hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(time);
    }
}
