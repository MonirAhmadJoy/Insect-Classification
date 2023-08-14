package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tutorial.databinding.ActivityLikeBinding;
import com.example.tutorial.databinding.ActivityMainBinding;
import com.example.tutorial.databinding.ActivityProfileBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends DrawerBase{
    ActivityMainBinding activityMainBinding;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        allocateActivityTitle("Home");
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                LatLng sundarbansLocation = new LatLng(21.9497, 89.1833);
                LatLng lawacharaLocation = new LatLng(24.3524, 91.7360);
                LatLng baikkaBeelLocation = new LatLng(23.1167, 90.7167);
                LatLng bhawalLocation = new LatLng(23.8265, 90.2607);
                LatLng madhupurLocation = new LatLng(24.3317, 90.2725);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(sundarbansLocation);
                builder.include(lawacharaLocation);
                builder.include(baikkaBeelLocation);
                builder.include(bhawalLocation);
                builder.include(madhupurLocation);
                LatLngBounds bounds = builder.build();

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100); // Adjust the padding as needed
                googleMap.animateCamera(cameraUpdate);


                googleMap.addMarker(new MarkerOptions().position(sundarbansLocation).title("Sundarbans National Park"));
                googleMap.addMarker(new MarkerOptions().position(lawacharaLocation).title("Lawachara National Park"));
                googleMap.addMarker(new MarkerOptions().position(baikkaBeelLocation).title("Baikka Beel Wetland"));
                googleMap.addMarker(new MarkerOptions().position(bhawalLocation).title("Bhawal National Park"));
                googleMap.addMarker(new MarkerOptions().position(madhupurLocation).title("Madhupur National Park"));


                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Enable the My Location button and show the current location
                    googleMap.setMyLocationEnabled(true);

                } else {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

}