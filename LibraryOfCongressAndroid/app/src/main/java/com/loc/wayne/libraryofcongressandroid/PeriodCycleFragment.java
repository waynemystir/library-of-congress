package com.loc.wayne.libraryofcongressandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wayne on 12/18/14.
 */
public class PeriodCycleFragment extends Fragment {

    private LocResultsAdapter locResultsAdapter;
    private Timer shuffleTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        locResultsAdapter = new LocResultsAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        final View v = inflater.inflate(R.layout.period_cycle_fragment, null, false);
        GridView recordsGridView = (GridView) v.findViewById(R.id.recordsGridView);
        recordsGridView.setAdapter(locResultsAdapter);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shuffleTimer != null) {
            shuffleTimer.cancel();
            shuffleTimer = null;
        }
    }

    public void updateAdapterAndStartShuffleTimer(Hashtable<Integer, LibraryOfCongressRecord> locResults) {
        if (locResultsAdapter != null) {
            locResultsAdapter.updateRecords(locResults);
            shuffleTimer = new Timer();
            shuffleTimer.schedule(new ShuffleTimerTask(), 15*1000, 15*1000);
        }
    }

    private class LocResultsAdapter extends BaseAdapter {

        private List<LibraryOfCongressRecord> records;

        public void updateRecords(Hashtable<Integer, LibraryOfCongressRecord> ht) {
            records = new ArrayList<LibraryOfCongressRecord>(ht.values());
            notifyDataSetChanged();
        }

        public void shuffleViews() {
            long seed = System.nanoTime();
            Collections.shuffle(records, new Random(seed));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (records == null)
                return 0;
            else
                return records.size();
        }

        @Override
        public LibraryOfCongressRecord getItem(int position) {
            if (records == null || records.size() < position + 1)
                return null;
            else
                return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parentView) {
            TextView indexView = null;
            TextView titleView = null;
            ImageView imageView = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.loc_record_view, parentView, false);
                indexView = (TextView) convertView.findViewById(R.id.locIndex);
                titleView = (TextView) convertView.findViewById(R.id.locTitle);
                imageView = (ImageView) convertView.findViewById(R.id.locImage);
                convertView.setTag(new ViewHolder(indexView, titleView, imageView));
            } else {
                ViewHolder vh = (ViewHolder) convertView.getTag();
                indexView = vh.indexView;
                titleView = vh.titleView;
                imageView = vh.imageView;
            }

            LibraryOfCongressRecord locRecord = getItem(position);

            if (locRecord != null) {
                if (indexView != null)
                    indexView.setText(String.valueOf(locRecord.getIndex()));

                if (titleView != null)
                    titleView.setText(locRecord.getTitle());

                if (imageView != null)
                    Picasso.with(getActivity()) //
                            .load(locRecord.getImageUrl()) //
                            .placeholder(R.drawable.ic_launcher) //
                            .error(R.drawable.error) //
                                    // .fit() //
                            .into(imageView);
            }

            return convertView;
        }

        private class ViewHolder {
            public final TextView indexView;
            public final TextView titleView;
            public final ImageView imageView;

            public ViewHolder(TextView indexView, TextView titleView, ImageView imageView) {
                this.indexView = indexView;
                this.titleView = titleView;
                this.imageView = imageView;
            }
        }
    }

    private class ShuffleTimerTask extends TimerTask {

        @Override
        public void run() {
            if (locResultsAdapter != null && getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locResultsAdapter.shuffleViews();
                    }
                });
        }
    }

}
