package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class TrySpinner extends AppCompatActivity {
    private Spinner spinner1, spinner2;
    private Button button;
    private TextView textView,dtxtv,stxtv;
    private String selectedSpin1, selectedSpin2;
    private ArrayAdapter<String> adapter1, adapter2;
    DatabaseReference databaseReference, databaseReference1;
    private FirebaseAuth mAuth;

    static ArrayList<String> DistrictsArray, SubdistrictsArray,Names,SubNames1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_spinner);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dtxtv=findViewById(R.id.dtxtv);
        stxtv=findViewById(R.id.stxtv);
        //getValuesD();
//        getValuesS(2);

       // String feature = getResources().getString(R.string.page1);

//        String feature = getResources().getString(R.string.page2);
//
//        String feature = getResources().getString(R.string.page3);
//
//        String feature = getResources().getString(R.string.page4);
//
        String feature = getResources().getString(R.string.page5);
////        String s1="Chittagong Sadar",s2="Hathazari",s3="Sitakunda",s4="Cox's Bazar Sadar",s5="Ramu",s6="Teknaf",s7="Ukhia",s8="Rangamati Sadar",s9="Kaptai",s10="Naniarchar";
        Location loc=new Location(feature);
        databaseReference.child("Insects").child("Dragonfly").setValue(loc);

//        spinner1 =(Spinner) findViewById(R.id.spinner1ID);
//        spinner2 =(Spinner) findViewById(R.id.spinner2ID);
//        //textView =findViewById(R.id.textviewID);
//        button =findViewById(R.id.buttonID);
//
//
//        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DistrictsArray);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner1.setAdapter(adapter1);
//
//        adapter2 = new ArrayAdapter<>(TrySpinner.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner2.setAdapter(adapter2);        //Populate the list of Districts in respect of the State selected

//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.sample_view,R.id.textviewID,Names);
//        spinner.setAdapter(adapter);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String value=spinner.getSelectedItem().toString();
//               // textView.setText(value);
//            }
//        });
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                selectedSpin1 = spinner1.getSelectedItem().toString();      //Obtain the selected State
//
//                int parentID = adapterView.getId();
//                if (parentID == R.id.spinner1ID){
//                    switch (selectedSpin1){
//                        case "Select Your District": {
//                            getValuesS(0);
//                            adapter2 = new ArrayAdapter<>(TrySpinner.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
//                            break;
//                        }
//
//                        case "Chittagong": {
//                            getValuesS(1);
//                            adapter2 = new ArrayAdapter<>(TrySpinner.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
//                            break;
//                        }
//                        case "Cox'sBazar": {
//                            getValuesS(2);
//                            adapter2 = new ArrayAdapter<>(TrySpinner.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
//                            break;
//                        }
//                        default: {
//                            getValuesS(3);
//                            adapter2 = new ArrayAdapter<>(TrySpinner.this, android.R.layout.simple_spinner_item, SubdistrictsArray);
//                            break;
//                        }
//                }
//
//                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner2.setAdapter(adapter2);
//                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            selectedSpin2 = spinner2.getSelectedItem().toString();
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//            public void onClick(View view) {
//                if (selectedSpin1.equals("Select Your District")) {
//                    Toast.makeText(TrySpinner.this, "Please select your District from the list", Toast.LENGTH_LONG).show();
//                    dtxtv.setError("District is required!");      //To set error on TextView
//                    dtxtv.requestFocus();
//                } else if (selectedSpin2.equals("Select Your Subdistrict")) {
//                    Toast.makeText(TrySpinner.this, "Please select your Subistrict from the list", Toast.LENGTH_LONG).show();
//                    stxtv.setError("Subdistrict is required!");
//                    stxtv.requestFocus();
//                    dtxtv.setError(null);                      //To reove error from stateSpinner
//                } else {
//                    stxtv.setError(null);
//                    dtxtv.setError(null);
//                    Toast.makeText(TrySpinner.this, "Selected District: "+selectedSpin1+"\nSelected Subdistrict: "+selectedSpin2, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    private void getValuesD() {
        DistrictsArray = new ArrayList<>();
        DistrictsArray.add("Select Your District");
        databaseReference.child("Districts").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for(int j=1;j<=3;j++) {
                            String lnumstr = Integer.toString(j);
                            String pos="district"+lnumstr;
                            String like = String.valueOf(dataSnapshot.child(pos).getValue());
                            DistrictsArray.add(like);
                        }
            }
        });
    }

    private void getValuesS(int p) {
        SubdistrictsArray= new ArrayList<>();
        SubdistrictsArray.add("Select Your Subdistrict");
        if(p==0)
            return;
        databaseReference.child("Subdistricts").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        int l,k;
                        if(p==1)
                        {
                            l=1;
                            k=3;
                        }
                        else if(p==2){
                            l=4;
                            k=7;
                        }
                        else {
                            l=8;
                            k=10;
                        }
                        for(int j=l;j<=k;j++) {
                            String lnumstr = Integer.toString(j);
                            String pos="s"+lnumstr;
                            String like = String.valueOf(dataSnapshot.child(pos).getValue());
                            SubdistrictsArray.add(like);
                        }


            }
        });
    }

}