package com.example.tutorial;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial.ml.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class Predict extends AppCompatActivity {
    private static final String FILE_NAME = "canvas_pdf.pdf";

    private Button createPdfButton;
    private String uid;
    private String[] userData = new String[5]; // For UID, Name, Email, Address, and DOB

    private ArrayList<String[]> postDataRows = new ArrayList<>();
    private int numberOfPosts = 0;
    TextView result, confidence;
    ImageView imageView;
    Button picture, selectBtn, share;
    int imageSize = 224, maxPos = 0;
    String imageURL = "";


    DatabaseReference databaseReference, databaseReference1, databaseReference2;

    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    static ArrayList<String> pdNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        selectBtn = findViewById(R.id.selectBtn);
        share = findViewById(R.id.share);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        share.setEnabled(false);
        if (user != null) {
            uid = user.getUid();
        }
        postuser();

        createPdfButton = findViewById(R.id.button_create_pdf);

        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("NEWUSER").child(uid);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                userData[0] = snapshot.child("userid").getValue(String.class);
                                userData[1] = snapshot.child("name").getValue(String.class);
                                userData[2] = snapshot.child("email").getValue(String.class);
                                userData[3] = snapshot.child("subdistrict").getValue(String.class) + ", " +
                                        snapshot.child("district").getValue(String.class);
                                userData[4] = snapshot.child("age").getValue(String.class);

                                // Call the method to create the PDF report once you have the user data
                                createPdfReport();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error if data retrieval is canceled or fails
                        }
                    });
                }

            }
        });



        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });
    }


    private void postuser(){
        DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference("Post_User");
        postUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @android.support.annotation.Nullable String previousChildName) {
                numberOfPosts++;
                String postUserId = snapshot.child("userid").getValue(String.class);
                if (postUserId != null && postUserId.equals(uid)) {
                    fetchPostData(snapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @android.support.annotation.Nullable String previousChildName) {
                // Handle post data changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle post data removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @android.support.annotation.Nullable String previousChildName) {
                // Handle post data movement if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if data retrieval is canceled or fails
            }
        });

// Helper method to fetch post data from a DataSnapshot and add it to the postDataRows array

    }

    private void fetchPostData(DataSnapshot postSnapshot) {
        String[] postData = new String[4];
        postData[0] = postSnapshot.getKey(); // PostID
        postData[1] = calculateTotalLikes(postSnapshot);
        postData[2] = calculateTotalDisLikes(postSnapshot);
        postData[3] = calculateAverageRating(postSnapshot); // Avg Rating
//        postData[4] = postSnapshot.child("datetime").getValue(String.class);

        postDataRows.add(postData);
    }

    private String calculateTotalDisLikes(DataSnapshot postSnapshot){
        int totalDisLikes = 0;

        for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
            if (userSnapshot.hasChild("dislike")) {
                int dislike = Integer.parseInt(userSnapshot.child("like").getValue(String.class));
                totalDisLikes += dislike;
            }
        }
        return String.valueOf(totalDisLikes);
    }

    private String calculateTotalLikes(DataSnapshot postSnapshot){
        int totalLikes = 0;

        for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
            if (userSnapshot.hasChild("like")) {
                int like = Integer.parseInt(userSnapshot.child("like").getValue(String.class));
                totalLikes += like;
            }
        }
        return String.valueOf(totalLikes);
    }

    private String calculateAverageRating(DataSnapshot postSnapshot) {
        int totalRating = 0;
        int numRatings = 0;
        for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
            if (userSnapshot.hasChild("rating")) {
                int rating = Integer.parseInt(userSnapshot.child("rating").getValue(String.class));
                totalRating += rating;
                numRatings++;
            }
        }

        if (numRatings > 0) {
            float averageRating =(float) totalRating / numRatings;
            return String.valueOf(averageRating);
        } else {
            return "N/A"; // If there are no ratings, show "N/A"
        }
    }


    private void createPdfReport() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Set up paints for drawing
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLUE);
        paint.setTextSize(18.0f);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        String dateText = dateFormat.format(calendar.getTime());

        float dateTextX = canvas.getWidth() - 110f;
        float dateTextY = 75f;

        canvas.drawText("Insect Classification",pageInfo.getPageWidth()/2,30,paint);
        paint.setTextSize(14.0f);
        canvas.drawText("CSE, CU",pageInfo.getPageWidth()/2,50,paint);


        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        canvas.drawText(dateText, dateTextX, dateTextY, paint);



        // Draw User Information Table
        float userInfoStartX = 50f;
        float userInfoStartY = 100f;
        float rowHeight = 30f;
        float cellWidth = 200f;

        // Draw table fields
        String[] fieldNames = {"UID", "Name", "Email", "Address", "DOB"};

        // Draw horizontal lines for the table
        for (int i = 0; i <= fieldNames.length; i++) {
            canvas.drawLine(userInfoStartX, userInfoStartY + i * rowHeight,
                    userInfoStartX + 2 * cellWidth, userInfoStartY + i * rowHeight, paint);
        }

        canvas.drawLine(userInfoStartX + 0 * cellWidth, userInfoStartY,
                userInfoStartX + 0 * cellWidth, userInfoStartY + fieldNames.length * rowHeight, paint);

        canvas.drawLine(userInfoStartX + 1 * cellWidth/2, userInfoStartY,
                userInfoStartX + 1 * cellWidth/2, userInfoStartY + fieldNames.length * rowHeight, paint);

        canvas.drawLine(userInfoStartX + 2 * cellWidth, userInfoStartY,
                userInfoStartX + 2 * cellWidth, userInfoStartY + fieldNames.length * rowHeight, paint);

