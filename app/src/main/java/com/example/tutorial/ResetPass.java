package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPass extends AppCompatActivity {
    EditText resetMail;
    Button resetSubmit;
    ProgressBar resetProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        resetMail=findViewById(R.id.resetMail);
        resetSubmit=findViewById(R.id.restSubmit);
        resetProgress = findViewById(R.id.Progressreset);
        mAuth = FirebaseAuth.getInstance();

        resetSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpassword();

            }
        });
    }

    private void resetpassword() {
        String email=resetMail.getText().toString().trim();
        if(email.isEmpty())
        {
            resetMail.setError("Enter an email address");
            resetMail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            resetMail.setError("Enter a valid email address");
            resetMail.requestFocus();
            return;
        }
        resetProgress.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),  "Please check your mail", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),  "Wrong, Try again", Toast.LENGTH_LONG).show();
                }
                resetProgress.setVisibility(View.GONE);
            }
        });
    }
}