package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.tutorial.databinding.ActivityLikeBinding;
import com.example.tutorial.databinding.ActivityMainBinding;
import com.example.tutorial.databinding.ActivityTrymapBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Trymap extends DrawerBase {
    ActivityTrymapBinding activityTrymapBinding;
    DatabaseReference databaseReference, databaseReference1;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private ProgressBar progressBar;

    private static final int REQUEST_CODE_SECOND_ACTIVITY = 1;
    Button btnlogout;
    ImageView PostImage, UserImage;
    TextView textView, prev, next, second, third, fourth, mytext, UserName, PostDate, PostText, likenum, dislikenum, ratenum, avgrat;
    private ImageButton likeBtn, dislikeBtn, send,numComments;
    private RatingBar ratingBar, ratingBarAvg;
    private FirebaseAuth mAuth;
    private String uid;
    int currpage = 1, maxpage = 2;

    static ArrayList<String> Names,postids,sttext, userids, userimages, names, dates, texts, postimages, likes, dislikes, ratings, avgratings, numcomments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTrymapBinding = ActivityTrymapBinding.inflate(getLayoutInflater());
        setContentView(activityTrymapBinding.getRoot());
        allocateActivityTitle("Posts");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
//        mytext = findViewById(R.id.mytext);
        UserImage = findViewById(R.id.user_image);
        PostImage = findViewById(R.id.post_image);
        UserName = findViewById(R.id.user_name);
        PostDate = findViewById(R.id.post_date);
        PostText = findViewById(R.id.post_text);
        likenum = findViewById(R.id.likenum);
        dislikenum = findViewById(R.id.dislikenum);
        likeBtn = findViewById(R.id.likeBtn);
        dislikeBtn = findViewById(R.id.dislikeBtn);
        ratingBar = findViewById(R.id.ratingBar);
        avgrat = findViewById(R.id.avgrat);
        numComments = findViewById(R.id.numComments);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();


        getValues();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secondstr = second.getText().toString();
                String thirdstr = third.getText().toString();
                String fourthstr = fourth.getText().toString();
                int secondint = Integer.parseInt(String.valueOf(secondstr));
                int thirdint = Integer.parseInt(String.valueOf(thirdstr));
                int fourthint = Integer.parseInt(String.valueOf(fourthstr));
                secondint++;
                thirdint++;
                fourthint++;
                if (fourthint > maxpage) {
                    secondint--;
                    thirdint--;
                    fourthint--;
                }
                String secondstr1 = Integer.toString(secondint);
                String thirdstr1 = Integer.toString(thirdint);
                String fourthstr1 = Integer.toString(fourthint);
                second.setText(secondstr1);
                third.setText(thirdstr1);
                fourth.setText(fourthstr1);
                currpage++;
                if (currpage > maxpage)
                    currpage = maxpage;

                setparameters(currpage - 1);

//                mytext.setText(sttext.get(currpage-1));
                //mytext.setText(sttext[currpage-1]);
                setcolor();
                getbtnStatus(currpage - 1);
                getRating(currpage - 1);


            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secondstr = second.getText().toString();
                String thirdstr = third.getText().toString();
                String fourthstr = fourth.getText().toString();
                int secondint = Integer.parseInt(String.valueOf(secondstr));
                int thirdint = Integer.parseInt(String.valueOf(thirdstr));
                int fourthint = Integer.parseInt(String.valueOf(fourthstr));
                secondint--;
                thirdint--;
                fourthint--;
                if (secondint < 1) {
                    secondint++;
                    thirdint++;
                    fourthint++;
                }
                String secondstr1 = Integer.toString(secondint);
                String thirdstr1 = Integer.toString(thirdint);
                String fourthstr1 = Integer.toString(fourthint);
                second.setText(secondstr1);
                third.setText(thirdstr1);
                fourth.setText(fourthstr1);
                currpage--;
                if (currpage < 1)
                    currpage = 1;

                setparameters(currpage - 1);
//                mytext.setText(sttext.get(currpage-1));
                //mytext.setText(sttext[currpage-1]);
                setcolor();
                getbtnStatus(currpage - 1);
                getRating(currpage - 1);

            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secondstr = second.getText().toString();
                int secondint = Integer.parseInt(String.valueOf(secondstr));
                currpage = secondint;
                if (currpage > maxpage) {
                    currpage = maxpage;
                    Toast.makeText(getApplicationContext(), "No Page here", Toast.LENGTH_SHORT).show();
                }
                setparameters(currpage - 1);
//                mytext.setText(sttext.get(currpage-1));
                // mytext.setText(sttext[currpage-1]);
                setcolor();
                getbtnStatus(currpage - 1);
                getRating(currpage - 1);

            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String thirdstr = third.getText().toString();
                int thirdint = Integer.parseInt(String.valueOf(thirdstr));
                currpage = thirdint;
                if (currpage > maxpage) {
                    currpage = maxpage;
                    Toast.makeText(getApplicationContext(), "No Page here", Toast.LENGTH_SHORT).show();
                }

                setparameters(currpage - 1);
//                mytext.setText(sttext.get(currpage-1));
                // mytext.setText(sttext[currpage-1]);
                setcolor();
                getbtnStatus(currpage - 1);
                getRating(currpage - 1);

            }
        });

        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fourthstr = fourth.getText().toString();
                int fourthint = Integer.parseInt(String.valueOf(fourthstr));
                currpage = fourthint;
                if (currpage > maxpage) {
                    currpage = maxpage;
                    Toast.makeText(getApplicationContext(), "No Page here", Toast.LENGTH_SHORT).show();
                }
                setparameters(currpage - 1);
