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

public class Profile extends DrawerBase{
    ActivityProfileBinding activityProfileBinding;
    //drawer  https://www.bdtopcoder.xyz/2022/07/android-navigation-drawer-custom.html

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private ImageButton btnSelect;
    private Button btnVid;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        //setContentView(R.layout.activity_main);
        allocateActivityTitle("Map");
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Access the GoogleMap object here

                // Create LatLng objects for the locations
                LatLng sundarbansLocation = new LatLng(21.9497, 89.1833);
                LatLng lawacharaLocation = new LatLng(24.3524, 91.7360);
                LatLng baikkaBeelLocation = new LatLng(23.1167, 90.7167);
                LatLng bhawalLocation = new LatLng(23.8265, 90.2607);
                LatLng madhupurLocation = new LatLng(24.3317, 90.2725);

                // Create a LatLngBounds.Builder to include all the locations
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(sundarbansLocation);
                builder.include(lawacharaLocation);
                builder.include(baikkaBeelLocation);
                builder.include(bhawalLocation);
                builder.include(madhupurLocation);
                LatLngBounds bounds = builder.build();

                // Set the camera position and zoom level to include all the locations
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100); // Adjust the padding as needed
                googleMap.animateCamera(cameraUpdate);

                // Add markers for each location
                googleMap.addMarker(new MarkerOptions().position(sundarbansLocation).title("Sundarbans National Park"));
                googleMap.addMarker(new MarkerOptions().position(lawacharaLocation).title("Lawachara National Park"));
                googleMap.addMarker(new MarkerOptions().position(baikkaBeelLocation).title("Baikka Beel Wetland"));
                googleMap.addMarker(new MarkerOptions().position(bhawalLocation).title("Bhawal National Park"));
                googleMap.addMarker(new MarkerOptions().position(madhupurLocation).title("Madhupur National Park"));

                // Check if the location permission is granted
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Enable the My Location button and show the current location
                    googleMap.setMyLocationEnabled(true);

                } else {
                    // Request the location permission
                    ActivityCompat.requestPermissions(Profile.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_LOCATION_PERMISSION_REQUEST_CODE);
                }


            }
        });





//
//        btnSelect = findViewById(R.id.btnChoose);
//        imageView = findViewById(R.id.imgView);
//
//
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        //databaseReference= FirebaseDatabase.getInstance().getReference("Upload");
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        uid=user.getUid();
//        databaseReference = FirebaseDatabase.getInstance().getReference("NEWUSER");
//        setPImage();
//        btnSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                SelectImage();
//            }
//        });
    }

//    private void SelectImage()
//    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode,data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
////                imageView.setImageBitmap(bitmap);
//                uploadImage();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public String getFileExtension(Uri imageUri){
//        ContentResolver contentResolver=getContentResolver();
//        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
//    }
//
//    private void setPImage(){
//        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    if(task.getResult().exists()){
//                        DataSnapshot dataSnapshot=task.getResult();
////                                String name=String.valueOf(dataSnapshot.child("name").getValue());
//                        String img=String.valueOf(dataSnapshot.child("pimg").getValue());
//                        //int rnum = Integer.parseInt(String.valueOf(img));
//                        //imageView.setIm((float)rnum);
//                        Picasso.get().load(img).into(imageView);
//                    }
//                    else {
//                        Toast.makeText(Profile.this, "No data exists", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                    Toast.makeText(Profile.this, "No data exists", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//    private void uploadImage() {
//        if (filePath != null) {
//
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//
//            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
//
//            ref.putFile(filePath)
//                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
//                                {
//                                    progressDialog.dismiss();
//                                    Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
//                                    while (!uriTask.isSuccessful());
//                                    Uri downloadUrl=uriTask.getResult();
//                                    String imgref=downloadUrl.toString();
//                                    databaseReference.child(uid).child("pimg").setValue(imgref);
//                                    Toast.makeText(Profile.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
//                                    setPImage();
//
//                                }
//                            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e)
//                        {
//                            progressDialog.dismiss();
//                            Toast.makeText(Profile.this,"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(
//                            new OnProgressListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
//                                }
//                            });
//        }
//}
}