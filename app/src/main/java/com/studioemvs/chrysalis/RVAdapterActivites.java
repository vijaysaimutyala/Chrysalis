package com.studioemvs.chrysalis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studioemvs.chrysalis.models.ActivityPointsBean;

import java.util.List;

/**
 * Created by vijsu on 25-07-2017.
 */

public class RVAdapterActivites extends RecyclerView.Adapter<RVAdapterActivites.ActivitiesViewHolder>{
    List<ActivityPointsBean> activityList;

    RVAdapterActivites(List<ActivityPointsBean> activityList) {
        this.activityList = activityList;
    }

    @Override
    public RVAdapterActivites.ActivitiesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_recent_dummy,viewGroup,false);
        ActivitiesViewHolder activityViewHolder = new ActivitiesViewHolder(view);
        return activityViewHolder;
    }

    @Override
    public void onBindViewHolder(RVAdapterActivites.ActivitiesViewHolder holder, final int position) {
        holder.activity.setText(activityList.get(position).activity);
        holder.points.setText(String.valueOf(activityList.get(position).points));
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent actIntent = new Intent(view.getContext(),ProgressUpdateActivity.class);
                bundle.putInt("actPoints",activityList.get(position).points);
                bundle.putString("activity",activityList.get(position).activity);
                actIntent.putExtras(bundle);
                view.getContext().startActivity(actIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }
    public class ActivitiesViewHolder extends RecyclerView.ViewHolder{
        TextView activity;
        TextView points;
        public CardView cv;


        public ActivitiesViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_recentActivity);
            activity = (TextView)itemView.findViewById(R.id.txt_activity);
            points = (TextView)itemView.findViewById(R.id.txt_points);
        }
    }
}
