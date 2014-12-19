package com.loc.wayne.libraryofcongressandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private PeriodCycleFragment periodCycleFragment;
    private ViewPager viewPager;
    private SwipeModePagerAdapter pagerAdapter;
    private boolean periodOrPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerAdapter = new SwipeModePagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        if (savedInstanceState == null) {
            periodCycleFragment = new PeriodCycleFragment();
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentFrame, periodCycleFragment, "periodCycleFragment");
            ft.commit();

            new GetLibraryData().execute();
        } else {
            periodCycleFragment = (PeriodCycleFragment) getSupportFragmentManager().findFragmentByTag("periodCycleFragment");
            periodOrPager = savedInstanceState.getBoolean("periodOrPager");
        }

        setupPeriodOrPager(true);

        final Button b = (Button) findViewById(R.id.switchFragments);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPeriodOrPager(false);
                periodOrPager ^= true;
            }
        });
    }

    private void setupPeriodOrPager(boolean flip) {
        final View ff = findViewById(R.id.fragmentFrame);
        final Button b = (Button) findViewById(R.id.switchFragments);

        boolean pop = periodOrPager;
        pop ^= flip;

        if (!pop) {
            ff.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            b.setText("Grid View");
        } else {
            ff.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            b.setText("View Pager");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("periodOrPager", periodOrPager);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0 || !periodOrPager)
            super.onBackPressed();
        else
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    private class GetLibraryData extends GetLibraryRecordsTask {

        @Override
        protected void onPostExecute(Hashtable<Integer, LibraryOfCongressRecord> locResults) {
            if (periodCycleFragment != null)
                periodCycleFragment.updateAdapterAndStartShuffleTimer(locResults);

            if(pagerAdapter != null)
                pagerAdapter.updateRecords(locResults);
        }
    }

    private class SwipeModePagerAdapter extends FragmentStatePagerAdapter {

        private List<LibraryOfCongressRecord> records;

        public void updateRecords(Hashtable<Integer, LibraryOfCongressRecord> ht) {
            records = new ArrayList<LibraryOfCongressRecord>(ht.values());
            notifyDataSetChanged();
        }

        public SwipeModePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (records == null || records.size() < position + 1)
                return null;
            else {
                SwipeModePageFragment f = new SwipeModePageFragment();
                f.record = records.get(position);
                return f;
            }
        }

        @Override
        public int getCount() {
            if (records == null)
                return 0;
            else
                return records.size();
        }
    }

    public static class SwipeModePageFragment extends Fragment {

        public LibraryOfCongressRecord record;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.swipe_mode_page, container, false);
            TextView indexView = (TextView) v.findViewById(R.id.locIndex);
            TextView titleView = (TextView) v.findViewById(R.id.locTitle);
            ImageView imageView = (ImageView) v.findViewById(R.id.locImage);

            if (record != null) {
                if (indexView != null)
                    indexView.setText(String.valueOf(record.getIndex()));

                if (titleView != null) {
                    titleView.setText(record.getTitle());
                    titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                }

                if (imageView != null)
                    Picasso.with(getActivity()) //
                            .load(record.getImageUrl()) //
                            .placeholder(R.drawable.ic_launcher) //
                            .error(R.drawable.error) //
                                    // .fit() //
                            .into(imageView);
            }

            return v;
        }
    }
}
