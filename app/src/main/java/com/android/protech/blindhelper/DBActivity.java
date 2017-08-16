package com.android.protech.blindhelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DBActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private ArrayList<BlindBeacon> blindBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbactivity);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LoadData loadData = new LoadData();
        loadData.execute();
    }

    private class LoadData extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            blindBeacons = ServerRoutine.getBeaconsFromDB();
            Log.d("db_size", Integer.toString(blindBeacons.size()));
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            adapter = new ListAdapter(blindBeacons);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

        ArrayList<BlindBeacon> beacon_list = new ArrayList<>();
        BlindBeacon beacon;

        ListAdapter(ArrayList<BlindBeacon> b){
            beacon_list = b;
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.db_list_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            beacon = beacon_list.get(position);
            holder.title.setText(beacon.getName());
            holder.description.setText(beacon.getDescription());
        }

        @Override
        public int getItemCount() {
            if (beacon_list!=null)
            return beacon_list.size();
            else return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            TextView description;
            LinearLayout linearLayout;
            ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
                description = (TextView) v.findViewById(R.id.description);
                linearLayout = (LinearLayout) v.findViewById(R.id.linear);
                linearLayout.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int pos = getLayoutPosition();
                Intent i = new Intent(DBActivity.this, BeaconDetailActivity.class);
                i.putExtra(Data.PASS_BSSID, blindBeacons.get(pos).getUuid());
                startActivity(i);
            }
        }
    }
}
