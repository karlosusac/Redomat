package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redomat.improved.databinding.ActivityLoginBinding;
import com.redomat.improved.databinding.ActivityRedomatAdminBinding;
import com.redomat.improved.pojo.AccountLine;
import com.redomat.improved.pojo.Line;
import com.redomat.improved.pojo.ProgressBar;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import static com.redomat.improved.pojo.ProgressBar.closeProgressDialog;
import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class RedomatAdmin extends AppCompatActivity implements PauseDialog.PauseDialogListener {

    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference userRef = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference activeRedomatRef;
    private DatabaseReference activeRedomatLineRef;
    private DatabaseReference activeRedomatLenRef;
    private DatabaseReference activeRedomatCurrentPositionRef;
    private DatabaseReference activeRedomatStatusRef;
    private DatabaseReference getActiveRedomatNextPersonTimeRef;
    private DatabaseReference redomatNextPersonTimeRef;

    private boolean isRedomatLinePaused;
    //------------------------------------------------------------

    //View Binding
    private ActivityRedomatAdminBinding mBinding;
    //------------------------

    //Buttons
    private Button rdmaAdminBtnPause;
    private Button rdmaAdminBtnNext;
    private Button rdmaAdminBtnDestory;
    private Button rdmaAdminBtnAddPerson;
    //----------------------

    //TextView
    private Integer rdmaCurrentPosition;
    private Integer rdmaRedomatLength;
    private Integer nextPersonTime;

    private TextView rdmaAdminCurrentPositionValue;
    private TextView rdmaAdminRedomatLengthValue;
    private TextView rdmaAdminRedomatPinValue;
    //------------------------------

    //MainMenuActivityVariables
    private String pin;
    private Line redomat;
    //---------------------------

    private long avgWaitingTime = 0;
    private boolean redomatHasJustStarted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        closeProgressDialog();
        showProgressDialog(RedomatAdmin.this);

        //MainMenuActivityVariables
        pin = getIntent().getStringExtra("pin");
        redomat = (Line) getIntent().getSerializableExtra("redomat");
        //---------------------------

        //Set Redomat properties (displaying redomat status)
        rdmaCurrentPosition = redomat.getCurrentPosition();
        rdmaRedomatLength = redomat.getRedomatLength();
        nextPersonTime = redomat.getNextPersonTime();
        //-------------------------------------------------

        //Initialize firebase variables
         activeRedomatRef = db.getReference("Redomats").child(pin);
         activeRedomatLineRef = activeRedomatRef.child("redomatLine");
         activeRedomatLenRef = activeRedomatRef.child("redomatLength");
         activeRedomatStatusRef = activeRedomatRef.child("status");
         activeRedomatCurrentPositionRef = activeRedomatRef.child("currentPosition");
        activeRedomatStatusRef = activeRedomatRef.child("status");
        redomatNextPersonTimeRef = db.getReference("Redomats").child(pin).child("nextPersonTime");
        //------------------------------------------------------------

        mBinding = ActivityRedomatAdminBinding.inflate(getLayoutInflater());
        View redomatAdminView = mBinding.getRoot();
        setContentView(redomatAdminView);
        setTitle(redomat.getName());

        //Buttons
        rdmaAdminBtnPause = mBinding.rdmaAdminBtnPause;
        rdmaAdminBtnNext = mBinding.rdmaAdminBtnNext;
        rdmaAdminBtnDestory = mBinding.rdmaAdminBtnDestroy;
        rdmaAdminBtnAddPerson = mBinding.rdmaAdminBtnAddPerson;
        //-----------------------------

        //Text View
        rdmaAdminCurrentPositionValue = mBinding.rdmaAdminCurrentPositionValue;
        rdmaAdminRedomatLengthValue = mBinding.rdmaAdminRedomatLengthValue;
        rdmaAdminRedomatPinValue = mBinding.rdmaAdminRedomatPinValue;
        //------------------------------

        //Settings up starting redomat status info
        rdmaAdminCurrentPositionValue.setText(redomat.getCurrentPosition().toString());
        rdmaAdminRedomatLengthValue.setText(redomat.getRedomatLength().toString());
        rdmaAdminRedomatPinValue.setText(pin);

        //Listen for when new users are added, increment the line len counter
        listenForNewUsers();
        //--------------------------

        //Listen for Redomat status
        listenForRedomatStatusChange();
        //--------------------------

        rdmaAdminBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the RedomatLine is pause and admin clicked 'next' button, unpause the RedomatLine
                if(isRedomatLinePaused == true){
                    pauseAndUnpauseRedomatLine();
                }

                waitingTimeSetter();

                if(redomat.getCurrentPosition() < redomat.getRedomatLength()){
                    if(!(redomat.getRedomatLine().get(redomat.getCurrentPosition() + 1).getStatus().equals("active"))){
                        Integer counterOfInactiveUsers = 0;
                        String lastRedomatUserIsInactiveMessage = "";
                        redomat.incrementRedomatCurrentPosition();

                        while(!(redomat.getRedomatLine().get(redomat.getCurrentPosition()).getStatus().equals("active"))){
                            //If all remaining users have left an Redomat Line stop at the last one, and set the following message
                            if(redomat.getCurrentPosition() == redomat.getRedomatLength()){
                                counterOfInactiveUsers++;
                                lastRedomatUserIsInactiveMessage = ", te je trenutni korisnik također izašao iz reda";
                                break;
                            }
                            counterOfInactiveUsers++;
                            redomat.incrementRedomatCurrentPosition();
                        }

                        if(counterOfInactiveUsers == 1){
                            Toast.makeText(RedomatAdmin.this, "Preskočen je " + counterOfInactiveUsers + " korisnik koji je izašao iz reda" +  lastRedomatUserIsInactiveMessage + ".", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RedomatAdmin.this, "Preskočeno je " + counterOfInactiveUsers + " korisnika koji su izašli iz reda" + lastRedomatUserIsInactiveMessage + ".", Toast.LENGTH_LONG).show();
                        }


                        activeRedomatCurrentPositionRef.setValue(redomat.getCurrentPosition());
                        rdmaAdminCurrentPositionValue.setText(redomat.getCurrentPosition().toString());
                    } else {
                        nextUser();
                    }
                }
            }
        });

        rdmaAdminBtnDestory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog destoryRedomat = new AlertDialog.Builder(RedomatAdmin.this)
                        .setTitle(getString(R.string.rdmaAdminBtnDestroyTitle))
                        .setMessage(getString(R.string.rdmaAdminBtnDestroyMessage))
                        .setPositiveButton(getString(R.string.rdmaAdminBtnDestroy), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                showProgressDialog(RedomatAdmin.this);
                                rdmaAdminBtnPause.setEnabled(false);
                                rdmaAdminBtnNext.setEnabled(false);
                                rdmaAdminBtnDestory.setEnabled(false);
                                destroyRedomat();

                                closeProgressDialog();
                            }
                        })
                        .setNegativeButton(getString(R.string.rdmaAdminBtnDestroyCancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        })
                        .show();
            }
        });


        rdmaAdminBtnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeRedomatLineRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long index = dataSnapshot.getChildrenCount();
                        activeRedomatLineRef.child(String.valueOf(index + 1)).setValue(new AccountLine("AQdHVYgh7tfb81D12Sx986nk4bJ2"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        rdmaAdminBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAndUnpauseRedomatLine();
            }
        });
    }

    public void destroyRedomat(){
        userRef.child("redomat").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                activeRedomatRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });
    }

    private void listenForNewUsers(){
        activeRedomatLineRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    redomat.incrementRedomatLength();
                    activeRedomatLenRef.setValue(redomat.getRedomatLength());

                    List userRedomatInfo = new ArrayList();

                    for(DataSnapshot redomatUsers : dataSnapshot.getChildren()){
                        userRedomatInfo.add(redomatUsers.getValue());
                    }

                    redomat.pushUser(String.valueOf(userRedomatInfo.get(0)), String.valueOf(userRedomatInfo.get(1)));
                    //On added user, update UI to display correct num of users in the Redomat
                    rdmaAdminRedomatLengthValue.setText(String.valueOf(redomat.getRedomatLength()));
                } catch (Exception e){
                    destroyRedomatListener();
                    finish();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String index = dataSnapshot.getKey();

                redomat.inactiveUser(Integer.valueOf(index));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void nextUser(){
        redomat.incrementRedomatCurrentPosition();
        Toast.makeText(RedomatAdmin.this, redomat.getRedomatLine().get(redomat.getCurrentPosition()).getStatus(), Toast.LENGTH_SHORT).show();
        activeRedomatCurrentPositionRef.setValue(redomat.getCurrentPosition());
        rdmaAdminCurrentPositionValue.setText(redomat.getCurrentPosition().toString());
    }

    //If Redomat Line has been pause this method will be triggered, and it updates status
    @Override
    public void pause(String date) {
        activeRedomatStatusRef.setValue(date);
    }

    private void listenForRedomatStatusChange(){
        activeRedomatStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().equals("active")){
                        rdmaAdminBtnPause.setText(getString(R.string.rdmaAdminBtnPause));
                        isRedomatLinePaused = false;
                    } else {
                        rdmaAdminBtnPause.setText(getString(R.string.rdmaAdminBtnActivate));
                        isRedomatLinePaused = true;
                    }

                    //CloseProgressDialog for RedomatAdmin, this is here to hold progressDialog opened untill all data is loaded but I had an
                    // issue where button would update in realtime and this would perhaps cause an error if internet connection was really slow
                    if(ProgressBar.getProgressDialog() != null){
                        closeProgressDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void pauseAndUnpauseRedomatLine(){
        activeRedomatStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("active")){
                    PauseDialog pauseDialog = new PauseDialog();
                    pauseDialog.show(getSupportFragmentManager(), "pause");
                } else {
                    activeRedomatStatusRef.setValue("active");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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

    private void waitingTimeSetter(){
        if(redomatHasJustStarted == true){
            avgWaitingTime = (System.currentTimeMillis() / 1000);
            redomatHasJustStarted = false;
        } else {
            redomatNextPersonTimeRef.setValue(String.valueOf(((System.currentTimeMillis() / 1000) - avgWaitingTime)));
            avgWaitingTime = (System.currentTimeMillis() / 1000);
        }
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
                showProgressDialog(RedomatAdmin.this);

                mAuth = FirebaseAuth.getInstance();
                mAuth.getInstance().signOut();

                Intent i = new Intent(RedomatAdmin.this, LoginActivity.class);
                startActivity(i);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // '\'Options menu -----------------------------------------------------------------------------
}
