package rpham64.criminalintent.ui.pages;

import java.io.File;
import java.util.Date;

import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.ui.base.BasePresenter;

import static rpham64.criminalintent.utils.TimeUtils.formatDate;
import static rpham64.criminalintent.utils.TimeUtils.formatTime;

/**
 * Created by Rudolf on 5/3/2017.
 */

public class CrimePresenter extends BasePresenter<CrimePresenter.View> implements ICrimePresenter {

    private Crime mCrime;

    public CrimePresenter(Crime crime) {
        mCrime = crime;
    }

    // TODO: Figure out how to move database access away from View

    @Override
    public void onDeleteCrimeButtonClicked() {
        getView().deleteCrime(mCrime);
    }

    @Override
    public void updateCrime() {
        getView().updateCrime(mCrime);
    }

    @Override
    public File getPhotoFile() {
        return mCrime.getPhotoFile();
    }

    @Override
    public void setPhotoFile(File photoFile) {
        mCrime.setPhotoFile(photoFile);
    }

    @Override
    public String getPhotoFileName() {
        return mCrime.getPhotoFilename();
    }

    @Override
    public void setPhotoView() {
        getView().setPhoto(mCrime.getPhotoFile());
    }

    @Override
    public void setTitle(String newTitle) {
        mCrime.setTitle(newTitle);
    }

    @Override
    public void updateTitle() {
        getView().updateEditTextTitle(mCrime.getTitle());
    }

    @Override
    public void setDate(Date date) {
        mCrime.setDate(date);
        updateDate();
    }

    @Override
    public void setTime(Date time) {
        mCrime.setDate(time);
        updateTime();
    }

    @Override
    public void updateDate() {
        String date = formatDate(mCrime.getDate());
        getView().updateButtonDate(date);
    }

    @Override
    public void updateTime() {
        String time = formatTime(mCrime.getDate());
        getView().updateButtonTime(time);
    }

    @Override
    public void setSolvedCheckBox() {
        getView().setSolvedCheckBox(mCrime.isSolved());
    }

    @Override
    public void setSuspectInfo(String suspect, String number) {
        mCrime.setSuspect(suspect);
        mCrime.setNumber(number);
    }

    @Override
    public void setButtonSuspect() {
        getView().updateButtonSuspect(mCrime.getSuspect());
    }

    @Override
    public void setCallButtonEnabled() {
        boolean isValidNumber = mCrime.getNumber() != null;
        getView().setCallButtonEnabled(isValidNumber);
    }

    @Override
    public String getCrimeReport() {

        String title = mCrime.getTitle();
        String date = formatDate(mCrime.getDate());
        boolean isSolved = mCrime.isSolved();
        boolean hasSuspect = mCrime.getSuspect() == null;
        String suspect = mCrime.getSuspect();

        return getView().buildCrimeReport(title, date, isSolved, hasSuspect, suspect);
    }

    /** UI Components */

    @Override
    public void onCameraButtonClicked() {
        getView().startCamera();
    }

    @Override
    public void onDateButtonClicked() {
        getView().showDateDialog(mCrime.getDate());
    }

    @Override
    public void onTimeButtonClicked() {
        getView().showTimeDialog(mCrime.getDate());
    }

    @Override
    public void onSolvedButtonChecked() {
        boolean isSolved = mCrime.isSolved();
        mCrime.setSolved(!isSolved);
        getView().setSolvedCheckBox(!isSolved);
    }

    @Override
    public void onSuspectButtonClicked() {
        getView().findSuspect();
    }

    @Override
    public void onCallButtonClicked() {
        getView().startCall(mCrime.getNumber());
    }

    @Override
    public void onSendCrimeReportButtonClicked() {
        getView().sendCrimeReport();
    }

    public interface View {
        void deleteCrime(Crime crime);
        void updateCrime(Crime crime);
        void updateEditTextTitle(String title);
        void startCamera();
        void setPhoto(File photo);
        void updateButtonDate(String date);
        void updateButtonTime(String time);
        void showDateDialog(Date date);
        void showTimeDialog(Date time);
        void setSolvedCheckBox(boolean isSolved);
        void findSuspect();
        void updateButtonSuspect(String suspect);
        void setCallButtonEnabled(boolean isEnabled);
        void startCall(String phoneNumber);
        String buildCrimeReport(String title, String date, boolean isSolved, boolean hasSuspect, String suspect);
        void sendCrimeReport();
    }
}
