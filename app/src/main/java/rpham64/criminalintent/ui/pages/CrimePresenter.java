package rpham64.criminalintent.ui.pages;

import java.util.Date;

import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.ui.BasePresenter;

/**
 * Created by Rudolf on 5/3/2017.
 */

public class CrimePresenter extends BasePresenter<CrimePresenter.View> {

    private Crime mCrime;

    public CrimePresenter() {

    }

    public void onDateButtonClicked(Date date) {
        getView().showDateDialog(date);
    }

    public void onTimeButtonClicked(Date time) {
        getView().showTimeDialog(time);
    }

    public void onSolvedButtonChecked(boolean isSolved) {
        getView().setSolved(isSolved);
    }

    public void onSuspectButtonClicked() {
        getView().findSuspect();
    }

    public void onCallButtonClicked() {
        getView().startCall();
    }

    public void onSendCrimeReportButtonClicked() {
        getView().sendCrimeReport();
    }

    public interface View {
        void showDateDialog(Date date);
        void showTimeDialog(Date time);
        void setSolved(boolean isSolved);
        void findSuspect();
        void startCall();
        void sendCrimeReport();
    }
}
