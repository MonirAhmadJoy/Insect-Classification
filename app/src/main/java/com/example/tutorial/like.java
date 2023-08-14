package com.example.tutorial;

import static java.lang.Math.max;
import static java.lang.Math.min;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial.databinding.ActivityLikeBinding;
import com.example.tutorial.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class like extends DrawerBase{
    ActivityLikeBinding activityLikeBinding;
    //drawer  https://www.bdtopcoder.xyz/2022/07/android-navigation-drawer-custom.html

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private TextView likenum,dislikenum,ratenum,numComments,avgrat;
    private ImageButton likeBtn,dislikeBtn,send;
    private RatingBar ratingBar,ratingBarAvg;
    EditText comment;

    ListView listView;
    SearchView searchView;
    ArrayList<String> cDates;
    ArrayList<String> cList;
    ArrayList<String> uNames;
    ArrayList<Integer> cIds;

    DatabaseReference databaseReference,databaseReference1,databaseReference2;
    private FirebaseAuth mAuth;

    private String uid;

    static ArrayList<String>Names;
    HashMap<String,String> usercollection;
    HashMap<String,String> usercollectionImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLikeBinding=ActivityLikeBinding.inflate(getLayoutInflater());
        setContentView(activityLikeBinding.getRoot());
        //setContentView(R.layout.activity_main);
        allocateActivityTitle("Feedback");

        likenum=findViewById(R.id.likenum);
        dislikenum=findViewById(R.id.dislikenum);
        likeBtn=findViewById(R.id.likeBtn);
        dislikeBtn=findViewById(R.id.dislikeBtn);
        ratingBar=findViewById(R.id.ratingBar);
        avgrat=findViewById(R.id.avgrat);
//        ratenum=findViewById(R.id.ratenum);
        numComments=findViewById(R.id.numComments);
        send=findViewById(R.id.send);
        comment=findViewById(R.id.comment);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("NEWUSER");
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Commnets");
        getTotalStatus();
        getbtnStatus();
        getRating();
        getcommentnum();


        numComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getcomment();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = comment.getText().toString().trim();
                comment.setText("");
                if (comments.isEmpty()) {
                    comment.setError("Please write something");
                    comment.requestFocus();
                    return;//Toast.makeText(SignUp.this, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                }
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String datetime = df.format(Calendar.getInstance().getTime());
                Usercom usercom=new Usercom(uid,comments,datetime);
                databaseReference1.child("Commnets").child(uid).setValue(usercom)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),  "Thanks for your comment", Toast.LENGTH_SHORT).show();
                                    getcomment();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),  "Comment is Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                                String like=String.valueOf(dataSnapshot.child("like").getValue());
                                String dislike=String.valueOf(dataSnapshot.child("dislike").getValue());
                                if(like.equalsIgnoreCase("0")&&dislike.equalsIgnoreCase("0")){
                                    databaseReference.child(uid).child("like").setValue("1");
                                    likeBtn.setImageResource(R.drawable.like_fill);
                                    getTotalStatus();
                                }

                                else if(like.equalsIgnoreCase("0")&&dislike.equalsIgnoreCase("1")){
                                    databaseReference.child(uid).child("like").setValue("1");
                                    databaseReference.child(uid).child("dislike").setValue("0");
                                    likeBtn.setImageResource(R.drawable.like_fill);
                                    dislikeBtn.setImageResource(R.drawable.dislike_nfill);
                                    getTotalStatus();
                                }
                                else {
                                    databaseReference.child(uid).child("like").setValue("0");
                                    likeBtn.setImageResource(R.drawable.like_nfill);
                                    getTotalStatus();
                                }
                            }
                            else {
                                Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//
//                String st=likenum.getText().toString().trim();
//                int lnum=Integer.parseInt(String.valueOf(st));
//                lnum++;
//                String lnumstr=Integer.toString(lnum);
//                dislikenum.setText(lnumstr);

            }
        });



        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                                String like=String.valueOf(dataSnapshot.child("like").getValue());
                                String dislike=String.valueOf(dataSnapshot.child("dislike").getValue());
                                if(like.equalsIgnoreCase("0")&&dislike.equalsIgnoreCase("0")){
                                    databaseReference.child(uid).child("dislike").setValue("1");
                                    dislikeBtn.setImageResource(R.drawable.dislike_fill);
                                    getTotalStatus();
                                }

                                else if(like.equalsIgnoreCase("1")&&dislike.equalsIgnoreCase("0")){
                                    databaseReference.child(uid).child("dislike").setValue("1");
                                    databaseReference.child(uid).child("like").setValue("0");
                                    dislikeBtn.setImageResource(R.drawable.dislike_fill);
                                    likeBtn.setImageResource(R.drawable.like_nfill);
                                    getTotalStatus();
                                }
                                else {
                                    databaseReference.child(uid).child("dislike").setValue("0");
                                    dislikeBtn.setImageResource(R.drawable.dislike_nfill);
                                    getTotalStatus();
                                }
                            }
                            else {
                                Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//
//                String st=likenum.getText().toString().trim();
//                int lnum=Integer.parseInt(String.valueOf(st));
//                lnum++;
//                String lnumstr=Integer.toString(lnum);
//                dislikenum.setText(lnumstr);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int val=(int)v;
                String lnumstr = Integer.toString(val);
                databaseReference.child(uid).child("rate").setValue(lnumstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            getTotalStatus();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),  "User is Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    private void getValueshere() {
        usercollection=new HashMap<String,String>();
        usercollectionImg=new HashMap<String,String>();
        databaseReference.orderByChild("Name").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Map<String,Object> users = (Map<String,Object>)dataSnapshot.getValue();
                    String userid=users.get("userid").toString();
                    usercollection.put(userid,users.get("name").toString());
                    usercollectionImg.put(userid,users.get("name").toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //nouser.setText(Names.get(0));
    }

    public void getcommentnum() {
        databaseReference2.orderByChild("datetime").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentsnum = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    commentsnum+=1;
                }
                String commentsnumstr= Integer.toString(commentsnum);
                numComments.setText(commentsnumstr+" comments");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getcomment(){
        getValueshere();
        cDates=new ArrayList<>();
        cList=new ArrayList<>();
        uNames=new ArrayList<>();
        cIds=new ArrayList<>();
        databaseReference2.orderByChild("datetime").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentsnum = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    GenericTypeIndicator<Map<String, Object>> to = new GenericTypeIndicator<Map<String, Object>>() {
//                    };
                    Map<String, Object> users = (Map<String, Object>)dataSnapshot.getValue();
                    String ttcomment = users.get("comments").toString();
                    String ttuid = users.get("uid").toString();
                    String ttdatetime = users.get("datetime").toString();
                    commentsnum+=1;
                    cDates.add(ttdatetime);
                    cList.add(ttcomment);
                    //cIds.add( Integer.valueOf(R.drawable.orange));
                    String username3=usercollection.get(ttuid);
                    uNames.add(username3);
                }
                String commentsnumstr= Integer.toString(commentsnum);
//                String commentsnumstr= Integer.toString(usercollection.size());
                numComments.setText(commentsnumstr+" comments");
                listView = findViewById(R.id.listView);
                ArrayList<HashMap<String, Object>> list = new ArrayList<>();

                for (int i = 0; i < cDates.size(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Date", cDates.get(i));
                    map.put("Name", uNames.get(i));
                   // map.put("Image", cIds.get(i));
                    map.put("Comment", cList.get(i));
                    list.add(map);
                }

                String[] from = {"Date", "Name","Comment"};
                int to[] = {R.id.textView,R.id.textName,R.id.textView1};

                SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.listv_row_items, from, to);
                listView.setAdapter(simpleAdapter);
                //nouser.setText(Names.get(0));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getRating(){
        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                        String rate=String.valueOf(dataSnapshot.child("rate").getValue());
                        int rnum = Integer.parseInt(String.valueOf(rate));
                        ratingBar.setRating((float)rnum);
                    }
                    else {
                        Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
//
//    public void getRatingAvg(){
//        databaseReference.orderByChild("Name").addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int likesnum = 0;
//                int dislikesnum = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                    GenericTypeIndicator<Map<String, Object>> to = new GenericTypeIndicator<Map<String, Object>>() {
////                    };
//                    Map<String, Object> users = (Map<String, Object>)dataSnapshot.getValue();
//                    String ttlike = users.get("rate").toString();
//                    int lnum = Integer.parseInt(String.valueOf(ttlike));
//                    likesnum += lnum;
//                    dislikesnum ++;
//                }
//                float avgrat=likesnum/dislikesnum;
//                ratingBarAvg.setRating((float)avgrat);
//                Toast.makeText(like.this, "avg: "+avgrat, Toast.LENGTH_SHORT).show();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void getTotalStatus() {
        databaseReference.orderByChild("Name").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likesnum = 0;
                int dislikesnum = 0;
                int cntrat=0;
                int cnt=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    GenericTypeIndicator<Map<String, Object>> to = new GenericTypeIndicator<Map<String, Object>>() {
//                    };
                    Map<String, Object> users = (Map<String, Object>)dataSnapshot.getValue();
                    String ttlike = users.get("like").toString();
                    String ttdislike = users.get("dislike").toString();
                    String ttrat = users.get("rate").toString();
                    int lnum = Integer.parseInt(String.valueOf(ttlike));
                    int dnum = Integer.parseInt(String.valueOf(ttdislike));
                    int rnum = Integer.parseInt(String.valueOf(ttrat));
                    likesnum += lnum;
                    dislikesnum += dnum;
                    cnt++;
                    cntrat+=rnum;
                }
                String lnumstr = Integer.toString(likesnum);
                String dnumstr = Integer.toString(dislikesnum);
                likenum.setText(lnumstr);
                dislikenum.setText(dnumstr);
                float avgrating=(float)cntrat/cnt;
                String mytext = Float.toString(avgrating);
                avgrat.setText(mytext);
                //nouser.setText(Names.get(0));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        public void getbtnStatus() {
            databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DataSnapshot dataSnapshot = task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                            String like = String.valueOf(dataSnapshot.child("like").getValue());
                            String dislike = String.valueOf(dataSnapshot.child("dislike").getValue());
                            if (like.equalsIgnoreCase("0") && dislike.equalsIgnoreCase("0")) {
                                likeBtn.setImageResource(R.drawable.like_nfill);
                                dislikeBtn.setImageResource(R.drawable.dislike_nfill);
                            } else if (like.equalsIgnoreCase("0") && dislike.equalsIgnoreCase("1")) {
                                likeBtn.setImageResource(R.drawable.like_nfill);
                                dislikeBtn.setImageResource(R.drawable.dislike_fill);
                            } else {
                                likeBtn.setImageResource(R.drawable.like_fill);
                                dislikeBtn.setImageResource(R.drawable.dislike_nfill);
                            }
                        } else {
                            Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(like.this, "No data exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
}