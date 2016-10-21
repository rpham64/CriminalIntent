package rpham64.criminalintent.models;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class Crime {

    private UUID mId;                   // Unique crime ID
    private String mTitle;              // Crime Title
    private Date mDate;                 // Date of Crime
    private boolean mSolved;            // Crime solved or not
    private String mSuspect;            // Name of suspect

    /**
     * Construct Crime with random UUID
     */
    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    /**
     * Retrieves photo filename.
     *
     * Filename is unique due to Crime's ID.
     *
     * @return
     */
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

}
