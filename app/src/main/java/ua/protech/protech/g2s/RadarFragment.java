package ua.protech.protech.g2s;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class RadarFragment extends Fragment {

    private RecyclerView listView;
    private LinearLayout guide_view;
    private RadarFragment.ListAdapter itemsAdapter;
    private SharedPreferences sharedPreferences;
    private ArrayList<BlindBeacon> beaconsToShow = new ArrayList<>();

    public RadarFragment() {

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radar, container, false);
        beaconsToShow.addAll(Data.getBeaconsAfterScan());
        itemsAdapter = new ListAdapter(beaconsToShow);
        listView = (RecyclerView) view.findViewById(R.id.list);
        guide_view = (LinearLayout) view.findViewById(R.id.guide_layout);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(itemsAdapter);
        listView.setVisibility(View.VISIBLE);
        listView.setLongClickable(true);
        listView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity().getApplicationContext(), listView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Intent i = new Intent(getContext(), BeaconDetailActivity.class);
                i.putExtra(Data.PASS_MAC, Data.getBeaconsAfterScan().get(pos).getUuid());
                i.putExtra(Data.PASS_SSID, Data.getBeaconsAfterScan().get(pos).getSsid());
                i.putExtra(Data.IS_WENT_FROM_RADAR, true);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int pos) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Маяк: " + Data.getBeaconsAfterScan().get(pos).getName());
                }
            }
        }));

        sharedPreferences = getActivity().getSharedPreferences(Data.SETTINGS_FILE_SHARED_PREF, Context.MODE_PRIVATE);

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onResume() {
        ServiceMessages stickyEvent = EventBus.getDefault().getStickyEvent(ServiceMessages.class);
        if (beaconsToShow != null && itemsAdapter != null)
        {
            beaconsToShow.clear();
            itemsAdapter.notifyDataSetChanged();
            beaconsToShow.addAll(Data.getBeaconsAfterScan());
            itemsAdapter.notifyDataSetChanged();
        }

        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
            beaconsToShow.clear();
            itemsAdapter.notifyDataSetChanged();
            beaconsToShow.addAll(Data.getBeaconsAfterScan());
            itemsAdapter.notifyDataSetChanged();
        }

        if (sharedPreferences.getBoolean((Data.IS_GUIDE), false)) {
            guide_view.setVisibility(View.VISIBLE);
        } else {
            guide_view.setVisibility(View.GONE);
        }

        super.onResume();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServiceMessages event) {
        beaconsToShow.clear();
        beaconsToShow.addAll(Data.getBeaconsAfterScan());
        itemsAdapter.notifyDataSetChanged();
        for (BlindBeacon b: beaconsToShow) {
            Log.e("@@@", b.toString());
        }
    }

    public static RadarFragment newInstance() {
        RadarFragment f = new RadarFragment();
        return f;
    }

    public interface RecyclerViewClickListener {
        void onLongClick(View view, int position);
        void onClick(View view, int position);
    }

    public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private RecyclerViewClickListener clickListener;

        RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, final RecyclerViewClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    private class ListAdapter extends RecyclerView.Adapter<RadarFragment.ListAdapter.ViewHolder> {

        ArrayList<BlindBeacon> beacon_list;
        BlindBeacon beacon;

        ListAdapter(ArrayList<BlindBeacon> b) {
            beacon_list = b;
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.beacon_list_row, parent, false);
            ListAdapter.ViewHolder vh = new ListAdapter.ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final ListAdapter.ViewHolder holder, int position) {
            beacon = beacon_list.get(position);
            holder.title.setText(beacon.getName());
        }

        @Override
        public int getItemCount() {
            if (!beacon_list.isEmpty())
                return beacon_list.size();
            else return 0;
        }


        class ViewHolder extends RecyclerView.ViewHolder

        {
            TextView title;
            LinearLayout linearLayout;

            ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
                linearLayout = (LinearLayout) v.findViewById(R.id.linear);
            }
        }
    }

}
