package rpham64.criminalintent.ui.pages;

import java.io.File;
import java.util.Date;

/**
 * Created by Rudolf on 5/4/2017.
 */

public interface ICrimePresenter<T> {

    void onDeleteCrimeButtonClicked();

    void updateCrime();

    File getPhotoFile();

    void setPhotoFile(File photoFile);

    String getPhotoFileName();

    void setPhotoView();

    void setTitle(String newTitle);

    void updateTitle();

    void setDate(Date date);

    void setTime(Date time);

    void updateDate();

    void updateTime();

    void setSolvedCheckBox();

    void setSuspectInfo(String suspect, String number);

    void setButtonSuspect();

    void setCallButtonEnabled();

    String getCrimeReport();

    /** UI Components */
    void onCameraButtonClicked();

    void onDateButtonClicked();

    void onTimeButtonClicked();

    void onSolvedButtonChecked();

    void onSuspectButtonClicked();

    void onCallButtonClicked();

    void onSendCrimeReportButtonClicked();

}
