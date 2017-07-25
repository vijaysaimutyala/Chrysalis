package com.studioemvs.chrysalis;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class UpdateActivityFragment extends Fragment {
    RecyclerView activitiesRecycler;
    private List<ActivityPointsBean> activityList;
    RVAdapterActivites recyclerAdapter;


    public UpdateActivityFragment() {
        // Required empty public constructor
    }

    public static UpdateActivityFragment newInstance() {
        UpdateActivityFragment fragment = new UpdateActivityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_update_activity, container, false);
        activitiesRecycler = (RecyclerView)rootView.findViewById(R.id.rv_fragment_recentActivity);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        activitiesRecycler.setLayoutManager(llm);
        activitiesRecycler.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
        return rootView;
    }
    private void initializeData() {
        activityList = new ArrayList<>();
        activityList.add(new ActivityPointsBean("Participated in 1 meeting",50));
        activityList.add(new ActivityPointsBean("Conducted Training Session",100));
        activityList.add(new ActivityPointsBean("Attended Speaker session",100));
        activityList.add(new ActivityPointsBean("Attended ETA Training",200));
        activityList.add(new ActivityPointsBean("Complete Accelerate Job",200));
        activityList.add(new ActivityPointsBean("Conducted Training",500));
        activityList.add(new ActivityPointsBean("Delivered Speaker session",500));
        activityList.add(new ActivityPointsBean("Built reusable code/libraries",500));
        activityList.add(new ActivityPointsBean("Participated in an event",500));
        activityList.add(new ActivityPointsBean("Conducted events",1000));
        activityList.add(new ActivityPointsBean("Admin contribution",1000));
        activityList.add(new ActivityPointsBean("Built POC",1000));
        activityList.add(new ActivityPointsBean("Deployed real life solutions",2500));
        activityList.add(new ActivityPointsBean("Participated in 3 consecutive meetings",100));
        activityList.add(new ActivityPointsBean("Participated in 5 consecutive meetings",150));
        activityList.add(new ActivityPointsBean("Participated in 10 consecutive meetings",250));
        activityList.add(new ActivityPointsBean("Participated in 10 consecutive meetings",500));

    }

    private void initializeAdapter() {
        recyclerAdapter = new RVAdapterActivites(activityList);
        activitiesRecycler.setAdapter(recyclerAdapter);
    }


}
