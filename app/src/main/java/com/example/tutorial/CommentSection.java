package com.example.tutorial;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommentSection extends AppCompatActivity {
    ArrayList<String> cDates;
    ArrayList<String> cList;
    ArrayList<String> uNames;
    ArrayList<Integer> cIds;
    ArrayList<String> uImages;

    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private String uid, postId;
    ListView listView;
    EditText comment;
    private TextView numComments;
    private ImageButton send;
    static ArrayList<String> Names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_section);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postId = bundle.getString("Key");
        }
        numComments = findViewById(R.id.numComments);
        send = findViewById(R.id.send);
        comment = findViewById(R.id.comment);
        listView = findViewById(R.id.listView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getValueshere();
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
                Map<String, Object> updates = new HashMap<>();
                updates.put("comment", comments);
                updates.put("commenttime", datetime);
                databaseReference.child("Post_User").child(postId).child(uid).updateChildren(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Thanks for your comment", Toast.LENGTH_SHORT).show();
                                getValueshere();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Update failed
                                Toast.makeText(getApplicationContext(), "Failed to update fields", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    public void getValueshere() {
        cDates = new ArrayList<>();
        cList = new ArrayList<>();
        uNames = new ArrayList<>();
        uImages = new ArrayList<>();
        cIds = new ArrayList<>();
        databaseReference.child("Post_User").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals("datetime") || dataSnapshot.getKey().equals("post_image")
                            || dataSnapshot.getKey().equals("result") || dataSnapshot.getKey().equals("userid")) {
                        continue;
                    }

                    String userId = dataSnapshot.getKey();
                    String comment = dataSnapshot.child("comment").getValue(String.class);
                    String commenttime = dataSnapshot.child("commenttime").getValue(String.class);
                    if (!comment.trim().isEmpty()) {
                        userIds.add(userId);
                        cList.add(comment);
                        cDates.add(commenttime);
                    }
                }
                String commentsnumstr = Integer.toString(cList.size());
                numComments.setText(commentsnumstr + " comments");
                if (userIds.isEmpty()) {

                    if (isTaskRoot()) {
                        Toast.makeText(getApplicationContext(), "Data not found1", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    for (int i = 0; i < userIds.size(); i++) {
                        String uuid = userIds.get(i);
                        databaseReference.child("NEWUSER").child(uuid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        DataSnapshot dataSnapshot = task.getResult();
                                        uNames.add(dataSnapshot.child("name").getValue(String.class));

                                        if (uNames.size() == userIds.size()) {
                                            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
                                            for (int j = 0; j < cDates.size(); j++) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("Date", cDates.get(j));
                                                map.put("Name", uNames.get(j));
                                                map.put("Comment", cList.get(j));
                                                list.add(map);
                                            }
                                            String[] from = {"Date", "Name", "Comment"};
                                            int to[] = {R.id.textView, R.id.textName, R.id.textView1};
                                            SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.listv_row_items, from, to);
                                            listView.setAdapter(simpleAdapter);
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No data exists", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "No data exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}