package com.example.tutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MySpinner extends AppCompatActivity{
    String[]Names={"One","Two","Three"};
    private Spinner spinner;
    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myspinner);

        spinner = findViewById(R.id.spinnerID);
        textView =findViewById(R.id.textviewID);
        button =findViewById(R.id.buttonID);

        ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,R.layout.sample_view,R.id.textviewID,Names);
        spinner.setAdapter(adapter);
    }
}