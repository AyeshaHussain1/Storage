package com.example.storage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText EmailET, PwdET;
    private Button sign,signUp;
    private FirebaseAuth auth;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmailET = findViewById(R.id.EmailET);
        PwdET = findViewById(R.id.PwdET);
        sign = findViewById(R.id.sign);
        signUp = findViewById(R.id.signUp);
        auth = FirebaseAuth.getInstance();
        bar=findViewById(R.id.ProgressBar);
    }
    public void Signup() {
        try {

            if (!EmailET.getText().toString().isEmpty()
                    &&
                    !PwdET.getText().toString().isEmpty()) {
                if (auth != null) {

                    bar.setVisibility(View.VISIBLE);
                    signUp.setEnabled(false);

                    auth.createUserWithEmailAndPassword(EmailET.getText().toString(),
                            PwdET.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            if (authResult.getUser() != null) {

                                auth.signOut();
                                EmailET.setText("");

                                PwdET.setText("");
                                EmailET.requestFocus();

                                signUp.setEnabled(true);
                                bar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            signUp.setEnabled(true);
                            EmailET.requestFocus();

                            bar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Failed To Create User" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (EmailET.getText().toString().isEmpty()) {
                signUp.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                EmailET.requestFocus();
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (PwdET.getText().toString().isEmpty()) {
                signUp.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                PwdET.requestFocus();
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

            signUp.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);

            EmailET.requestFocus();
            Toast.makeText(this, "Signup Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void Login() {
        try {

            if (!EmailET.getText().toString().isEmpty() && !PwdET.getText().toString().isEmpty()) {

                if (auth.getCurrentUser() != null) {


                    auth.signOut();
                    sign.setEnabled(false);

                    Toast.makeText(this, "User Logged Out Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(EmailET.getText().toString(),
                            PwdET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    bar.setVisibility(View.INVISIBLE);
                                    sign.setEnabled(true);
                                    Toast.makeText(MainActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            sign.setEnabled(true);
                            EmailET.requestFocus();

                        }
                    });
                }
            } else if (EmailET.getText().toString().isEmpty()) {
                sign.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                EmailET.requestFocus();
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (PwdET.getText().toString().isEmpty()) {
                sign.setEnabled(true);
                 bar.setVisibility(View.INVISIBLE);

                PwdET.requestFocus();
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

            sign.setEnabled(true);
            EmailET.requestFocus();

            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Logging In Error" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void openUploadPage(View view) {
        try {
            startActivity(new Intent(this, Upload_Image.class));
        } catch (Exception e) {
            Toast.makeText(this, "openUploadPage:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Login(View view) {
        Login();
    }
public void Signup(View view){
        Signup();
}
    public String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            Toast.makeText(this, "getCurrentTimeStamp:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        }



}
