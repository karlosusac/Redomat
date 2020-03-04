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

public class RedomatUserActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference redomatCurrentUserRef;
    private DatabaseReference redomatRef;
    private DatabaseReference redomatCurrentPositionRef;
    private DatabaseReference redomatNextPersonTimeRef;
    private DatabaseReference userAccountRef = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());
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
        showProgressDialog(RedomatUserActivity.this);

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

    //Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_dialog, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.optMenuLogout:
                showProgressDialog(RedomatUserActivity.this);

                mAuth = FirebaseAuth.getInstance();
                mAuth.getInstance().signOut();

                Intent i = new Intent(RedomatUserActivity.this, LoginActivity.class);
                finishAffinity();
                startActivity(i);
                finish();

                return true;

            case R.id.optMenuStatistics:
                StatisticsDialog statDialog = new StatisticsDialog();
                statDialog.show(getSupportFragmentManager(), "stat_dialog");
                return true;

            case R.id.optMenuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // '\'Options menu -----------------------------------------------------------------------------

    private void userPositionListener(){
        redomatCurrentPositionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userRedomatCurrentRedomatPosition = Integer.valueOf(dataSnapshot.getValue().toString());

                    if(userRedomatCurrentRedomatPosition == rdmaUserPositionValue){
                        if(notificationOpened == false){
                            if(readNotificationSettings()){
                                sendYouAreUpNotification();
                            }
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
        final AlertDialog leaveAnRedomat = new AlertDialog.Builder(RedomatUserActivity.this)
                .setTitle("Napustite red?")
                .setMessage("Dali ste sigurni da želite napusiti red?")
                .setPositiveButton("Napusti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveAnRedomatLine();
                    }
                })
                .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
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
                    if(rdmaUserPositionValue > userRedomatCurrentRedomatPosition){
                        incrementNumOfLeftRedomats();
                    }
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

        Intent intent = new Intent(getApplicationContext(), RedomatUserActivity.class);
        intent.putExtra("enteredUserPosition", String.valueOf(rdmaUserPositionValue));
        intent.putExtra("pin", pin);
        intent.putExtra("openedThroughNotification", "true");
        PendingIntent pi = PendingIntent.getActivity(RedomatUserActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        not.setContentIntent(pi);

        notManager.notify(1, not.build());
    }

    //Increment the number of user left Redomats in the Firebase
    private void incrementNumOfLeftRedomats(){
        userAccountRef.child("numOfLeftRedomats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countOfLeftRedomats = Integer.valueOf(String.valueOf(dataSnapshot.getValue())) + 1;
                userAccountRef.child("numOfLeftRedomats").setValue(countOfLeftRedomats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Return true if notifications are turned on
    private boolean readNotificationSettings(){
        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(shrdPref.getBoolean("settingsActivitySwitchNotifications", true) == true){
            return true;
        } else {
            return false;
        }
    }
}
