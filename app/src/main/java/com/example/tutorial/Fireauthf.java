package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial.databinding.ActivityFireauth1Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fireauthf extends AppCompatActivity implements View.OnClickListener {
    private EditText signInEmailEditText, SignInPasswordEditText, editText;
    private TextView signUpTextView, nouser, resetpass;
    private Button signInButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference, databaseReference1, databaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fireauth1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");

        mAuth = FirebaseAuth.getInstance();
        signInEmailEditText = findViewById(R.id.SignInEmailEditTextID);
        SignInPasswordEditText = findViewById(R.id.SignInPasswordEditTextID);
        signInButton = findViewById(R.id.signInButtonID);
        signUpTextView = findViewById(R.id.SignUpTextViewID);
        resetpass = findViewById(R.id.reset);
        progressBar = findViewById(R.id.ProgressBarID);
        databaseReference = FirebaseDatabase.getInstance().getReference("NEWUSER");
        resetpass.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mAuth.signOut();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButtonID:
                userlogin();
                //Toast.makeText(getApplicationContext(),  "Login is Unsuccessfull", Toast.LENGTH_SHORT).show();
                break;

            case R.id.SignUpTextViewID:
                finish();
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                break;

            case R.id.reset:
                finish();
                intent = new Intent(getApplicationContext(), ResetPass.class);
                startActivity(intent);
                break;
        }

    }

    private void userlogin() {
        String email = signInEmailEditText.getText().toString().trim();
        String password = SignInPasswordEditText.getText().toString().trim();
        if (email.isEmpty()) {
            signInEmailEditText.setError("Enter an email address");
            signInEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signInEmailEditText.setError("Enter a valid email address");
            signInEmailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            SignInPasswordEditText.setError("Enter a password");
            SignInPasswordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            SignInPasswordEditText.setError("Minimum length of password should be six");
            SignInPasswordEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    verifyEmail(email, password);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Login is Unsuccessfull", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyEmail(String email, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {
            checkphone(email, password);
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please check gmail to verify your email", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

    public void checkphone(String email, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String flag = String.valueOf(dataSnapshot.child("flag").getValue());
                        if (flag.equalsIgnoreCase("0")) {
                            finish();
                            progressBar.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putString("email", email);
                            bundle.putString("password", password);
                            Intent intent = new Intent(getApplicationContext(), PhoneAuth.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            Toast.makeText(Fireauthf.this, "Phone number is not verified yet. Verify here", Toast.LENGTH_LONG).show();
                        } else {
                            finish();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(Fireauthf.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Fireauthf.this, "No data exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Fireauthf.this, "No data exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}