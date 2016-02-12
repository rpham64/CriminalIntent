package bignerdranch.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    // RecyclerView
    private RecyclerView mCrimeRecyclerView;

    // Adapter
    private CrimeAdapter mAdapter;

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

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        mAdapter = new CrimeAdapter(crimes);

        // Set adapter of mCrimeRecyclerView to mAdapter
        mCrimeRecyclerView.setAdapter(mAdapter);
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

/*        // Date Format
        String format = "EEEE MMM dd, yyyy KK:mm:ss aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        String formattedDate = simpleDateFormat.format(mCrime.getDate());*/

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
         * When CrimeHolder is clicked, creates Toast
         *
         * @param v
         */
        @Override
        public void onClick(View v) {

            String message = mCrime.getTitle() + " clicked!";

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

    }



}
