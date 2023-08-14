package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial.databinding.ActivityFireauth1Binding;
import com.example.tutorial.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    //    ActivitySignUpBinding activitySignUpBinding;
    DatabaseReference databaseReference, databaseReference1;
    //ArrayList<HashMap<String, Object>> list;//= new ArrayList<>();

    private Spinner spinner1, spinner2;
    private EditText signUpEmailEditText, SignUpPasswordEditText, editText, textage, datepic;
    private TextView signInTextView, nouser, textView, dtxtv, stxtv;
    ;
    private Button signUpButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    static ArrayList<String> Names, DistrictsArray, SubdistrictsArray;
    ImageView imageMenu;
    private String selectedSpin1, selectedSpin2;
    private ArrayAdapter<String> adapter1, adapter2;

    Calendar calendar;
    //HashMap<String, Object> mapout;


    private android.text.TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // get the content of both the edit text

            int l = s.length();
            boolean test = check(String.valueOf(s));
            if (l == 0) {
                nouser.setText("");
                signUpButton.setEnabled(false);
            } else if (test == true) {
                nouser.setText("Not available");
                signUpButton.setEnabled(false);

            } else {
                nouser.setText("Available");
                signUpButton.setEnabled(true);
            }
            String textInput = editText.getText().toString();
            // check whether both the fields are empty or not
            //submit.setEnabled(!textInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static boolean check(String toCheckValue) {
        boolean test = false;
        for (String element : Names) {
            if (element.equalsIgnoreCase(toCheckValue)) {
                test = true;
                break;
            }
        }
        return test;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference1 = FirebaseDatabase.getInstance().getReference("NEWUSER");

        editText = findViewById(R.id.etValue);
        datepic = findViewById(R.id.etage);
        nouser = findViewById(R.id.tt);
        signUpEmailEditText = findViewById(R.id.SignUpEmailEditTextID);
        SignUpPasswordEditText = findViewById(R.id.SignUpPasswordEditTextID);
        signUpButton = findViewById(R.id.signUpButtonID);
        signInTextView = findViewById(R.id.SignInTextViewID);
        progressBar = findViewById(R.id.ProgressBarID);
        dtxtv = findViewById(R.id.dtxtv);
        stxtv = findViewById(R.id.stxtv);
        spinner1 = (Spinner) findViewById(R.id.spinner1ID);
        spinner2 = (Spinner) findViewById(R.id.spinner2ID);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true); // work offline
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getValues();
        getValuesD();
        editText.addTextChangedListener(textWatcher);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                datepic.setText(dateFormat.format(calendar.getTime()));
            }
        };

        datepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignUp.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DistrictsArray);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedSpin1 = spinner1.getSelectedItem().toString();      //Obtain the selected State

                int parentID = adapterView.getId();
                if (parentID == R.id.spinner1ID) {
                    switch (selectedSpin1) {
                        case "Select Your District": {
                            getValuesS(0);
                            adapter2 = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
                            break;
                        }

                        case "Chittagong": {
                            getValuesS(1);
                            adapter2 = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
                            break;
                        }
                        case "Cox'sBazar": {
                            getValuesS(2);
                            adapter2 = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
                            break;
                        }
                        default: {
                            getValuesS(3);
                            adapter2 = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
                            break;
                        }
                    }

                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(adapter2);
                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedSpin2 = spinner2.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        signUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);

    }

    private void getValues() {
        Names = new ArrayList<>();
        databaseReference.child("NEWUSER").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    GenericTypeIndicator<Map<String, Object>> to = new GenericTypeIndicator<Map<String, Object>>() {};
                    Map<String, Object> users = (Map<String, Object>) dataSnapshot.getValue();
                    String usName = (String) users.get("name");
                    Names.add(usName);
                }
                //nouser.setText(Names.get(0));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //nouser.setText(Names.get(0));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpButtonID:
                userregister();
                break;

            case R.id.SignInTextViewID:
                finish();
                Intent intent = new Intent(getApplicationContext(), Fireauthf.class);
                startActivity(intent);
                break;
        }

    }

    private void userregister() {
        String id = "user" + new Date().getTime();
        String name = editText.getText().toString();
        String age = datepic.getText().toString().trim();
        String email = signUpEmailEditText.getText().toString().trim();
        String password = SignUpPasswordEditText.getText().toString().trim();
        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", name);

        if (name.isEmpty()) {
            editText.setError("Please Enter Username");
            editText.requestFocus();
            //Toast.makeText(SignUp.this, "Please Enter All data...", Toast.LENGTH_SHORT).show();
        }

        //checking the validity of the email
        else if (email.isEmpty()) {
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        } else if (password.isEmpty()) {
            SignUpPasswordEditText.setError("Enter a password");
            SignUpPasswordEditText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEmailEditText.setError("Enter a valid email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        //checking the validity of the password

        else if (password.length() < 6) {
            SignUpPasswordEditText.setError("Minimum length of password should be six");
            SignUpPasswordEditText.requestFocus();
            return;
        } else if (selectedSpin1.equals("Select Your District")) {
            Toast.makeText(SignUp.this, "Please select your District from the list", Toast.LENGTH_LONG).show();
            dtxtv.setError("District is required!");      //To set error on TextView
            dtxtv.requestFocus();
        } else if (selectedSpin2.equals("Select Your Subdistrict")) {
            Toast.makeText(SignUp.this, "Please select your Subistrict from the list", Toast.LENGTH_LONG).show();
            stxtv.setError("Subdistrict is required!");
            stxtv.requestFocus();
            dtxtv.setError(null);                      //To reove error from stateSpinner
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    String flag = "0", district = selectedSpin1, subdistrict = selectedSpin2, pimg = "https://firebasestorage.googleapis.com/v0/b/logreg-60384.appspot.com/o/images%2Fuser_on.png?alt=media&token=4d10f1cc-d9fa-4c85-b52a-774ee979b7f5";
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //databaseReference.child("ALLUSER").child(id).setValue(map);
                        User user = new User(uid, name, age, email, district, subdistrict, pimg, flag);
//                        databaseReference.child("ALLUSER").child(uid).child("Name").setValue(name);
                        databaseReference.child("NEWUSER").child(uid).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            verifyEmail1();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "User is Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        //Toast.makeText(getApplicationContext(),  "Register is not successfull", Toast.LENGTH_SHORT).show();
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        verifyEmail(name, email, password);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Login is Unsuccessfull", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            //Toast.makeText(SignUp.this, "DONE!", Toast.LENGTH_SHORT).show();
        }

    }

    public void verifyEmail1() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification();
        Toast.makeText(getApplicationContext(), "Please check gmail to verify your email", Toast.LENGTH_SHORT).show();
        mAuth.signOut();
    }

    public void verifyEmail(String name, String email, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {
            finish();
            mAuth.signOut();
            //FirebaseUser curruser = FirebaseAuth.getInstance().getCurrentUser();
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("email", email);
            bundle.putString("password", password);
//            //Toast.makeText(getApplicationContext(),  "Login is successfull", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PhoneAuth.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Please check gmail to verify your email", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

    private void getValuesD() {
        DistrictsArray = new ArrayList<>();
        DistrictsArray.add("Select Your District");
        databaseReference.child("Districts").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (int j = 1; j <= 3; j++) {
                    String lnumstr = Integer.toString(j);
                    String pos = "district" + lnumstr;
                    String like = String.valueOf(dataSnapshot.child(pos).getValue());
                    DistrictsArray.add(like);
                }
            }
        });
    }

    private void getValuesS(int p) {
        SubdistrictsArray = new ArrayList<>();
        SubdistrictsArray.add("Select Your Subdistrict");
        if (p == 0)
            return;
        databaseReference.child("Subdistricts").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                int l, k;
                if (p == 1) {
                    l = 1;
                    k = 3;
                } else if (p == 2) {
                    l = 4;
                    k = 7;
                } else {
                    l = 8;
                    k = 10;
                }
                for (int j = l; j <= k; j++) {
                    String lnumstr = Integer.toString(j);
                    String pos = "s" + lnumstr;
                    String like = String.valueOf(dataSnapshot.child(pos).getValue());
                    SubdistrictsArray.add(like);
                }
            }
        });
    }
}