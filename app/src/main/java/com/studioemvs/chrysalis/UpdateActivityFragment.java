package com.studioemvs.chrysalis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studioemvs.chrysalis.models.ActivitiesBean;
import com.studioemvs.chrysalis.models.ActivityPointsBean;

import java.util.List;


public class UpdateActivityFragment extends Fragment {
    RecyclerView activitiesRecycler;
    private List<ActivityPointsBean> activityList;
    RVAdapterActivites recyclerAdapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef,activityRef;
    FirebaseRecyclerAdapter<ActivitiesBean,UpdateActivityFragment.ActivitiesViewHolder> activityAdapter;
    String userKey;
    String TAG = "UpdateActivityFrgament";
    ProgressDialog progressDialog;
    LinearLayoutManager linearLayoutManager;


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
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_update_activity, container, false);
        progressDialog = new ProgressDialog(this.getActivity());
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        activityRef = mainRef.child("activites");
        activitiesRecycler = (RecyclerView)rootView.findViewById(R.id.rv_fragment_recentActivity);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        activitiesRecycler.setHasFixedSize(true);

        checkAuthorization();
        return rootView;
    }
    private void checkAuthorization() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, user.getEmail()+" is signed in");
                    // User is signed in
                    userKey = user.getUid();
                    progressDialog.setMessage("Getting Activities");
                    progressDialog.show();
                    getActivities();

                } else {
                    // User is signed out
                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    Log.d(TAG, "signed_out");
                }
            }
        };
    }
    private void getActivities() {
        activityAdapter = new FirebaseRecyclerAdapter<ActivitiesBean, UpdateActivityFragment.ActivitiesViewHolder>
                (ActivitiesBean.class,R.layout.activity_recent_dummy,UpdateActivityFragment.ActivitiesViewHolder.class,activityRef) {
            @Override
            protected void populateViewHolder(ActivitiesViewHolder viewHolder, final ActivitiesBean model, int position) {
//                Log.d(TAG, "populateViewHolder: "+model.getActivity());
                viewHolder.activity.setText(model.getActivity());
                viewHolder.cv.setRadius(2);
                viewHolder.points.setText(String.valueOf(model.getActPoints()));

                viewHolder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Intent actIntent = new Intent(view.getContext(),ProgressUpdateActivity.class);
                        bundle.putInt("actPoints",model.getActPoints());
                        bundle.putString("activity",model.getActivity());
                        actIntent.putExtras(bundle);
                        view.getContext().startActivity(actIntent);
                    }
                });

            }
        };
        activityAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int activityCount = activityAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (activityCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    activitiesRecycler.scrollToPosition(positionStart);
                }
            }
        });
        activitiesRecycler.setLayoutManager(linearLayoutManager);
        activitiesRecycler.setAdapter(activityAdapter);
        progressDialog.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    public static class ActivitiesViewHolder extends RecyclerView.ViewHolder{
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
