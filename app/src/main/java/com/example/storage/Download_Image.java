package com.example.storage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;

public class Download_Image extends AppCompatActivity {
    private ImageView imageToUploadIV;
    private Uri imageDataInUriForm;
    private EditText imageNameET;
    private ProgressBar bar;
    private Button Download_btn,imageToDownloadIV;
    private String currentDate;

    private StorageReference objectStorageReference;

    private FirebaseFirestore objectFirebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download__image);
        imageNameET = findViewById(R.id.imageNameET);
        bar=findViewById(R.id.ProgressBar);
       Download_btn = findViewById(R.id.download_btn);
        objectFirebaseFirestore=FirebaseFirestore.getInstance();
        objectStorageReference= FirebaseStorage.getInstance().getReference("Gallery");
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    }
    private void Download() {
        try {
            if (!imageNameET.getText().toString().isEmpty()) {
                objectFirebaseFirestore.collection("Gallery")
                        .document(imageNameET.getText().toString())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String url = documentSnapshot.getString("URL");
                                    Glide.with(Download_Image.this)
                                            .load(url)
                                            .into(imageToUploadIV);
                                    Toast.makeText(Download_Image.this, "Image Downloaded", Toast.LENGTH_SHORT).show();

                                } else {
                                    bar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Download_Image.this, "No Such File Exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Download_Image.this, "Failed To Retrieve Image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please Enter Name of The Image To Download", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "DownloadError: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void download(View view){
        Download();
    }

}
