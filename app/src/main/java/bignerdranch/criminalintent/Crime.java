package bignerdranch.criminalintent;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class Crime {

    private UUID mID;           // Unique crime ID
    private String mTitle;      // Crime Title
    private Date mDate;         // Date of Crime
    private boolean mSolved;    // Crime solved or not

    public Crime() {
        mID = UUID.randomUUID();    // Generate unique identifier via random UUId
        mDate = new Date();         // Default date (current time)
    }

    // Getter for mID
    public UUID getID() {
        return mID;
    }

    // Getters and Setters for mTitle
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    // Getters and Setters for mDate
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    // Getters and Setters for mSolved
    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
