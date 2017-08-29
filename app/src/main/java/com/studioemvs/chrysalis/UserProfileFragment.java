package com.studioemvs.chrysalis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hanks.htextview.line.LineTextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.studioemvs.chrysalis.R.drawable.elon;


public class UserProfileFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef;
    String TAG ="User Profile Fragment";
    String userKey,username,chrysLevel,chrysGroup,chrysPoints,chrysSublevel,chrysalisPointsToBeApproved;
    Query userDataQuery,activityQuery,newsQuery;
    ImageView imageView,profilePic;
    ProgressDialog progressDialog;
    Button recentActivity,adminDashboard;
    TextView name,level,points,group,sublevel,pointsToGetApproval,infoForUser;
    Boolean adminState;
    String sublevels []= {"1.1", "1.2","1.3","2.1","2.2","2.3","3.1","3.2","3.3"};
    int reqPointsForJump [] = {1000,1000,1000,1000,1000,1000,1000,1000,1000};
    RecyclerView userNews;
    SharedPreferences imagePref;
    FirebaseRecyclerAdapter<NewsForUser,UserProfileFragment.NewsHolder> toApproveAdapter;
    private static final int SELECT_PICTURE = 100;
    String mImageUri;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri defaultImage = Uri.parse("android.resource://com.studioemvs.chrysalis/drawable/elon");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        mImageUri = preferences.getString("profilepicUri", defaultImage.toString());
    }
    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
                    // Saves image URI as string to Default Shared Preferences
                    SharedPreferences preferences =
                            PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("image", String.valueOf(selectedImageUri));
                    editor.commit();
                    profilePic.setImageURI(selectedImageUri);
                }
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();
        imageView = (ImageView)rootView.findViewById(R.id.background_image_view);
        name = (TextView)rootView.findViewById(R.id.frag_profileName);
        level = (TextView)rootView.findViewById(R.id.frag_chrysLevel);
        points = (TextView)rootView.findViewById(R.id.frag_chrysPoints);
        group = (TextView)rootView.findViewById(R.id.frag_chrysGroup);
        profilePic = (ImageView)rootView.findViewById(R.id.profile_image);
        profilePic.setOnClickListener(this);

        // sublevel = (TextView)rootView.findViewById(R.id.frag_chrysSublevel);
        pointsToGetApproval = (TextView)rootView.findViewById(R.id.frag_toBeApprovedPoints);
        progressDialog = new ProgressDialog(this.getContext());
        //infoForUser = (TextView)rootView.findViewById(R.id.frag_infoText);
        recentActivity = (Button)rootView.findViewById(R.id.recActivityBtn);
        adminDashboard = (Button)rootView.findViewById(R.id.adminDashborad);
        adminDashboard.setVisibility(View.INVISIBLE);
        adminDashboard.setOnClickListener(this);
        recentActivity.setOnClickListener(this);
        // userNews = (RecyclerView)rootView.findViewById(R.id.frag_infoText);
//       infoForUser.setMovementMethod(new ScrollingMovementMethod());


        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap,7);
        imageView.setImageBitmap(blurred);

        profilePic.setImageURI(Uri.parse(mImageUri));

        checkAuthorization();
        return rootView;
    }
    private void checkAuthorization() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(getContext(), "User" +user.getEmail()+"is logged in!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onAuthStateChanged: "+user.getUid());
                    userKey = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: "+userKey+"uid: "+user.getUid());
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    getUserData(userKey);//settingtextView
                    progressDialog.hide();
                    //getNews();

                } else {
                    // User is signed out
                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    private void getUserData(final String userkey) {
        Log.d(TAG, "getUserData: "+userkey);
        userRef.child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);
                Log.d(TAG, "userprofile fragments: "+userProfile);
                adminState = userProfile.getAdmin();
                username = userProfile.getUsername();

                chrysLevel = userProfile.getChrysalisLevel();
               // chrysSublevel = userProfile.getChrysalisSublevel();
                chrysGroup = userProfile.getChrysalisGroup();
                chrysPoints = String.valueOf(userProfile.getChrysalisPoints());
                chrysalisPointsToBeApproved = String.valueOf(userProfile.getChrysalisPointsToBeApproved());
/*                Bundle dataToActivity = new Bundle();
                dataToActivity.putBoolean("adminState",adminState);
                Intent intent = getActivity().getIntent();
                intent.putExtras(dataToActivity);*/
                name.setText(username);
                level.setText(chrysLevel);
                points.setText(chrysPoints);
                group.setText(chrysGroup);
             //   sublevel.setText(chrysSublevel);
                pointsToGetApproval.setText(chrysalisPointsToBeApproved);
                adminButtonVisibility(adminState);

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError);
            }
        });

    }

    private void adminButtonVisibility(Boolean adminState) {
        if (adminState){
            adminDashboard.setVisibility(View.VISIBLE);
        }else{
            adminDashboard.setVisibility(View.GONE);
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

    @SuppressLint("NewApi")
    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(this.getContext());

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.recActivityBtn:
                Intent recIntent = new Intent(getContext(),RecentUpdatesActivity.class);
                startActivity(recIntent);
                break;
            case R.id.adminDashborad:
                Intent adminIntent = new Intent(getContext(),AdminActivity.class);
                startActivity(adminIntent);
                break;
            case R.id.profile_image:
                openImageChooser();
                break;
        }
    }

    public class NewsHolder extends RecyclerView.ViewHolder{
        TextView news;
        CardView newsCardView;
        public NewsHolder(View itemView) {
            super(itemView);
            newsCardView = (CardView)itemView.findViewById(R.id.newsCardView);
            news = (TextView)itemView.findViewById(R.id.newsText);
        }
    }

    private void getNews() {
        newsQuery = mainRef.child("news").orderByKey();
        toApproveAdapter =  new FirebaseRecyclerAdapter<NewsForUser, UserProfileFragment.NewsHolder>(NewsForUser.class,
                R.layout.dummy_news_for_user, UserProfileFragment.NewsHolder.class, newsQuery) {
            @Override
            protected void populateViewHolder(UserProfileFragment.NewsHolder viewHolder, NewsForUser model, int position) {
                DatabaseReference userKeyRef =getRef(position);
                viewHolder.news.setText(model.getNews());
                Log.d(TAG, "populateViewHolder: "+model.getNews());
            }
            @Override
            public NewsForUser getItem(int position){
                return super.getItem(getItemCount()-1-position);
            }
        };
        userNews.setAdapter(toApproveAdapter);

    }
}
