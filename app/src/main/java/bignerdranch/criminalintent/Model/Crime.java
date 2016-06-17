package bignerdranch.criminalintent.Model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class Crime {

    private UUID mID;                   // Unique crime ID
    private String mTitle;              // Crime Title
    private Date mDate;                 // Date of Crime
    private boolean mSolved;            // Crime solved or not
    private String mSuspect;            // Name of suspect


    /**
     * Construct Crime with random UUID
     */
    public Crime() {
        this(UUID.randomUUID());    // Generate unique identifier via random UUId
    }

    /**
     * Construct Crime with specific UUID
     *
     * @param id
     */
    public Crime(UUID id) {
        mID = id;
        mDate = new Date();
    }

    /** Getter for mID */
    public UUID getID() {
        return mID;
    }

    /** Getters and Setters for mTitle */
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    /** Getters and Setters for mDate */
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    /** Getters and Setters for mSolved */
    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    /** Getters and Setters for mSuspect */
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
        return "IMG_" + getID().toString() + ".jpg";
    }

}
