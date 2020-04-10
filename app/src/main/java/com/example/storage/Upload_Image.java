package com.example.storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Upload_Image extends AppCompatActivity {
    private ImageView imageToUploadIV;
    private Uri imageDataInUriForm;
    private EditText imageNameET;
    private static final int REQUEST_CODE = 123;
    private Button imageUploadingBtn,go;
    private TextView TV;
    private ProgressBar bar;

    private StorageReference objectStorageReference;

    private FirebaseFirestore objectFirebaseFirestore;
    private boolean isImageSelected=false;
private DatePickerDialog DateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        connectXMLToJava();
    }

    private void connectXMLToJava() {
        try {
            TV=findViewById(R.id.TV);
            bar=findViewById(R.id.ProgressBar);
            imageNameET = findViewById(R.id.imageNameET);
                go=findViewById(R.id.go);
            imageUploadingBtn = findViewById(R.id.imageUploadingBtn);
            objectFirebaseFirestore=FirebaseFirestore.getInstance();
            objectStorageReference= FirebaseStorage.getInstance().getReference("Gallery");
            imageToUploadIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGallery();

                }
            });
            TV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal=Calendar.getInstance();
                    int year=cal.get(Calendar.YEAR);
                    int month=cal.get(Calendar.MONTH);
                    int day=cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dialogue=new DatePickerDialog(Upload_Image.this,android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth, (DatePickerDialog.OnDateSetListener) DateSetListener,year,month,day);
                    dialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogue.show();

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "connectXMLToJava:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        try {
            Intent objectIntent = new Intent(); //Step 1:create the object of intent
            objectIntent.setAction(Intent.ACTION_GET_CONTENT); //Step 2: You want to get some data

            objectIntent.setType("image/*");//Step 3: Images of all type
            startActivityForResult(objectIntent, REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(this, "openGallery:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                imageDataInUriForm = data.getData();
                Bitmap objectBitmap;

                objectBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageDataInUriForm);
                imageToUploadIV.setImageBitmap(objectBitmap);

//                isImageSelected = true;

            } else if (requestCode != REQUEST_CODE) {
                Toast.makeText(this, "Request code doesn't match", Toast.LENGTH_SHORT).show();
            } else if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Fails to get image", Toast.LENGTH_SHORT).show();
            } else if (data == null) {
                Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Toast.makeText(this, "onActivityResult:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
    private void uploadOurImage()
    {
        try
        {
            if(imageDataInUriForm!=null && !imageNameET.getText().toString().isEmpty()
                    && isImageSelected)
            {
                //yourName.jpeg
                String imageName=imageNameET.getText().toString()+"."+getExtension(imageDataInUriForm);

                //FirebaseStorage/BSCSAImagesFolder/yourName.jpeg
                final StorageReference actualImageRef=objectStorageReference.child(imageName);

                UploadTask uploadTask=actualImageRef.putFile(imageDataInUriForm);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            bar.setVisibility(View.INVISIBLE);
                            throw task.getException();
                        }
                        return actualImageRef.getDownloadUrl();
                    }
                 }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().toString();
                    Map<String, Object> objectMap = new HashMap<>();
                    objectMap.put("URL", url);
                    objectMap.put("Server Time Stamp", FieldValue.serverTimestamp());
                    objectFirebaseFirestore.collection("Gallery")
                            .document(imageNameET.getText().toString())
                            .set(objectMap)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    bar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Upload_Image.this, "Fails To Upload Image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    imageNameET.setText("");
                                    imageToUploadIV.setVisibility(View.INVISIBLE);
                                    bar.setVisibility(View.INVISIBLE);
                                    TV.setVisibility(View.VISIBLE);
                                    Toast.makeText(Upload_Image.this, "Image Successfully Uploaded: ", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(Upload_Image.this, "Fails To Upload Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
            }
            else if(imageDataInUriForm==null)
            {
                Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
            }
            else if(imageNameET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please first you need to put image name", Toast.LENGTH_SHORT).show();
                imageNameET.requestFocus();
            }
            else if(!isImageSelected)
            {
                Toast.makeText(this, "Please select image view to select image", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "uploadOurImage:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri imageDataInUriForm)
    {
        try
        {
            ContentResolver objectContentResolver=getContentResolver();
            MimeTypeMap objectMimeTypeMap=MimeTypeMap.getSingleton();

            String extension=objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(imageDataInUriForm));
            return extension;
        }
        catch (Exception e)
        {
            Toast.makeText(this, "getExtension:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return "";
    }
    public void uploadimage(View view)
    {
        uploadOurImage();
    }
    public void change(View view) {
        startActivity(new Intent(Upload_Image.this, Download_Image.class));
        finish();
    }

}

