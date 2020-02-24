package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.redomat.improved.pojo.Line;

import static com.redomat.improved.pojo.ProgressBar.closeProgressDialog;
import static com.redomat.improved.pojo.ProgressBar.showProgressDialog;

public class MainMenuActivity extends AppCompatActivity implements MakeANewRedomatDialog.MakeANewRedomatDialogListener {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference user = db.getReference("Accounts").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference redomat;
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
    String pin;
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
                startActivity(i);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // '\'Options menu -----------------------------------------------------------------------------


    //CUSTOM METHODS
    @Override
    public void sendRedomatName(String redomatName) {
        showProgressDialog(MainMenuActivity.this);
        String pin = Line.generatePin();

        Line newRedomat = new Line(redomatName, "Active");

        user.child("redomat").setValue(pin);
        redomat = db.getReference("Redomats").child(pin);
        redomat.setValue(newRedomat);


        openRedomatAdmin(pin, newRedomat);
    }

    private void openRedomatAdmin(String pin, Line newRedomat){
        Intent redomatAdminIntent = new Intent(this, RedomatAdmin.class);
        redomatAdminIntent.putExtra("pin", pin);
        redomatAdminIntent.putExtra("redomat", newRedomat);
        startActivity(redomatAdminIntent);
    }
}
