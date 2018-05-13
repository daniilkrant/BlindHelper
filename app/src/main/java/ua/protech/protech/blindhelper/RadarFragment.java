package ua.protech.protech.blindhelper;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sm.euzee.github.com.servicemanager.ServiceManager;

public class RadarFragment extends Fragment {

    private RecyclerView listView;
    private static RadarFragment.ListAdapter itemsAdapter;
    private SharedPreferences sharedPreferences;

    public RadarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radar, container, false);
        itemsAdapter = new ListAdapter(Data.getBeaconsAfterScan());
        listView = (RecyclerView) view.findViewById(R.id.list);
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

        Intent intent = new Intent(getActivity().getApplicationContext(), ScaningService.class);
        ServiceManager.runService(getActivity().getApplicationContext(), intent);

        return view;
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
