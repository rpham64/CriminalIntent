package bignerdranch.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rudolf on 2/10/2016.
 */
public class CrimeListFragment extends Fragment {

    // RecyclerView
    private RecyclerView mCrimeRecyclerView;

    // Adapter
    private CrimeAdapter mAdapter;

    // Position of Crime in Adapter (for updating View objects)
    private int mCrimePosition;

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

        // Inflate the View
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // Call reference to mCrimeRecyclerView
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        // Set layout manager for mCrimeRecyclerView
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Update the user interface
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        // If mAdapter is null, create a new CrimeAdapter and set to mCrimeRecyclerView's adapter
        // Else, call notifyDataSetChanged
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            // Notify adapter that item changed at mCrimePosition
            mAdapter.notifyItemChanged(mCrimePosition);
        }

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
