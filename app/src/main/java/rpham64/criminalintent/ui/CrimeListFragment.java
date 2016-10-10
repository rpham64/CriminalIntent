package bignerdranch.criminalintent.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bignerdranch.criminalintent.BaseFragment;
import bignerdranch.criminalintent.R;
import bignerdranch.criminalintent.models.Crime;
import bignerdranch.criminalintent.models.CrimeLab;
import bignerdranch.criminalintent.ui.adapters.CrimeListAdapter;
import bignerdranch.criminalintent.ui.views.EmptyRecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.State;

/**
 * Test123
 *
 * Created by Rudolf on 2/10/2016.
 */
public class CrimeListFragment extends BaseFragment {

    private static final String TAG = CrimeListFragment.class.getName();

    @BindView(R.id.crime_recycler_view)
    EmptyRecyclerView recyclerView;

    @State boolean isSubtitleVisible;

    private Unbinder mUnbinder;
    private CrimeListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setEmptyView(view.findViewById(android.R.id.empty));

        updateUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);

        if (isSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    /**
     * Given a MenuItem, adds a new Crime to CrimeLab and sends the Crime's ID
     * as an extra in an Intent to CrimePagerActivity.
     * If not the add crime button, updates the subtitle on the toolbar.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case (R.id.menu_item_add_crime):

                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);

                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);

                return true;

            case (R.id.menu_item_show_subtitle):

                isSubtitleVisible = !isSubtitleVisible;

                getActivity().invalidateOptionsMenu();
                updateSubtitle();

                return true;

            case (R.id.menu_item_about_app):
                Intent aboutActivity = new Intent(getActivity(), AboutActivity.class);
                startActivity(aboutActivity);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays the number of Crimes in CrimeListFragment
     */
    private void updateSubtitle() {

        int crimeCount = CrimeLab.get(getActivity()).getItemCount();
        String subtitle = isSubtitleVisible ?
                getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount) :
                null;

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeListAdapter(getContext(), crimes);
            recyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        // Update the Crime count
        updateSubtitle();
    }

}
