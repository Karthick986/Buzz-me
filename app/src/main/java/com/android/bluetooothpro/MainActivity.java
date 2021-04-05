package com.android.bluetooothpro;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnScanDevice;
    BluetoothAdapter bluetoothAdapter;
     int i=0;
     DatabaseReference databaseReference;
    TextView noOfContacts;
    Button seeAllContacts, hideAllContacts;
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<GetSet> getSets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScanDevice = (Button) findViewById(R.id.onoff);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnScanDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }
        });

        bluetoothAdapter.startDiscovery();

        registerReceiver(ActionFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));

        noOfContacts = findViewById(R.id.noofContactstxt);
        seeAllContacts = findViewById(R.id.seeContactsbtn);
        hideAllContacts = findViewById(R.id.hideContactsbtn);

        recyclerView = findViewById(R.id.deviceRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);

        getSets = new ArrayList<GetSet>();

        seeAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAllContacts.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                hideAllContacts.setVisibility(View.VISIBLE);
            }
        });

        hideAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllContacts.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                seeAllContacts.setVisibility(View.VISIBLE);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("Devices").exists()) {

                    getSets.clear();
                    //seeAllContacts.setVisibility(View.VISIBLE);
                    long j=0;
                    j = snapshot.child("Devices").getChildrenCount();

                    noOfContacts.setText("You have come in contact with " +j+ " co-workers' at buzz");

                    for (DataSnapshot dataSnapshot: snapshot.child("Devices").getChildren()) {

                        GetSet h = dataSnapshot.getValue(GetSet.class);
                        getSets.add(h);
                    }
                    adapter = new Adapter(MainActivity.this, getSets);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    seeAllContacts.setVisibility(View.GONE);
                    noOfContacts.setText("Good day! You have not come with any contact at buzz");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        thread.start();
    }

    Thread thread = new Thread() {

        @Override
        public void run() {
            try {
                while (!thread.isInterrupted()) {
                    Thread.sleep(10000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            i = i+1;
                            Toast.makeText(MainActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                            bluetoothAdapter.startDiscovery();

                            registerReceiver(ActionFoundReceiver,
                                    new IntentFilter(BluetoothDevice.ACTION_FOUND));

                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };


    public void enableDisableBT(){
        if(bluetoothAdapter == null){
          //  Toast.makeText(this, "enableDisableBT: Does not have BT capabilities.", Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()){
        //    Toast.makeText(this, "enableDisableBT: enabling BT.", Toast.LENGTH_SHORT).show();
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(ActionFoundReceiver, BTIntent);
            registerReceiver(ActionFoundReceiver,
                    new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
        if(bluetoothAdapter.isEnabled()){
        //    Toast.makeText(this, "enableDisableBT: disabling BT.", Toast.LENGTH_SHORT).show();
            bluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(ActionFoundReceiver, BTIntent);

            registerReceiver(ActionFoundReceiver,
                    new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    public final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        //btArrayAdapter.clear();
                        btnScanDevice.setText("ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                       // btArrayAdapter.clear();
                        btnScanDevice.setText("ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        btnScanDevice.setText("OFF");
                        //   Toast.makeText(context, "mBroadcastReceiver1: STATE ON", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        btnScanDevice.setText("OFF");
                        //Toast.makeText(context, "mBroadcastReceiver1: STATE TURNING ON", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            final double rssi;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                double d = Math.pow(10, (-60 - rssi) / 20);

                DecimalFormat decimalFormat = new DecimalFormat("0.000");

                if (d <= 1.8288) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy, HH:mm:ss");
                    String currentDateandTime = dateFormat.format(Calendar.getInstance().getTime());

                    HashMap<String, Object> bluMap = new HashMap<>();
                    bluMap.put("bname", device.getName());
                    bluMap.put("bdist", decimalFormat.format(d));
                    bluMap.put("bdatetime", currentDateandTime);

                    databaseReference.child("Devices").child(String.valueOf(device.getAddress())).updateChildren(bluMap);
                }
            }
        }};

    public void signout(View view) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {

            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();

            GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(MainActivity.this,gso);
            googleSignInClient.signOut();
            FirebaseAuth.getInstance().signOut();

                    Toast.makeText(MainActivity.this, "Please login!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
                    finishAffinity();

        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Please login!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finishAffinity();
        }
    }
//
}



