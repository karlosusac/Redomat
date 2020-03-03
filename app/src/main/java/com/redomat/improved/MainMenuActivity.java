package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redomat.improved.databinding.ActivityLoginBinding;
import com.redomat.improved.databinding.ActivityMainMenuBinding;
import com.redomat.improved.pojo.AccountLine;
import com.redomat.improved.pojo.Line;

import static com.redomat.improved.pojo.ProgressBar.closeProgressDialog;
import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class MainMenuActivity extends AppCompatActivity implements MakeANewRedomatDialog.MakeANewRedomatDialogListener, EnterNewRedomatDialog.EnterAnNewRedomatListener {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference user = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference redomat;
    private DatabaseReference allRedomatsRef = db.getReference("Redomats");
    private DatabaseReference userMadeRedomatsCount = user.child("numOfMadeRedomats");
    private DatabaseReference userParticipatedRedomatsCount = user.child("numOfParticipatedRedomats");
    private DatabaseReference enterRedomatRef;
    private boolean userRedomatAdmin;

    //View Binding
    private ActivityMainMenuBinding mBiding;
    //---------------------------------

    //Initializing activity variables
    private Button mainMenuBtnNewRedomat;
    private Button mainMenuBtnEnterRedomat;
    //---------------------------------

    //RedomatAdminActivity
    private Line activeRedomat;
    private String pin;
    //---------------------


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Ovo", "onStart triggered");
        showProgressDialog(MainMenuActivity.this);

        user.child("redomat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userRedomatAdmin = true;
                    mainMenuBtnNewRedomat.setText("Nastavite");
                    pin = dataSnapshot.getValue().toString();


                    redomat = db.getReference("Redomats").child(pin);
                    redomat.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            activeRedomat = new Line(dataSnapshot.child("name").getValue().toString(), "active", Integer.valueOf(dataSnapshot.child("currentPosition").getValue().toString()), 0, Integer.valueOf(dataSnapshot.child("nextPersonTime").getValue().toString()));
                            closeProgressDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            closeProgressDialog();
                        }
                    });

                } else {
                    userRedomatAdmin = false;
                    mainMenuBtnNewRedomat.setText(getString(R.string.mainMenuNewRedomatArray));
                    closeProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //CODE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBiding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        View mainMenuView = mBiding.getRoot();
        setContentView(mainMenuView);

        //Button
        mainMenuBtnNewRedomat = mBiding.mainMenuBtnNewRedomat;
        mainMenuBtnEnterRedomat = mBiding.mainMenuBtnEnterRedomat;
        //------------

        mainMenuBtnNewRedomat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userRedomatAdmin){
                    openRedomatAdmin(pin, activeRedomat);
                } else {
                    MakeANewRedomatDialog newRedomatDialog = new MakeANewRedomatDialog();
                    newRedomatDialog.show(getSupportFragmentManager(), "new_redomat");
                }
            }
        });

        mainMenuBtnEnterRedomat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterNewRedomatDialog enterNewRedomatDialog = new EnterNewRedomatDialog();
                enterNewRedomatDialog.show(getSupportFragmentManager(), "enter_redomat");
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
                showProgressDialog(MainMenuActivity.this);

                mAuth = FirebaseAuth.getInstance();
                mAuth.getInstance().signOut();

                Intent i = new Intent(MainMenuActivity.this, LoginActivity.class);
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


    //CUSTOM METHODS

    //MakeANewRedomatDialog will send pin and redomatName here and make new Redomat Line
    @Override
    public void makeNewRedomat(String redomatName) {
        showProgressDialog(MainMenuActivity.this);

        returnUniquePin();

        Line newRedomat = new Line(redomatName, "active");

        user.child("redomat").setValue(pin);
        redomat = db.getReference("Redomats").child(pin);
        redomat.setValue(newRedomat);
        incrementNumOfMadeRedomats();


        openRedomatAdmin(pin, newRedomat);
    }

    //Open RedomatAdminActivity with pin and data, used when creating a new Redomat Line and when continuing Redomat Line
    private void openRedomatAdmin(String pin, Line newRedomat){
        Intent redomatAdminIntent = new Intent(this, RedomatAdmin.class);
        redomatAdminIntent.putExtra("pin", pin);
        redomatAdminIntent.putExtra("redomat", newRedomat);
        startActivity(redomatAdminIntent);
    }

    //When the user creates new Redomat Line take count of Redomats stat on his profile and increment it by 1
    private void incrementNumOfMadeRedomats(){
        userMadeRedomatsCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countOfMadeRedomats = Integer.valueOf(String.valueOf(dataSnapshot.getValue())) + 1;
                userMadeRedomatsCount.setValue(countOfMadeRedomats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void incrementNumOfParticipatedRedomats(){
        userParticipatedRedomatsCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countOfParticipatedRedomats = Integer.valueOf(String.valueOf(dataSnapshot.getValue())) + 1;
                userParticipatedRedomatsCount.setValue(countOfParticipatedRedomats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Generate and set unique pin value for creating a new Redomat Line
    private void returnUniquePin(){
        pin = Line.generatePin();

        allRedomatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                while(dataSnapshot.child(pin).exists()){
                    pin = Line.generatePin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainMenuActivity.this, "Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void enterAnNewRedomat(long myRedomatPosition, String pin) {
        showProgressDialog(MainMenuActivity.this);
        enterRedomatRef = db.getReference("Redomats").child(pin);
        AccountLine newUser = new AccountLine(enterRedomatRef.getKey());

        Intent i = new Intent(this, RedomatUserActivity.class);

        i.putExtra("enteredUserPosition", String.valueOf(myRedomatPosition));
        i.putExtra("pin", pin);
        incrementNumOfParticipatedRedomats();

        startActivity(i);
    }
}
