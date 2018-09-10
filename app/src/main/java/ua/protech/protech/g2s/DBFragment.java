package ua.protech.protech.g2s;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class DBFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private DBFragment.ListAdapter adapter;
    private ArrayList<BlindBeacon> blindBeacons;
    private EditText searchbar;
    private ArrayList <BlindBeacon> blindBeaconArrayList = new ArrayList<BlindBeacon>();


    public DBFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LoadData loadData = new LoadData();
        loadData.execute();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_db, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchbar = (EditText) view.findViewById(R.id.search_db);
        searchbar.setEnabled(false);

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                blindBeaconArrayList.clear();
                adapter.notifyDataSetChanged();
                if (blindBeacons != null) {
                    for (int j = 0; j < blindBeacons.size(); j++) {
                        Log.d(Data.TAG, blindBeacons.get(j).getName());
                        if (((blindBeacons.get(j).getName().toUpperCase().contains(searchbar.getText().toString().toUpperCase())) ||
                                (blindBeacons.get(j).getAddr().toUpperCase().contains(searchbar.getText().toString().toUpperCase())))
                                && !searchbar.getText().toString().isEmpty()) {
                            Log.d(Data.TAG, searchbar.getText().toString());
                            blindBeaconArrayList.add(blindBeacons.get(j));
                            adapter.notifyDataSetChanged();
                        }
                    }
                    Log.d(Data.TAG, Integer.toString(blindBeaconArrayList.size()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData loadData = new LoadData();
        loadData.execute();
    }

    public static DBFragment newInstance() {
        DBFragment f = new DBFragment();
        return f;
    }

    private class LoadData extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            blindBeacons = Data.getSerialized_beacons(getActivity().getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            adapter = new DBFragment.ListAdapter(blindBeaconArrayList);
            Log.d(Data.TAG, "getting data");
            recyclerView.setAdapter(adapter);
            if (blindBeacons != null)
                searchbar.setEnabled(true);
            else
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.db_not_found), Toast.LENGTH_LONG).show();
        }
    }

    private class ListAdapter extends RecyclerView.Adapter<DBFragment.ListAdapter.ViewHolder> {

        ArrayList<BlindBeacon> beacon_list = new ArrayList<>();
        BlindBeacon beacon;

        ListAdapter(ArrayList<BlindBeacon> b) {
            beacon_list = b;
        }

        @Override
        public DBFragment.ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.beacon_list_row, parent, false);
            DBFragment.ListAdapter.ViewHolder vh = new DBFragment.ListAdapter.ViewHolder(v);
            return vh;
        }


        @Override
        public void onBindViewHolder(final DBFragment.ListAdapter.ViewHolder holder, int position) {
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
