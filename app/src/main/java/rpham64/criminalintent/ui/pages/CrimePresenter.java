package rpham64.criminalintent.ui.pages;

import java.io.File;
import java.util.Date;

import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.models.CrimeLab;
import rpham64.criminalintent.ui.BasePresenter;

import static rpham64.criminalintent.utils.TimeUtils.formatDate;
import static rpham64.criminalintent.utils.TimeUtils.formatTime;

/**
 * Created by Rudolf on 5/3/2017.
 */

public class CrimePresenter extends BasePresenter<CrimePresenter.View> {

    private Crime mCrime;
    private CrimeLab mCrimeLab;
    private File mPhotoFile;

    public CrimePresenter(Crime crime) {
        mCrime = crime;
    }

    public void deleteCrime() {

    }

    public void updateCrime() {

    }

    public void setTitle(String newTitle) {
        mCrime.setTitle(newTitle);
    }

    public void updateTitle() {
        getView().updateEditTextTitle(mCrime.getTitle());
    }

    public void setDate(Date date) {
        mCrime.setDate(date);
        updateDate();
    }

    public void setTime(Date time) {
        mCrime.setDate(time);
        updateTime();
    }

    public void updateDate() {
        String date = formatDate(mCrime.getDate());
        getView().updateButtonDate(date);
    }

    public void updateTime() {
        String time = formatTime(mCrime.getDate());
        getView().updateButtonTime(time);
    }

    public void setSolvedCheckBox() {
        getView().setSolvedCheckBox(mCrime.isSolved());
    }

    public void setCallButtonEnabled() {
        boolean isValidNumber = mCrime.getNumber() != null;
        getView().setCallButtonEnabled(isValidNumber);
    }

    /** UI Components */
    public void onCameraButtonClicked() {
        getView().startCamera();
    }

    public void onDateButtonClicked() {
        getView().showDateDialog(mCrime.getDate());
    }

    public void onTimeButtonClicked() {
        getView().showTimeDialog(mCrime.getDate());
    }

    public void onSolvedButtonChecked() {
        getView().setSolvedCheckBox(mCrime.isSolved());
    }

    public void onSuspectButtonClicked() {
        getView().findSuspect();
    }

    public void onCallButtonClicked() {
        getView().startCall(mCrime.getNumber());
    }

    public void onSendCrimeReportButtonClicked() {
        getView().sendCrimeReport();
    }

    public String getCrimeReport() {

        String title = mCrime.getTitle();
        String date = formatDate(mCrime.getDate());
        boolean isSolved = mCrime.isSolved();
        boolean hasSuspect = mCrime.getSuspect() == null;
        String suspect = mCrime.getSuspect();

        return getView().buildCrimeReport(title, date, isSolved, hasSuspect, suspect);
    }

    public interface View {
        void updateEditTextTitle(String title);
        void startCamera();
        void updateButtonDate(String date);
        void updateButtonTime(String time);
        void showDateDialog(Date date);
        void showTimeDialog(Date time);
        void setSolvedCheckBox(boolean isSolved);
        void findSuspect();
        void setCallButtonEnabled(boolean isEnabled);
        void startCall(String phoneNumber);
        String buildCrimeReport(String title, String date, boolean isSolved, boolean hasSuspect, String suspect);
        void sendCrimeReport();
    }
}
