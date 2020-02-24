package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import static com.redomat.improved.pojo.ProgressBar.closeProgressDialog;
import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class RedomatAdmin extends AppCompatActivity {

    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference userRef = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference activeRedomatRef;
    private DatabaseReference activeRedomatLineRef;
    private DatabaseReference activeRedomatLenRef;
    private DatabaseReference activeRedomatCurrentPositionRef;
    private DatabaseReference getActiveRedomatNextPersonTimeRef;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
         activeRedomatCurrentPositionRef = activeRedomatRef.child("currentPosition");
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

        Log.d("Ovo", redomat.getCurrentPosition().toString());


        //Close progress dialog from the main menu, this is here beacuse I can't overlap progress dialogs as they are static properties
        closeProgressDialog();

        //Value Event Listeners

        //Listen for when new users are added, increment the line len counter
        listenForNewUsers();
        //--------------------------

        rdmaAdminBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(redomat.getCurrentPosition() < redomat.getRedomatLength()){
                    redomat.incrementRedomatCurrentPosition();
                    Toast.makeText(RedomatAdmin.this, redomat.getRedomatLine().get(redomat.getCurrentPosition()).getStatus(), Toast.LENGTH_SHORT).show();
                    activeRedomatCurrentPositionRef.setValue(redomat.getCurrentPosition());
                    rdmaAdminCurrentPositionValue.setText(redomat.getCurrentPosition().toString());
                }
            }
        });

        rdmaAdminBtnDestory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog destoryRedomat = new AlertDialog.Builder(RedomatAdmin.this)
                        .setTitle("Uništavanje reda?")
                        .setMessage("Dali ste sigurni da želite uništiti red?\n[Ova akcija se ne može poništiti]")
                        .setPositiveButton("Uništi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog(RedomatAdmin.this);

                                destroyRedomat();

                                closeProgressDialog();
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
                redomat.incrementRedomatLength();
                activeRedomatLenRef.setValue(redomat.getRedomatLength());

                List userRedomatInfo = new ArrayList();

                for(DataSnapshot redomatUsers : dataSnapshot.getChildren()){
                    userRedomatInfo.add(redomatUsers.getValue());
                }

                redomat.pushUser(String.valueOf(userRedomatInfo.get(0)), String.valueOf(userRedomatInfo.get(1)));
                //On added user, update UI to display correct num of users in the Redomat
                rdmaAdminRedomatLengthValue.setText(String.valueOf(redomat.getRedomatLength()));
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
}
