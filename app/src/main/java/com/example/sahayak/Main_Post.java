package com.example.sahayak;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.JetPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Main_Post#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main_Post extends Fragment implements OnMapReadyCallback {


    View v;
    StorageReference storage_ref;
    private GoogleMap mMap;
    private double latitude = 0;
    private double longitude = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Main_Post() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main_Post.
     */
    // TODO: Rename and change types and number of parameters
    public static Main_Post newInstance(String param1, String param2) {
        Main_Post fragment = new Main_Post();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_main_post, container, false);

        TextView title= v.findViewById(R.id.post_title);
        TextView desc= v.findViewById(R.id.post_description);
        TextView pin= v.findViewById(R.id.post_pin);
        TextView numberoflikes= v.findViewById(R.id.numberoflikesmainpost);
        Button claimissue=v.findViewById(R.id.claimissuebtn);
        Button issueresolved=v.findViewById(R.id.issueresolvedbtn);
        Button abortissue=v.findViewById(R.id.abortissuebtn);
        Button likeButton = v.findViewById(R.id.like_button);
        Button sharebtn= v.findViewById(R.id.share_button);
        ImageView issue_image= v.findViewById((R.id.issue_image));
        ImageView imageview=v.findViewById(R.id.issue_image);
        //
        storage_ref = FirebaseStorage.getInstance().getReference();
        //inital visibility is 0
        claimissue.setVisibility(View.GONE);
        issueresolved.setVisibility(View.GONE);
        abortissue.setVisibility(View.GONE);
        likeButton.setVisibility(View.GONE);
        sharebtn.setVisibility(View.GONE);

        Bundle bundle= getArguments();
        feedItem fc= (feedItem) bundle.getSerializable("pos");



        //to check the type of current user
        final String[] user_type = new String[1];


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final HashMap<String, Object>[] current_issue = new HashMap[]{new HashMap<String, Object>()};
        final HashMap<String, Object>[] current_user = new HashMap[]{new HashMap<String, Object>()};

        DocumentReference docRef = db.collection("Issue_detail").document(""+fc.id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i("innoc","we are here");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        current_issue[0] = (HashMap<String, Object>) document.getData();
                        numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                        String image_path= (String) current_issue[0].get("image_path");
                        String path=current_issue[0].get("image_path").toString();
                        latitude = (double) current_issue[0].get("latitude");
                        longitude = (double) current_issue[0].get("longitude");
                        LatLng latlong = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latlong);
                        markerOptions.title((String) current_issue[0].get("title"));
                        mMap.clear();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong,15));
                        // Zoom in, animating the camera.
                        //mMap.animateCamera(CameraUpdateFactory.zoomIn());
                        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latlong));
                        mMap.addMarker(markerOptions);
                        Log.d("pathh", " "+path);
                        storage_ref = FirebaseStorage.getInstance().getReference().child(path);
                        try{
                            final File localFile=File.createTempFile("temp","jpeg");
                            storage_ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap=BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    imageview.setImageBitmap(bitmap);
                                    sharebtn.setClickable(true);
                                    sharebtn.setVisibility(View.VISIBLE);
                                    sharebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String text = (String) current_issue[0].get(
                                                    "description")+"\n*Checkout our Application here*\nhttps://sahayakapp.page.link/issue";
                                            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Image Description", null);
                                            Uri uri = Uri.parse(path);
                                            Intent shareIntent = new  Intent();
                                            shareIntent.setAction(Intent.ACTION_SEND);
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                                            //shareIntent.putExtra(Intent.EXTRA_TEXT,"https://sahayakapp.page.link/feed");
                                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                            shareIntent.setType("image/*");
                                            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(Intent.createChooser(shareIntent, "Share images..."));
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener(){

                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                        catch(Exception e){

                        }

                        //to avoid 3 secs delay
                        title.setText(((String)current_issue[0].get("title")));
                        desc.setText((String)current_issue[0].get("description"));
                        pin.setText(""+(String)current_issue[0].get("pin_code"));
                        likeButton.setVisibility(View.VISIBLE);
                        sharebtn.setVisibility(View.VISIBLE);

                        FirebaseFirestore db2= FirebaseFirestore.getInstance();
                        Log.i("mail",user.getEmail());
                        DocumentReference docRefuser = db2.collection("users").document(user.getEmail());
                        docRefuser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.i("usss",""+user_type[0]);
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        current_user[0]= (HashMap<String, Object>) document.getData();
                                        user_type[0] = (String) document.get("type");
                                        ArrayList<String> issueclaimed= (ArrayList<String>) document.get("issue_claims");
                                        Log.i("usss",""+user_type[0]);
                                        if(user_type[0].equals("NGO")|| user_type[0].equals("Social Worker")){
                                            claimissue.setVisibility(View.VISIBLE);
                                            issueresolved.setVisibility(View.VISIBLE);
                                            abortissue.setVisibility(View.VISIBLE);
                                            claimissue.setClickable(true);
                                            issueresolved.setClickable(false);
                                            abortissue.setClickable(false);
                                            abortissue.setVisibility(View.GONE);
                                            issueresolved.setVisibility(View.GONE);
                                            if (!current_issue[0].get("Status").equals("Unclaimed")){
                                                claimissue.setClickable(false);
                                                issueresolved.setClickable(false);
                                                abortissue.setClickable(false);
                                                claimissue.setVisibility(View.GONE);
                                                abortissue.setVisibility(View.GONE);
                                                issueresolved.setVisibility(View.GONE);

                                            }
                                            if(issueclaimed.contains(fc.id)){
                                                claimissue.setClickable(false);
                                                issueresolved.setClickable(true);
                                                abortissue.setClickable(true);
                                                claimissue.setVisibility(View.GONE);
                                                issueresolved.setVisibility(View.VISIBLE);
                                                abortissue.setVisibility(View.VISIBLE);

                                            }

                                        }

                                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                                    } else {
                                        Log.d("tag", "No such document");
                                    }
                                } else {
                                    Log.d("tag", "get failed with ", task.getException());
                                }
                            }
                        });



                        //issue is claimed by yser
                        claimissue.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {
                                claimissue.setVisibility(View.GONE);
                                issueresolved.setVisibility(View.VISIBLE);
                                abortissue.setVisibility(View.VISIBLE);
                                claimissue.setClickable(false);
                                issueresolved.setClickable(true);
                                abortissue.setClickable(true);
                                //change status to claimed
                                current_issue[0].replace("Status","Claimed");
                                db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // on successful completion of this process
                                        // we are displaying the toast message.
                                        Toast.makeText(getActivity(), "You have clamed this issue..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    // inside on failure method we are
                                    // displaying a failure message.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to update the number of likes..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                ArrayList<String> issuesclamied= (ArrayList<String>) current_user[0].get("issue_claims");
                                issuesclamied.add(fc.id);
                                current_user[0].replace("issue_claims",issuesclamied);
                                db.collection("users").document(user.getEmail()).set(current_user[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // on successful completion of this process
                                        // we are displaying the toast message.
                                        Toast.makeText(getActivity(), "You have claimed this issue..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    // inside on failure method we are
                                    // displaying a failure message.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to update the number of likes..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                        issueresolved.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {

                                current_issue[0].replace("Status","Resolved");
                                db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // on successful completion of this process
                                        // we are displaying the toast message.
                                        Toast.makeText(getActivity(), "You have resolved this issue..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    // inside on failure method we are
                                    // displaying a failure message.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to update the status of this post..", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ArrayList<String> issuesclamied= (ArrayList<String>) current_user[0].get("issue_claims");
                                ArrayList<String> issuesresolved= (ArrayList<String>) current_user[0].get("issue_resolved");
                                issuesclamied.remove(fc.id);
                                issuesresolved.add(fc.id);
                                current_user[0].replace("issue_claims",issuesclamied);
                                current_user[0].replace("issue_resolved",issuesresolved);
                                db.collection("users").document(user.getEmail()).set(current_user[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // on successful completion of this process
                                        // we are displaying the toast message.
                                        Toast.makeText(getActivity(), "You have succesfully resolved this issue..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    // inside on failure method we are
                                    // displaying a failure message.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to remove the issue from issue claim list.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                claimissue.setVisibility(View.GONE);
                                abortissue.setVisibility(View.GONE);
                                issueresolved.setVisibility(View.GONE);
                                claimissue.setClickable(false);
                                issueresolved.setClickable(false);
                                abortissue.setClickable(false);
                            }
                        });
                        abortissue.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {
                                current_issue[0].replace("Status","Unclaimed");
                                db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // on successful completion of this process
                                        // we are displaying the toast message.
                                        Toast.makeText(getActivity(), "You have updated the status of this issue..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    // inside on failure method we are
                                    // displaying a failure message.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to update the status..", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ArrayList<String> issuesclamied= (ArrayList<String>) current_user[0].get("issue_claims");
                                issuesclamied.remove(fc.id);
                                current_user[0].replace("issue_claims",issuesclamied);
                                db.collection("users").document(user.getEmail()).set(current_user[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // on successful completion of this process
                                        // we are displaying the toast message.
                                        Toast.makeText(getActivity(), "You have removed the issue from your claims..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    // inside on failure method we are
                                    // displaying a failure message.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to remove the issue claim list.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                issueresolved.setVisibility(View.GONE);
                                abortissue.setVisibility(View.GONE);
                                claimissue.setVisibility(View.VISIBLE);
                                claimissue.setClickable(true);
                                issueresolved.setClickable(false);
                                abortissue.setClickable(false);
                            }
                        });


                        numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                        final boolean[] already_liked = {false};
                        likeButton.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {

                                ArrayList<String> current_likers= (ArrayList<String>) current_issue[0].get("likers");
                                if (current_likers.contains(user.getEmail())){
                                    already_liked[0]=true;
                                }

                                if (!already_liked[0]){

                                    //adding current id to likers list of this issue
                                    current_likers.add(user.getEmail());
                                    current_issue[0].replace("likers",current_likers);

                                    //likeButton.setBackgroundColor(Color.RED);
                                    Log.i("likes",""+current_issue[0].get("number_of_likes"));
                                    int likenumbers=Integer.parseInt((String) current_issue[0].get("number_of_likes"));
                                    likenumbers++;
                                    fc.likenumbers=""+likenumbers;
                                    current_issue[0].replace("number_of_likes",String.valueOf(likenumbers));
                                    numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                                    Log.i("likes",fc.likenumbers);

                                    db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // on successful completion of this process
                                            // we are displaying the toast message.
                                            Toast.makeText(getActivity(), "You have liked this post..", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        // inside on failure method we are
                                        // displaying a failure message.
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Fail to update the number of likes..", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                else{
                                    already_liked[0]=false;
                                    //likeButton.setBackgroundColor(Color.GREEN);
                                    current_likers.remove(user.getEmail());
                                    current_issue[0].replace("likers",current_likers);

                                    //likeButton.setBackgroundColor(Color.RED);
                                    Log.i("likes",""+current_issue[0].get("number_of_likes"));
                                    int likenumbers=Integer.parseInt((String) current_issue[0].get("number_of_likes"));
                                    likenumbers--;
                                    fc.likenumbers=""+likenumbers;
                                    current_issue[0].replace("number_of_likes",String.valueOf(likenumbers));
                                    numberoflikes.setText(""+current_issue[0].get("number_of_likes"));
                                    Log.i("likes",fc.likenumbers);

                                    db.collection("Issue_detail").document(fc.id).set(current_issue[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // on successful completion of this process
                                            // we are displaying the toast message.
                                            Toast.makeText(getActivity(), "You  succesfully unliked the post", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        // inside on failure method we are
                                        // displaying a failure message.
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Fail to update the number of likes..", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //Toast.makeText(getActivity(),"You have already liked",Toast.LENGTH_LONG).show();
                                }

                            }
                        });



                        //

                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });



        return  v;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.i("Latitude: ", String.valueOf(latitude));
        Log.i("Longitude: ", String.valueOf(longitude));
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}