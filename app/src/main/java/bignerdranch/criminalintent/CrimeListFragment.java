package bignerdranch.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Rudolf on 2/10/2016.
 */
public class CrimeListFragment extends Fragment {

    // Key for storing mSubtitleVisible in savedInstanceState
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    // RecyclerView
    private RecyclerView mCrimeRecyclerView;

    // Adapter
    private CrimeAdapter mAdapter;

    // Position of Crime in Adapter (for updating View objects)
    private int mCrimePosition;

    // Checks if the Subtitle is visible
    private boolean mSubtitleVisible;

    /**
     * On the creation on CrimeListFragment, implement an options menu
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Pseudocode:
     *
     * On create view, inflate the view and call reference to mCrimeRecyclerview.
     * Set up a layout manager for mCrimeRecyclerView using layout fragment_crime_list.xml
     * Return the inflated view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate Menu
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // Call reference to mCrimeRecyclerView
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        // Set layout manager for mCrimeRecyclerView
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Check: If mSubtitleVisible is saved in savedInstanceState
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        // Update the user interface
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    /**
     * Creates Toolbar Menu.
     *
     * Contains "Show Subtitle", "Hide Subtitle" action items.
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        // Retrieve reference to subtitle item
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);

        // Check: if mSubtitlesVisible is true, set title to "Hide Subtitles".
        // Else, set title to "Show Subtitles"
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    /**
     * Given a MenuItem, adds a new Crime to CrimeLab and sends the Crime's ID
     * as an extra in an Intent to CrimePagerActivity.
     * If not the add crime button, updates the subtitle on the toolbar.
     * Else, calls superclass' implementation.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Case: MenuItem is the "add crime" menu item
            case (R.id.menu_item_new_crime):

                // Create a new Crime object
                Crime crime = new Crime();

                // Add the Crime to CrimeLab
                CrimeLab.get(getActivity()).addCrime(crime);

                // Create a new intent and store the Crime's ID as an extra
                // Send the intent to CrimePagerActivity
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getID());
                startActivity(intent);

                // To indicate no further processing is necessary
                return true;

            // Case: MenuItem is the "show subtitle" menu item
            case (R.id.menu_item_show_subtitle):

                // Change status of mSubtitleVisible to alternate between shown and hidden
                mSubtitleVisible = !mSubtitleVisible;

                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays the number of Crimes in CrimeListFragment
     *
     */
    private void updateSubtitle() {

        // Retrieve CrimeLab
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        // Retrieve count of Crimes in CrimeLab
        int crimeCount = crimeLab.getCrimes().size();

        // Create subtitle with String subtitle_plural and the crimeCount
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        // Check: If subtitle is not visible, set subtitle to null
        if (!mSubtitleVisible) { subtitle = null; }

        // Convert hosting activity into AppCompatActivity instance (for access to toolbar)
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Set subtitle of hosting activity's support action bar
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    /**
     * Updates the User Interface
     *
     */
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        // If list is empty, create a message that says "No crimes here! Click the
        // "+" button above to add a new crime."
        if (crimes.size() == 0) {

            // Create toast
            Toast toast = Toast.makeText(getActivity(), R.string.crime_list_empty, Toast.LENGTH_LONG);

            // Align text in Toast to center
            TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
            if (textView != null) {
                textView.setGravity(Gravity.CENTER);
            }

            // Display toast message
            toast.show();
        }

        // If mAdapter is null, create a new CrimeAdapter and set to mCrimeRecyclerView's adapter
        // Else, send notification that an item has changed
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            // Notify adapter that item changed at mCrimePosition
//            mAdapter.notifyItemChanged(mCrimePosition);

            // Fixes ArrayOutOfIndexException for deleting crimes (Challenge: Deleting Crimes)
            mAdapter.notifyDataSetChanged();
        }

        // Update the Crime count in menu item "Subtitle"
        updateSubtitle();
    }

    // Define CrimeAdapter (Adapter) as an inner class
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;    // List of crimes

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        /**
         * Create a View and wrap it in a ViewHolder
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Call reference to LayoutInflater from activity
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            // Create View and inflate using list_item_crime layout
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);

            // Return CrimeHolder with View
            return new CrimeHolder(view);
        }

        /**
         * Binds a ViewHolder's View to model object
         *
         * @param crimeHolder
         * @param position
         */
        @Override
        public void onBindViewHolder(CrimeHolder crimeHolder, int position) {

            // Use position to find model data
            Crime crime = mCrimes.get(position);

            // Bind Crime to CrimeHolder
            crimeHolder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

    }

    /**
     * CrimeHolder class
     *
     * View components: Title, Date, Solved checkbox
     *
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;               // Crime Object

        private TextView mTitleTextView;    // Title
        private TextView mDateTextView;     // Date
        private CheckBox mSolvedCheckBox;   // Solved?

        public CrimeHolder(View itemView) {
            super(itemView);

            // Call reference to each member variable
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);

            // Set CrimeHolder listener to itemView
            itemView.setOnClickListener(this);
        }

        /**
         * Given a Crime, binds Crime View to CrimeHolder by updating title TextView, date TextView,
         * and solved CheckBox
         *
         * @param crime
         */
        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        /**
         * When CrimeHolder's View is clicked, creates an Intent that starts
         * an instance of CrimePagerActivity.
         *
         * @param v
         */
        @Override
        public void onClick(View v) {

            // Store adapter position of Crime
            mCrimePosition = getAdapterPosition();

            // Create an intent with extra UUID and send to CrimePagerActivity
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getID());

            // Call startActivity method on the intent
            startActivity(intent);
        }

    }



}
