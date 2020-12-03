package com.BSCS501.digi_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {
    //variable or objects
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    EditText lastnametxt, firstnametxt,middlenametxt, emailtxt, addresstxt;
    Button signup;
    String userID;
    //instantiate object
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        lastnametxt = findViewById(R.id.lastnametxt);
        firstnametxt = findViewById(R.id.firstnametxt);
        middlenametxt = findViewById(R.id.middlenametxt);
        emailtxt = findViewById(R.id.emailtxt);
        addresstxt = findViewById(R.id.addresstxt);
        signup = findViewById(R.id.signup);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference docRef = fstore.collection("users").document(userID);
        //signup
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lastnametxt.getText().toString().isEmpty() && !firstnametxt.getText().toString().isEmpty() && !middlenametxt.getText().toString().isEmpty() && !emailtxt.getText().toString().isEmpty() && !addresstxt.getText().toString().isEmpty())
                {
                    //Data to Firebase
                    String lastname = lastnametxt.getText().toString();
                    String firstname = firstnametxt.getText().toString();
                    String middlename = middlenametxt.getText().toString();
                    String email = emailtxt.getText().toString();
                    String address = addresstxt.getText().toString();

                    Map<String, Object> user = new HashMap<>();
                    user.put("LastName",lastname);
                    user.put("FirstName",firstname);
                    user.put("MiddleName",middlename);
                    user.put("Email",email);
                    user.put("Address",address);

                    //Data to Firebase checker
                    docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(Details.this, "Data is not inserted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(Details.this, "All Fields are Required",Toast.LENGTH_SHORT);
                }
            }
        });


    }
}