//        // Draw vertical lines for the table
//        for (int i = 0; i <= fieldNames.length; i++) {
//            canvas.drawLine(userInfoStartX + i * cellWidth, userInfoStartY,
//                    userInfoStartX + i * cellWidth, userInfoStartY + fieldNames.length * rowHeight, paint);
//        }

        // Draw table fields and data
        for (int i = 0; i < fieldNames.length; i++) {
            canvas.drawText(fieldNames[i], userInfoStartX + 5f, userInfoStartY + i * rowHeight + rowHeight - 5f, paint);
            canvas.drawText(userData[i], userInfoStartX + cellWidth - 70f, userInfoStartY + i * rowHeight + rowHeight - 5f, paint);
        }

        // Draw "Your Posts" Table
        float postsTableStartX = 50f;
        float postsTableStartY = userInfoStartY + 6 * rowHeight;
        cellWidth = 100f;




        // Draw table header
        String[] postFieldNames = {"PostID", "Likes", "Dislikes", "Avg Rating"};

        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        int k=0;
        for (int i = 0; i < postFieldNames.length; i++) {
            canvas.drawText(postFieldNames[i], postsTableStartX + k * cellWidth, postsTableStartY, paint);
            if(i==0)
                k+=2;
            else
                k++;

        }
        paint=new Paint();



        int j=0;
        for (j = 0; j < postDataRows.size(); j++) {

            String[] postData = postDataRows.get(j);
            k=0;
            for (int i = 0; i < 4; i++) {
                canvas.drawText(postData[i], postsTableStartX + k * cellWidth, postsTableStartY + (j + 1) * rowHeight, paint);
                if(i==0)
                    k+=2;
                else
                    k++;
            }
        }



//        for (int j = 0; j < postDataRows.length; j++) {
//            for (int i = 0; i < postDataRows[j].length; i++) {
//                canvas.drawText(postDataRows[j][i], postsTableStartX + i * cellWidth, postsTableStartY + (j + 1) * rowHeight, paint);
//            }
//        }

        pdfDocument.finishPage(page);

        // Save the PDF to Downloads folder
        String fileName = "insect_classification_report.pdf";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File filePath = new File(downloadsDir, fileName);
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
            pdfDocument.close();

            Toast.makeText(getApplicationContext(), "Report generated in Downloads Folder", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            // You can show a toast or a message here to indicate PDF creation failure.
        }
    }

    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.

            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            //String[] classes = {"Banana", "Orange", "Pen", "Sticky Notes"};
            String[] classes = {"Butterfly", "Dragonfly", "Grasshopper", "Mosquito", "Spider"};
            String text = classes[maxPos];
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
            result.setText(spannableString);

            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            confidence.setText(s);


            // Releases model resources if no longer used.
            model.close();

            findAllPDNodes();
            share.setEnabled(true);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadImageToFirebase(image, classes[maxPos], confidences[maxPos]);
                }
            });

            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", maxPos);
                    Intent intent = new Intent(getApplicationContext(), Videos.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });


        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    private void uploadImageToFirebase(Bitmap image, String class_max, Float confid_max) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String filename = "image_" + System.currentTimeMillis() + ".jpg";

        // Create a reference to the desired location in Firebase Storage
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference imageRef = storageReference.child("images/" + filename);

        // Convert the Bitmap image to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image uploaded successfully
                // Get the image URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageURL = uri.toString();
                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Success ", Toast.LENGTH_SHORT).show();

                    // After getting the image URL, you can proceed with inserting it into the database
                    String userid = uid;
                    String result = String.format("%s: %.1f%%\n", class_max, confid_max * 100);
                    String post_image = imageURL;
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String datetime = df.format(Calendar.getInstance().getTime());

                    Post post = new Post(userid, result, post_image, datetime);

                    Post_User data = new Post_User("0", "0", "0", " ",datetime);
                    int num_users = pdNodes.size();
                    String post_user_id = "post_user" + new Date().getTime();

                    databaseReference.child("Post_User").child(post_user_id).setValue(post)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                    } else {
                                        Toast.makeText(getApplicationContext(), "User is Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                    for (int i = 0; i < num_users; i++) {
                        databaseReference.child("Post_User").child(post_user_id).child(pdNodes.get(i)).setValue(data)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "User is Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
//                    databaseReference.child("Posts").child(postid).setValue(post)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "User is Failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

                }).addOnFailureListener(exception -> {
                    Log.e("Upload", "Error getting image URL", exception);
                });
            } else {
                Log.e("Upload", "Image upload failed", task.getException());
            }
        });
    }

    private void findAllPDNodes() {
        databaseReference.child("NEWUSER").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pdNodes = new ArrayList<>();
                for (DataSnapshot pdSnapshot : dataSnapshot.getChildren()) {
                    String pdNode = pdSnapshot.getKey();
                    pdNodes.add(pdNode);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PDNode", "Error finding PD nodes: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        } else if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    Bitmap image = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    int dimension = Math.min(image.getWidth(), image.getHeight());
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                    imageView.setImageBitmap(image);
                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    classifyImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}