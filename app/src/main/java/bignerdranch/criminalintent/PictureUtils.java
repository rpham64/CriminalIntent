package bignerdranch.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Rudolf on 3/4/2016.
 */
public class PictureUtils {

    /**
     * Scales bitmap to the given Activity's size
     *
     * @param path
     * @param activity
     * @return
     */
    public static Bitmap getScaledBitmap(String path, Activity activity) {

        // Create new Point
        Point size = new Point();

        // Get size of activity's display
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    /**
     * Scales bitmap to the given width and height
     *
     * @param path
     * @param destWidth
     * @param destHeight
     * @return
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        // Read in dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);    // Decodes file path into bitmap

        float srcWidth = options.outWidth;      // Width of Image
        float srcHeight = options.outHeight;    // Height of Image

        // Determine amount of scaling
        int inSampleSize = 1;

        if (srcHeight > destHeight || srcWidth > destWidth) {

            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }

        }

        // Create new options with inSampleSize
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);

    }

}
