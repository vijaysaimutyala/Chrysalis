package com.studioemvs.chrysalis;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.studioemvs.chrysalis.models.User;

import java.io.IOException;
import java.net.URISyntaxException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef;
    Uri defaultImage,profileImageUri;
    String TAG ="User Profile Fragment";
    String userKey,username,chrysLevel,chrysGroup,chrysPoints,chrysSublevel,chrysalisPointsToBeApproved;
    Query userDataQuery,activityQuery,newsQuery;
//    ImageView imageView;
    ImageView profilePic;
    ProgressDialog progressDialog;
    Button recentActivity,adminDashboard;
    TextView name,level,points,group,sublevel,pointsToGetApproval,infoForUser;
    RelativeLayout userProfileView;
    Boolean adminState,instructorState;
    String sublevels []= {"1.1", "1.2","1.3","2.1","2.2","2.3","3.1","3.2","3.3"};
    int reqPointsForJump [] = {1000,1000,1000,1000,1000,1000,1000,1000,1000};
    RecyclerView userNews;
    SharedPreferences sharedPreferences;
    FirebaseRecyclerAdapter<NewsForUser,UserProfileFragment.NewsHolder> toApproveAdapter;
    private static final int PICK_IMAGE_REQUEST = 234;
    String mImageUri = "ImageUri";
    //a Uri object to store file path
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference profilepicRef;
    String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
    private static final int PERMISSION_REQUEST_CODE = 200;



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
        mAuth = FirebaseAuth.getInstance();
        defaultImage = Uri.parse("android.resource://com.studioemvs.chrysalis/mipmap/ic_launcher");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if(sharedPreferences.getString(mImageUri,null)!=null){
            Uri uri = Uri.parse(sharedPreferences.getString(mImageUri,null));
            profileImageUri = uri;
        }else {
            profileImageUri = defaultImage;
        }
    }
    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(mImageUri,getFilePath(this.getActivity(),filePath));
                editor.commit();
               // uploadFile();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
    //this method will upload the file
    private void uploadFile() {

        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            profilepicRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying a success toast
                            Toast.makeText(getActivity().getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getActivity().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
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
        userProfileView = (RelativeLayout)rootView.findViewById(R.id.userProfileRelview);
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        profilepicRef = storageReference.child("images/"+userKey+"/profilepic.jpg");
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
//        imageView = (ImageView)rootView.findViewById(R.id.background_image_view);
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


//        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//        Bitmap blurred = blurRenderScript(bitmap,7);
//        imageView.setImageBitmap(blurred);

        if (profileImageUri !=null){
            profilePic.setImageURI(profileImageUri);
        }else {
            profilePic.setImageURI(defaultImage);
        }
        checkAuthorization();
       // setProfilePic();
        return rootView;
    }

    private void setProfilePic() {
        Glide.with(this.getActivity())
                .using(new FirebaseImageLoader())
                .load(profilepicRef)
                .into(profilePic);
    }


    private void checkAuthorization() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: "+user.getUid());
                    userKey = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: "+userKey+"uid: "+user.getUid());
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    getUserData(userKey);//settingtextView
                    //setProfilePic();
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
                instructorState = userProfile.getInstructor();
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
            adminDashboard.setText(getResources().getString(R.string.adminPage));
            adminDashboard.setVisibility(View.VISIBLE);
        }else if(instructorState){
            adminDashboard.setText(getResources().getString(R.string.instructorPage));
            adminDashboard.setVisibility(View.VISIBLE);
        }else {
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

//    @SuppressLint("NewApi")
//    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {
//
//        try {
//            smallBitmap = RGB565toARGB888(smallBitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        Bitmap bitmap = Bitmap.createBitmap(
//                smallBitmap.getWidth(), smallBitmap.getHeight(),
//                Bitmap.Config.ARGB_8888);
//
//        RenderScript renderScript = RenderScript.create(this.getContext());
//
//        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
//        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);
//
//        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
//                Element.U8_4(renderScript));
//        blur.setInput(blurInput);
//        blur.setRadius(radius); // radius must be 0 < r <= 25
//        blur.forEach(blurOutput);
//
//        blurOutput.copyTo(bitmap);
//        renderScript.destroy();
//
//        return bitmap;
//
//    }
//
//    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
//        int numPixels = img.getWidth() * img.getHeight();
//        int[] pixels = new int[numPixels];
//
//        //Get JPEG pixels.  Each int is the color values for one pixel.
//        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
//
//        //Create a Bitmap of the appropriate format.
//        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
//
//        //Set RGB pixels.
//        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
//        return result;
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.recActivityBtn:
                Intent recIntent = new Intent(getContext(),RecentUpdatesActivity.class);
                startActivity(recIntent);
                break;
            case R.id.adminDashborad:
                Intent adminIntent = new Intent(getContext(),AdminActivity.class);
                Intent instructorIntent = new Intent(getContext(),AddNewActivity.class);
                Bundle instructBundle = new Bundle();
                Toast.makeText(getActivity(), " "+userKey, Toast.LENGTH_SHORT).show();
                instructBundle.putString("instructorid",userKey);
                instructorIntent.putExtras(instructBundle);
                if (adminState) {
                    startActivity(adminIntent);
                }else if(instructorState){
                    startActivity(instructorIntent);
                }
                break;
            case R.id.profile_image:
                if (checkPermission()){
                    openImageChooser();
                }else {
                    requestPermission();
                    openImageChooser();
                }
                break;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted)
                        Snackbar.make(userProfileView, "Permission Granted, Now you can change your profile pic", Snackbar.LENGTH_LONG).show();
                    else {

                        Snackbar.make(userProfileView, "Permission Denied, You cannot access storage to change your profile pic.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access the storage permission to change your profile pic",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this.getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
