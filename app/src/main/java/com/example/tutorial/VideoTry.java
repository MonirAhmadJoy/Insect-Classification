package com.example.tutorial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class VideoTry extends AppCompatActivity {

    private static final String FILE_NAME = "canvas_pdf.pdf";

    private Button createPdfButton;
    private String uid;
    private String[] userData = new String[5]; // For UID, Name, Email, Address, and DOB

    private ArrayList<String[]> postDataRows = new ArrayList<>();
    private int numberOfPosts = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_try);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
    }

    private void postuser(){
        DatabaseReference postUserRef = FirebaseDatabase.getInstance().getReference("Post_User");
        postUserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // This method will be triggered when a new post is added to the "Post_User" node
                numberOfPosts++;
                String postUserId = snapshot.child("userid").getValue(String.class);
                if (postUserId != null && postUserId.equals(uid)) {
                    fetchPostData(snapshot);
                }
                // If all posts have been fetched, call the method to create the PDF report
//                if (numberOfPosts == postDataRows.size()) {
//                    createPdfReport();
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle post data changes if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle post data removal if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
            // You can show a toast or a message here to indicate successful PDF creation.
        } catch (IOException e) {
            e.printStackTrace();
            // You can show a toast or a message here to indicate PDF creation failure.
        }
    }
}

