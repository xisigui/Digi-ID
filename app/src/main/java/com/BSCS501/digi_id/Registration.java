package com.BSCS501.digi_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth authentication;//<<firebase authetication
    FirebaseFirestore fstore; //<<database firebase
    EditText phoneNum, otp;
    Button nextBtn;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker picker; //<<added library
    String verificationID;
    PhoneAuthProvider.ForceResendingToken Token;
    boolean verificationprogress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        authentication = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        phoneNum = findViewById(R.id.phoneNum);
        otp = findViewById(R.id.otp);
        nextBtn = findViewById(R.id.nextBtn);
        progressBar = findViewById(R.id.progressBar);
        state = findViewById(R.id.state);
        picker = findViewById(R.id.ccp);

        //One Time Password
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verificationprogress) {
                    if (!phoneNum.getText().toString().isEmpty() && phoneNum.getText().toString().length() == 10) {
                        String phonenum = "+" + picker.getSelectedCountryCode() + phoneNum.getText().toString();
                        Log.d(TAG, "onClick: PhoneNumber " + phonenum);
                        progressBar.setVisibility(View.VISIBLE);
                        state.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP...");
                        requestOTP(phonenum);
                    } else {
                        phoneNum.setError("Not Valid");
                    }
                } else {
                    String userOTP = otp.getText().toString();
                    if (!userOTP.isEmpty() && userOTP.length() == 6) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, userOTP);
                        verifyauthentication(credential);
                    } else {
                        otp.setError("Invalid OTP");
                    }
                }
            }
        });
    }
//overides the onStart at the main activity if the user is already logged in
    @Override
    protected void onStart() {
        super.onStart();

        if(authentication.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Loading");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }

    }
// for verification of OTP
    private void verifyauthentication(PhoneAuthCredential credential) {
        authentication.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Registration.this, "Authentication is successful.", Toast.LENGTH_SHORT).show();
                    checkUserProfile();
                } else {
                    Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//If the user already exist in the database, it will direct the user to the main menu or main activity.
    private void checkUserProfile() {
        DocumentReference docRef = fstore.collection("users").document(authentication.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(),Details.class));
                    finish();
                }
            }
        });
    }
//request otp in the firebase. OTP == 123456
    private void requestOTP(String phonenum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                otp.setVisibility(View.VISIBLE);
                verificationID = s;
                Token = forceResendingToken;
                nextBtn.setText("Verify");
                verificationprogress = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Registration.this, "Cannot Create Account" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}