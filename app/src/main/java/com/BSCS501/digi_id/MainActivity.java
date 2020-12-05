package com.BSCS501.digi_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;
import android.os.Handler;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth Fauth;
    FirebaseFirestore fstore;
    TextView pname, pemail, pPhone;
    Button logout;
    Button camsnap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);
        pname = findViewById(R.id.profileFullName);
        pemail = findViewById(R.id.profileEmail);
        pPhone = findViewById(R.id.profilePhone);

        Fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        boolean btn = false;

        final DocumentReference docRef = fstore.collection("users").document(Fauth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    pname.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleName") + " " + documentSnapshot.getString("LastName"));
                    pemail.setText(documentSnapshot.getString("Email"));
                    pPhone.setText(Fauth.getCurrentUser().getPhoneNumber());
                }


                camsnap = findViewById(R.id.enrol);
                camsnap.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        switchActivites();
                    }
                });
            }
        });
        }

        private void switchActivites() {
            Intent swapIntent = new Intent (this, CamSnap.class);
            startActivity(swapIntent);

        }



    public void btnlogout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Registration.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Registration.class));
                finish();

        return super.onOptionsItemSelected(item);
    }

}