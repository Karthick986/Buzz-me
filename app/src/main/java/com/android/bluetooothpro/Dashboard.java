package com.android.bluetooothpro;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Dashboard extends AppCompatActivity {

    DatabaseReference databaseReference;
    long i=0;
    String bdist="", bname="", bdatetime="";
    TextView noOfContacts;
    Button seeAllContacts;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_ativity);

//        MainActivity mainActivity = new MainActivity();
//        mainActivity.thread.start();

        noOfContacts = findViewById(R.id.noofContactstxt);
        seeAllContacts = findViewById(R.id.seeContactsbtn);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Devices").exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.child("Devices").getChildren()) {
                        i = dataSnapshot.getChildrenCount();

                         bdist = dataSnapshot.child("bdist").getValue().toString();
                         bname = dataSnapshot.child("bname").getValue().toString();
                         bdatetime = dataSnapshot.child("bdatetime").getValue().toString();

                        Toast.makeText(Dashboard.this, ""+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        noOfContacts.setText("You have come in contact with " +i+ " co-workers at buzz");

    }
}