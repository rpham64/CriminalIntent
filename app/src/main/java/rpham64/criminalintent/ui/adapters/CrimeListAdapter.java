package bignerdranch.criminalintent.ui.adapters;

/**
 * Created by Rudolf on 10/9/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import bignerdranch.criminalintent.R;
import bignerdranch.criminalintent.models.Crime;
import bignerdranch.criminalintent.models.CrimeLab;
import bignerdranch.criminalintent.ui.CrimePagerActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrimeListAdapter extends RecyclerView.Adapter<CrimeListAdapter.CrimeHolder>{

    private Context mContext;
    private List<Crime> mCrimes;

    public CrimeListAdapter(Context context, List<Crime> crimes) {
        mContext = context;
        mCrimes = crimes;
    }

    @Override
    public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_crime, parent, false);
        return new CrimeHolder(view);
    }

    @Override
    public void onBindViewHolder(CrimeHolder crimeHolder, int position) {
        Crime crime = mCrimes.get(position);
        crimeHolder.bindCrime(crime);
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;
    }

    class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.list_item_crime_title_text_view) TextView txtTitle;
        @BindView(R.id.list_item_crime_date_text_view) TextView txtDate;
        @BindView(R.id.list_item_crime_solved_check_box) CheckBox chkBoxSolved;

        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

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
            txtTitle.setText(mCrime.getTitle());
            txtDate.setText(mCrime.getDate().toString());
            chkBoxSolved.setChecked(mCrime.isSolved());
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(mContext, mCrimes.get(position).getId());
            mContext.startActivity(intent);
        }

        @OnClick(R.id.list_item_crime_solved_check_box)
        public void onSolved() {
            mCrime.setSolved(chkBoxSolved.isChecked());
            CrimeLab.get(mContext).updateCrime(mCrime);
        }
    }

}