//                mytext.setText(sttext.get(currpage-1));
                //mytext.setText(sttext[currpage-1]);
                setcolor();
                getbtnStatus(currpage - 1);
                getRating(currpage - 1);

            }
        });

        numComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String postId = postids.get(currpage-1); // Replace with the actual post ID
//                CommentBottomSheetDialog bottomSheetDialog = CommentBottomSheetDialog.newInstance(postId);
//                bottomSheetDialog.show(getSupportFragmentManager(), "commentBottomSheet");

                String postId = postids.get(currpage-1); // Replace with the actual post ID
                Intent intent = new Intent(Trymap.this, CommentSection.class);
                Bundle bundle = new Bundle();
                bundle.putString("Key", postId);
                intent.putExtras(bundle);
//                startActivityForResult(intent, 1);
                startActivity(intent);
//                finish();
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                                String like=String.valueOf(dataSnapshot.child("like").getValue());
                                String dislike=String.valueOf(dataSnapshot.child("dislike").getValue());
                                if(like.equalsIgnoreCase("0")&&dislike.equalsIgnoreCase("0")){
                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("like").setValue("1");
                                    likeBtn.setImageResource(R.drawable.like_fill);
                                    getTotalStatus(currpage-1);
                                }

                                else if(like.equalsIgnoreCase("0")&&dislike.equalsIgnoreCase("1")){
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("dislike", "0");
                                    updates.put("like", "1");
                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).updateChildren(updates);


