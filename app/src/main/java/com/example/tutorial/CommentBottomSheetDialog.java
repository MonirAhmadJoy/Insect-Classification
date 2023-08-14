package com.example.tutorial;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class CommentBottomSheetDialog extends BottomSheetDialogFragment {

//    private BackButtonListener backButtonListener;
//
//    public interface BackButtonListener {
//        void onBackButtonPressed();
//    }
    private static final String ARG_POST_ID = "post_id";

    ArrayList<String> cDates;
    ArrayList<String> cList;
    ArrayList<String> uNames;
    ArrayList<Integer> cIds;
    ArrayList<String> uImages;

    DatabaseReference databaseReference, databaseReference1, databaseReference2;
    private FirebaseAuth mAuth;

    private String uid, postId;
    ListView listView;
    EditText comment;
    private TextView numComments;
    private ImageButton send;
    static ArrayList<String> Names;
    HashMap<String, String> usercollection;
    HashMap<String, String> usercollectionImg;

    public static CommentBottomSheetDialog newInstance(String postId) {
        CommentBottomSheetDialog fragment = new CommentBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_comment, container, false);

        numComments = view.findViewById(R.id.numComments);
        send = view.findViewById(R.id.send);
        comment = view.findViewById(R.id.comment);
        listView = view.findViewById(R.id.listView);
        // Retrieve the post ID from the arguments
        postId = getArguments().getString(ARG_POST_ID);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("Commnets");

        getcommentnum();
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
                                Toast.makeText(getContext(), "Thanks for your comment", Toast.LENGTH_SHORT).show();
//                                getValueshere();
                                getcommentnum();
//                                getcomment();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Update failed
                                Toast.makeText(getContext(), "Failed to update fields", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
            boolean isRootFragment = getActivity().getSupportFragmentManager().getBackStackEntryCount() == 0;

            if (!isRootFragment) {
                Toast.makeText(getContext(), "Data not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public void onDismiss(@NonNull DialogInterface dialog) {
//        super.onDismiss(dialog);
//
//        if (backButtonListener != null) {
//            backButtonListener.onBackButtonPressed();
//        }
//    }
//    @Override
//    public void onDismiss(@NonNull DialogInterface dialog) {
//        super.onDismiss(dialog);
//
//
//        // Start the TryMap activity
////        Intent intent = new Intent(getActivity(), Trymap.class);
////        startActivity(intent);
//    }


    public void getValueshere() {
//        if (!isTaskRoot()) {
//            Toast.makeText(getApplicationContext(), "Data not found1", Toast.LENGTH_SHORT).show();
//            return;
//        }
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
                        continue; // Skip these keys and proceed to the next iteration
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

//                Toast.makeText(getContext(), "exists "+cDates.size(), Toast.LENGTH_LONG).show();

                if (userIds.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Data not found1", Toast.LENGTH_SHORT).show();

                    // Check if the current activity is CommentSection

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
                                        uNames.add(String.valueOf(dataSnapshot.child("name").getValue()));
                                        uImages.add(String.valueOf(dataSnapshot.child("pimg").getValue()));
//                                        Toast.makeText(getApplicationContext(), "exists " + cDates.size(), Toast.LENGTH_LONG).show();
                                        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

                                        for (int i = 0; i < cDates.size(); i++) {
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("Date", cDates.get(i));
                                            map.put("Name", uNames.get(i));
                                            // map.put("Image", cImages.get(i));
                                            map.put("Comment", cList.get(i));
                                            list.add(map);
                                        }

                                        String commentsnumstr = Integer.toString(cList.size());
                                        numComments.setText(commentsnumstr + " comments");
                                        String[] from = {"Date", "Name", "Comment"};
                                        int to[] = {R.id.textView, R.id.textName, R.id.textView1};
                                        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list, R.layout.listv_row_items, from, to);
                                        listView.setAdapter(simpleAdapter);
                                        //nouser.setText(Names.get(0));

                                    } else {
                                        Toast.makeText(getContext(), "No data exists", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "No data exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error condition
            }
        });

    }

    public void getcommentnum() {
//        if (isTaskRoot()) {
//            Toast.makeText(getApplicationContext(), "Data not found1", Toast.LENGTH_SHORT).show();
//            return;
//        }
        getValueshere();
        databaseReference.child("Post_User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postUserSnapshot : dataSnapshot.getChildren()) {
                    String postidl = postUserSnapshot.getKey();
                    if (!postidl.equalsIgnoreCase(postId)) {
                        continue;
                    }
                    int cnt = 0;

                    for (DataSnapshot userSnapshot : postUserSnapshot.getChildren()) {
                        if (userSnapshot.getKey().equals("datetime") || userSnapshot.getKey().equals("post_image") ||
                                userSnapshot.getKey().equals("result") || userSnapshot.getKey().equals("userid")) {
                            continue;
                        }

                        String comment = userSnapshot.child("comment").getValue(String.class);
                        if (!comment.trim().isEmpty()) {
                            cnt++;
                        }
                    }

                    numComments.setText(String.valueOf(cnt) + " comments");


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
                // ...
            }
        });
        return;
    }

}

