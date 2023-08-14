package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DrawerBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private String uid;

    TextView nameuser;
    private ImageButton btnSelect;
    private Button btnVid;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;


    public void setContentView(View view){
        drawerLayout =(DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container=drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);
        Toolbar toolbar=drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        NavigationView navigationView=drawerLayout.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnSelect = headerView.findViewById(R.id.btnChoose);
        nameuser = headerView.findViewById(R.id.nameuser);
        imageView =headerView.findViewById(R.id.pimg);



        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //databaseReference= FirebaseDatabase.getInstance().getReference("Upload");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("NEWUSER");

        if(user!=null) {
            uid = user.getUid().toString();
            setPImage();
        }

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(user!=null)
                    SelectImage();
                else
                    Toast.makeText(DrawerBase.this, "Please Sign In First", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void SelectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
//                imageView.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void setPImage(){
        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot=task.getResult();
//                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                        String img=String.valueOf(dataSnapshot.child("pimg").getValue());
                        String usName=String.valueOf(dataSnapshot.child("name").getValue());
                        //int rnum = Integer.parseInt(String.valueOf(img));
                        //imageView.setIm((float)rnum);
                        Picasso.get().load(img).transform(new CircleTransformation()).into(imageView);
//                        Picasso.get().load(img).into(imageView);
                        nameuser.setText(usName);
                    }
                    else {
                        Toast.makeText(DrawerBase.this, "No data exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(DrawerBase.this, "No data exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadImage() {
        if (filePath != null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            progressDialog.dismiss();
                            Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUrl=uriTask.getResult();
                            String imgref=downloadUrl.toString();
                            databaseReference.child(uid).child("pimg").setValue(imgref);
                            Toast.makeText(DrawerBase.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                            setPImage();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(DrawerBase.this,"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        MenuItem menu1 = menu.findItem(R.id.mSignUp);
        MenuItem menu2 = menu.findItem(R.id.mSignIn);
        MenuItem menu3 = menu.findItem(R.id.mSignOut);


        if(user!=null) {
            menu1.setVisible(false);
            menu2.setVisible(false);
        }
        else {
            menu3.setVisible(false);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.mSignUp:
                finish();
                intent=new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                break;
            case R.id.mSignIn:
                finish();
                intent=new Intent(getApplicationContext(), Fireauthf.class);
                startActivity(intent);
                break;
            case R.id.mSignOut:
                finish();
                FirebaseAuth.getInstance().signOut();
                intent=new Intent(getApplicationContext(), Fireauthf.class);
                startActivity(intent);
//                Toast.makeText(DrawerBase.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout.closeDrawer(GravityCompat.START);
        Intent intent=new Intent();
        switch (item.getItemId()){
            case R.id.mHome:
                finish();
                intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;

            case R.id.mPredict:
                intent=new Intent(getApplicationContext(), Predict.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;

            case R.id.post:
                intent=new Intent(getApplicationContext(), Trymap.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;

            case R.id.mYou:
                // finish();
                intent=new Intent(getApplicationContext(), YoutubeVideo.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;

        }
        return false;
    }
    protected void allocateActivityTitle(String titleString){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(titleString);
        }
    }
}