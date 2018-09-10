package ua.protech.protech.g2s;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private FavouriteFragment.ListAdapter adapter;
    private ArrayList<BlindBeacon> blindBeacons;
    private ArrayList <BlindBeacon> blindBeaconArrayList = new ArrayList<BlindBeacon>();

    public FavouriteFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment_favourite, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new FavouriteFragment.ListAdapter(blindBeaconArrayList);
            recyclerView.setAdapter(adapter);

            return view;
        }

    @Override
    public void onResume() {
        FavouriteFragment.LoadData loadData = new FavouriteFragment.LoadData();
        loadData.execute();

        super.onResume();
    }

    public static FavouriteFragment newInstance() {
        FavouriteFragment f = new FavouriteFragment();
        return f;
    }

    private class LoadData extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            BlindBeacon.UpdList(getActivity().getApplicationContext());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(Data.TAG, "Getting data");
            blindBeacons = Data.getSerialized_beacons(getActivity().getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            blindBeaconArrayList.clear();
            if (blindBeacons != null) {
                for (int i = 0; i < blindBeacons.size(); i++) {
                    if (blindBeacons.get(i).isFav() == 1) {
                        blindBeaconArrayList.add(blindBeacons.get(i));
                    }
                }
            } else
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.db_not_found), Toast.LENGTH_LONG).show();

            adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<FavouriteFragment.ListAdapter.ViewHolder> {

        ArrayList<BlindBeacon> beacon_list = new ArrayList<>();
        BlindBeacon beacon;

        ListAdapter(ArrayList<BlindBeacon> b) {
            beacon_list = b;
        }

        @Override
        public FavouriteFragment.ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.beacon_list_row, parent, false);
            FavouriteFragment.ListAdapter.ViewHolder vh = new FavouriteFragment.ListAdapter.ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final FavouriteFragment.ListAdapter.ViewHolder holder, int position) {
            beacon = beacon_list.get(position);
            holder.title.setText(beacon.getName());
        }

        @Override
        public int getItemCount() {
            if (beacon_list != null)
                return beacon_list.size();
            else return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            LinearLayout linearLayout;

            ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
                linearLayout = (LinearLayout) v.findViewById(R.id.linear);
                linearLayout.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int pos = getLayoutPosition();
                Intent i = new Intent(getContext(), BeaconDetailActivity.class);
                i.putExtra(Data.PASS_MAC, blindBeaconArrayList.get(pos).getUuid());
                i.putExtra(Data.IS_WENT_FROM_RADAR, false);
                startActivity(i);
            }
        }
    }
}
