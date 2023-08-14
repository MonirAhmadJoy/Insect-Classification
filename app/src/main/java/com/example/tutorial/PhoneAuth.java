package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneAuth extends AppCompatActivity {
    AlertDialog.Builder builder;
    Button signIn, btnSpn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference, databaseReference1, databaseReference2;
    private String name, email, password;
    String url = "http://192.168.0.104/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        email = bundle.getString("email");
        password = bundle.getString("password");
//        signIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.ProgressBarID);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("NEWUSER");
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        builder = new AlertDialog.Builder(this);
//        signIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

        List providers = Collections.singletonList(
                new AuthUI.IdpConfig.PhoneBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mAuth.signOut();
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
//                            finish();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            databaseReference.child(uid).child("flag").setValue("1");
                            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                            String datetime = df.format(Calendar.getInstance().getTime());
                            Post_User data = new Post_User("0", "0", "0", " ", datetime);

                            databaseReference1.child("Post_User").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int i=0;
                                    for (DataSnapshot postUserSnapshot : dataSnapshot.getChildren()) {
                                        String subnodeKey = postUserSnapshot.getKey();
                                        i++;

                                        databaseReference1.child("Post_User").child(subnodeKey).child(uid).setValue(data);
                                    }
//                                    Toast.makeText(YoutubeVideo.this, "nums: "+i, Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle potential errors here
                                    // ...
                                }
                            });

                            builder.setMessage("Do you want to register into our Flower Shop website?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            processdata(name.toString(), email.toString(), password.toString());

//
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //  Action for 'NO' Button
                                            dialog.cancel();
                                            finish();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(PhoneAuth.this, "Login Successful but you are not connected to our Flower Shop", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            //Creating dialog box
                            AlertDialog alert = builder.create();
                            //Setting the title manually
                            alert.setTitle("With Flower Shop");
                            alert.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login is Unsuccessfull", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(PhoneAuth.this, "Phone verification process was cancelled", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void processdata(String name, String email, String password) {
        Call<responsemodel> call = apiController.getInstance()
                .getapi()
                .getregister(name, email, password);

        call.enqueue(new Callback<responsemodel>() {
            @Override
            public void onResponse(Call<responsemodel> call, Response<responsemodel> response) {
                responsemodel obj = response.body();
                finish();
                Toast.makeText(PhoneAuth.this, "Login Successful and Thanks for connecting with our Flower Shop", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<responsemodel> call, Throwable t) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(PhoneAuth.this, "Something went error to register into Flower Shop", Toast.LENGTH_LONG).show();
            }
        });

    }
}