package rpham64.criminalintent.ui;

/**
 * Created by Rudolf on 5/3/2017.
 */

public interface IPresenter<T> {

    void attachView(T mvpView);

    void detachView();
    T getView();

    void onResume();
    void onPause();
    void onDestroy();
}