//                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("like").setValue("1");
//                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("dislike").setValue("0");
                                    likeBtn.setImageResource(R.drawable.like_fill);
                                    dislikeBtn.setImageResource(R.drawable.dislike_nfill);
                                    getTotalStatus(currpage-1);
                                }
                                else {
                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("like").setValue("0");
                                    likeBtn.setImageResource(R.drawable.like_nfill);
                                    getTotalStatus(currpage-1);
                                }
                            }
                            else {
                                Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                                String like=String.valueOf(dataSnapshot.child("like").getValue());
                                String dislike=String.valueOf(dataSnapshot.child("dislike").getValue());
                                if(like.equalsIgnoreCase("0")&&dislike.equalsIgnoreCase("0")){
                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("dislike").setValue("1");
                                    dislikeBtn.setImageResource(R.drawable.dislike_fill);
                                    getTotalStatus(currpage-1);
                                }

                                else if(like.equalsIgnoreCase("1")&&dislike.equalsIgnoreCase("0")){
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("dislike", "1");
                                    updates.put("like", "0");
                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).updateChildren(updates);


//                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("dislike").setValue("1");
//                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("like").setValue("0");
                                    dislikeBtn.setImageResource(R.drawable.dislike_fill);
                                    likeBtn.setImageResource(R.drawable.like_nfill);
                                    getTotalStatus(currpage-1);
                                }
                                else {
                                    databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("dislike").setValue("0");
                                    dislikeBtn.setImageResource(R.drawable.dislike_nfill);
                                    getTotalStatus(currpage-1);
                                }
                            }
                            else {
                                Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int val=(int)v;
                String lnumstr = Integer.toString(val);
                databaseReference.child("Post_User").child(postids.get(currpage-1)).child(uid).child("rating").setValue(lnumstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            getTotalStatus(currpage-1);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),  "User is Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Retrieve the value from the second activity
                String valueFromSecondActivity = data.getStringExtra("valueFromSecondActivity");
                Toast.makeText(Trymap.this, "Data "+valueFromSecondActivity, Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void getValues() {
        userids = new ArrayList<>();
        texts = new ArrayList<>();
        postimages = new ArrayList<>();
        likes = new ArrayList<>();
        dislikes = new ArrayList<>();
        numcomments = new ArrayList<>();
        ratings = new ArrayList<>();
        dates = new ArrayList<>();
        names = new ArrayList<>();
        userimages = new ArrayList<>();
        postids=new ArrayList<>();


        databaseReference.child("Post_User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postUserSnapshot : dataSnapshot.getChildren()) {
                    String postId = postUserSnapshot.getKey();
                    String datetime = postUserSnapshot.child("datetime").getValue(String.class);
                    String postImage = postUserSnapshot.child("post_image").getValue(String.class);
                    String result = postUserSnapshot.child("result").getValue(String.class);
                    String userId = postUserSnapshot.child("userid").getValue(String.class);

                    dates.add(datetime);
                    postimages.add(postImage);
                    texts.add(result);
                    userids.add(userId);
                    postids.add(postId);

                    // Fetch additional fields like "like", "dislike", "comment", and "rating"
                    int likeSum = 0;
                    int dislikeSum = 0;
                    int ratingSum = 0;
                    int cnt = 0;

                    for (DataSnapshot userSnapshot : postUserSnapshot.getChildren()) {
                        if (userSnapshot.getKey().equals("datetime") || userSnapshot.getKey().equals("post_image") ||
                                userSnapshot.getKey().equals("result") || userSnapshot.getKey().equals("userid")) {
                            continue;
                        }

                        String like = userSnapshot.child("like").getValue(String.class);
                        String dislike = userSnapshot.child("dislike").getValue(String.class);
                        String rating = userSnapshot.child("rating").getValue(String.class);

                        likeSum += Integer.parseInt(like);
                        dislikeSum += Integer.parseInt(dislike);
                        ratingSum += Integer.parseInt(rating);
                        cnt++;
                    }

                    // Calculate average rating
                    float avgRating = (float) ratingSum / cnt;

                    // Add the calculated values to their respective arrays
                    likes.add(String.valueOf(likeSum));
                    dislikes.add(String.valueOf(dislikeSum));
                    ratings.add(String.valueOf(avgRating));
                }

                if (dates.isEmpty()) {
                    Toast.makeText(Trymap.this, "Data not found", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < userids.size(); i++) {
                        String uuid = userids.get(i);
                        databaseReference.child("NEWUSER").child(uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        DataSnapshot dataSnapshot = task.getResult();
                                        names.add(String.valueOf(dataSnapshot.child("name").getValue()));
                                        userimages.add(String.valueOf(dataSnapshot.child("pimg").getValue()));
                                        maxpage = texts.size();
                                        setcolor();
                                        setparameters(0);
                                        getbtnStatus(0);
                                        getRating(0);
                                    } else {
                                        Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                // Use the fetched data here or pass it to another method
                // ...

                // You can access the arrays here and do further processing

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                // ...
            }
        });

    }

    public void getbtnStatus(int i) {
        databaseReference.child("Post_User").child(postids.get(i)).child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getRating(int i){
        databaseReference.child("Post_User").child(postids.get(i)).child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                        String rate=String.valueOf(dataSnapshot.child("rating").getValue());
                        int rnum = Integer.parseInt(String.valueOf(rate));
                        ratingBar.setRating((float)rnum);
                    }
                    else {
                        Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Trymap.this, "No data exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getTotalStatus(int i) {
        databaseReference.child("Post_User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postUserSnapshot : dataSnapshot.getChildren()) {
                    String postId = postUserSnapshot.getKey();
                    if(!postId.equalsIgnoreCase(postids.get(i))){
                        continue;
                    }
                    int likeSum = 0;
                    int dislikeSum = 0;
                    int ratingSum = 0;
                    int cnt = 0;

                    for (DataSnapshot userSnapshot : postUserSnapshot.getChildren()) {
                        if (userSnapshot.getKey().equals("datetime") || userSnapshot.getKey().equals("post_image") ||
                                userSnapshot.getKey().equals("result") || userSnapshot.getKey().equals("userid")) {
                            continue;
                        }

                        String like = userSnapshot.child("like").getValue(String.class);
                        String dislike = userSnapshot.child("dislike").getValue(String.class);
                        String rating = userSnapshot.child("rating").getValue(String.class);

                        likeSum += Integer.parseInt(like);
                        dislikeSum += Integer.parseInt(dislike);
                        ratingSum += Integer.parseInt(rating);
                        cnt++;
                    }

                    // Calculate average rating
                    float avgRating = (float) ratingSum / cnt;

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String formattedAvgRating = decimalFormat.format(avgRating);

                    float roundedAvgRating = Float.parseFloat(formattedAvgRating);

                    likenum.setText(String.valueOf(likeSum));
                    dislikenum.setText(String.valueOf(dislikeSum));
                    avgrat.setText(String.valueOf(roundedAvgRating));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                // ...
            }
        });
    }
    public void setcolor() {
        String secondstr = second.getText().toString();
        String thirdstr = third.getText().toString();
        String fourthstr = fourth.getText().toString();
        int secondint = Integer.parseInt(String.valueOf(secondstr));
        int thirdint = Integer.parseInt(String.valueOf(thirdstr));
        int fourthint = Integer.parseInt(String.valueOf(fourthstr));
        if (currpage == secondint) {
            second.setTextColor(Color.parseColor("#3DDC84"));
            third.setTextColor(Color.parseColor("#9C27B0"));
            fourth.setTextColor(Color.parseColor("#9C27B0"));
        } else if (currpage == thirdint) {
            third.setTextColor(Color.parseColor("#3DDC84"));
            second.setTextColor(Color.parseColor("#9C27B0"));
            fourth.setTextColor(Color.parseColor("#9C27B0"));
        } else {
            fourth.setTextColor(Color.parseColor("#3DDC84"));
            third.setTextColor(Color.parseColor("#9C27B0"));
            second.setTextColor(Color.parseColor("#9C27B0"));

        }
    }

    public void setparameters(int i) {
        Picasso.get().load(userimages.get(i)).transform(new CircleTransformation()).into(UserImage);
        UserName.setText(names.get(i));
        PostDate.setText(dates.get(i));

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString coloredText = new SpannableString(texts.get(i));
        coloredText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, coloredText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        coloredText.setSpan(new StyleSpan(Typeface.BOLD), 0, coloredText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("Hi, I have tested this application to predict my desired input. I think it worked well as I got a better result for the following image with a result of \n")
                .append(coloredText);
        PostText.setText(builder);

        Picasso.get().load(postimages.get(i)).into(PostImage);
        likenum.setText(likes.get(i));
        dislikenum.setText(dislikes.get(i));

        avgrat.setText(ratings.get(i));
    }

}
