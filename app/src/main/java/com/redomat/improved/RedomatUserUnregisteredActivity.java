package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redomat.improved.databinding.ActivityRedomatUserBinding;

import static com.redomat.improved.pojo.ProgressBar.closeProgressDialog;
import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class RedomatUserUnregisteredActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference redomatCurrentUserRef;
    private DatabaseReference redomatRef;
    private DatabaseReference redomatCurrentPositionRef;
    private DatabaseReference redomatNextPersonTimeRef;

    //ValueEvent initialized to ditach the listener after the user leaves the Redomat
    private ValueEventListener redomatCurrentPositionEventListener;
    //------------------------

    //NotificationManager
    private NotificationManagerCompat notManager;
    //--------------

    //ViewBinding
    private ActivityRedomatUserBinding mBinding;
    //----------------------------------------------------------------

    //MainMenuVariables
    private static String pin;
    private String redomatName;
    private int userRedomatCurrentRedomatPosition;

    //If notification was opened this will be set to true and new notifications will not be sent
    private static boolean notificationOpened;
    //----------------------------------------------------------------

    //RedomatUserVariables
    private TextView rdmaUserTextUserPosition;
    private TextView rdmaUserTextUserPositionValue;
    private int rdmaUserPositionValue;

    private TextView rdmaUserSInfront;
    private TextView rdmaUserSInfrontValue;

    private TextView rdmaUserAvgTime;
    private TextView rdmaUserAvgTimeValue;

    private TextView rdmaUserRedomatStatus;
    private TextView rdmaUserRedomatStatusValue;

    private Button rdmaUserBtnLeave;

    private long rdmaUserCurrentPosition;

    private long avgWaitingTime = 0;
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeProgressDialog();
        showProgressDialog(RedomatUserUnregisteredActivity.this);

        notManager = NotificationManagerCompat.from(this);

        //MainMenuActivity Variables
        pin = getIntent().getStringExtra("pin");
        rdmaUserPositionValue = Integer.valueOf(getIntent().getStringExtra("enteredUserPosition"));
        if(getIntent().getStringExtra("openedThroughNotification") == null){
            notificationOpened = false;
        } else {
            notificationOpened = true;
            Log.d("Ovo", "NOTIFICATION OPENED SET");
        }
        //---------------------------

        destroyRedomatListener();

        //Setting ip DatabaseRefrences
        redomatRef = db.getReference("Redomats").child(pin);
        redomatCurrentPositionRef = redomatRef.child("currentPosition");
        redomatCurrentUserRef = redomatRef.child("redomatLine").child(String.valueOf(rdmaUserPositionValue));
        redomatNextPersonTimeRef = db.getReference("Redomats").child(pin).child("nextPersonTime");
        //-------------------------------------------------------------------------

        mBinding = ActivityRedomatUserBinding.inflate(getLayoutInflater());
        View redomatUserView = mBinding.getRoot();
        setContentView(redomatUserView);
        setRedomatName();

        //Initialize activity stuff
        rdmaUserTextUserPositionValue = mBinding.rdmaUserTextUserPositionValue;
        rdmaUserSInfrontValue = mBinding.rdmaUserSInfrontValue;
        rdmaUserBtnLeave = mBinding.rdmaUserBtnLeave;

        rdmaUserRedomatStatus = mBinding.rdmaUserRedomatStatus;
        rdmaUserRedomatStatusValue= mBinding.rdmaUserRedomatStatusValue;
        rdmaUserAvgTimeValue = mBinding.rdmaUserAvgTimeValue;
        //-----------------------------------

        rdmaUserAvgTimeValue.setText(R.string.rdmaUserRedomatNextPersonTimeDefault);

        rdmaUserTextUserPositionValue.setText(String.valueOf(rdmaUserPositionValue));

        userPositionListener();

        //Update next person time when it's updated on the Firebase
        redomatNextPersonTimeListener();

        //Listen for status changes and apply them
        redomatStatusListener();

        closeProgressDialog();

        rdmaUserBtnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnExitAlertDialog();
            }
        });



    }

    private void userPositionListener(){
        redomatCurrentPositionEventListener = redomatCurrentPositionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userRedomatCurrentRedomatPosition = Integer.valueOf(dataSnapshot.getValue().toString());

                    if(userRedomatCurrentRedomatPosition == rdmaUserPositionValue){
                        if(notificationOpened == false){
                            sendYouAreUpNotification();
                        }
                        rdmaUserSInfrontValue.setText(getString(R.string.notYouAreUpText));
                        rdmaUserAvgTimeValue.setText("---");
                    } else if(userRedomatCurrentRedomatPosition > rdmaUserPositionValue) {
                        finish();
                    } else {
                        rdmaUserSInfrontValue.setText(String.valueOf(rdmaUserPositionValue - userRedomatCurrentRedomatPosition));
                    }
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Set Redomat name as a Action Bar title
    private void setRedomatName(){
        redomatRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                redomatName = dataSnapshot.getValue().toString();
                setTitle(redomatName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        makeAnExitAlertDialog();
    }

    //-----------------------------------------------------------------------------------------------

    //Alert dialog builder for leaving an Redomat
    private void makeAnExitAlertDialog(){
        final AlertDialog leaveAnRedomat = new AlertDialog.Builder(RedomatUserUnregisteredActivity.this)
                .setTitle(getString(R.string.alrtDialogLeaveTitle))
                .setMessage(getString(R.string.alrtDialogSureYouWantToLeave))
                .setPositiveButton(getString(R.string.alrtDialogLeave), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        redomatCurrentPositionRef.removeEventListener(redomatCurrentPositionEventListener);
                        leaveAnRedomatLine();
                    }
                })
                .setNegativeButton(getString(R.string.alrtDialogCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                })
                .show();
    }

    //Method which updates firebase and sets the value that user has left an Redomat
    private void leaveAnRedomatLine(){
        redomatRef.child("currentPosition").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                destroyRedomatListener();

                if(dataSnapshot.exists()){
                    redomatCurrentUserRef.child("status").setValue("inactive");
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Listener which listens for Redomat destruction and it has occured delete any values that were set by this activity, this is here to prevent any junk that can be made due to an async function trigger
    private void destroyRedomatListener(){
        db.getReference("Redomats").child(pin).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    db.getReference("Redomats").child(pin).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Listener for the Redomat status
    private void redomatStatusListener(){
        redomatRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().toString().equals("active")){
                        rdmaUserRedomatStatusValue.setText("Aktivan");
                    } else {
                        rdmaUserRedomatStatusValue.setText("Pauziran do: " + dataSnapshot.getValue().toString());
                        rdmaUserAvgTimeValue.setText("---");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Listener for the user waiting time
    private void redomatNextPersonTimeListener(){
        redomatNextPersonTimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int numOfUsersInfront = rdmaUserPositionValue - userRedomatCurrentRedomatPosition;
                    int avgTime = Integer.valueOf(dataSnapshot.getValue().toString()) * numOfUsersInfront;

                    if(avgTime < 60){
                        rdmaUserAvgTimeValue.setText(getString(R.string.rdmaUserRedomatNextPersonLessThanMinLeft));
                    } else {
                        rdmaUserAvgTimeValue.setText("~" + (avgTime / 60) + " min");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Notification builder
    private void sendYouAreUpNotification(){
        NotificationCompat.Builder not = new NotificationCompat.Builder(this, "Notifikacije")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(redomatName) // title for notification
                .setContentText(getString(R.string.notYouAreUpText))// message for notification
                .setAutoCancel(true) // clear notification after click
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(getApplicationContext(), RedomatUserUnregisteredActivity.class);
        intent.putExtra("enteredUserPosition", String.valueOf(rdmaUserPositionValue));
        intent.putExtra("pin", pin);
        intent.putExtra("openedThroughNotification", "true");
        PendingIntent pi = PendingIntent.getActivity(RedomatUserUnregisteredActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        not.setContentIntent(pi);

        notManager.notify(1, not.build());
    }
}
