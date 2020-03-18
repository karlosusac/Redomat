package com.redomat.improved;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redomat.improved.databinding.DialogEnterAnRedomatBinding;
import com.redomat.improved.pojo.AccountLine;

public class EnterNewRedomatDialog extends DialogFragment {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference redomatsRef = db.getReference("Redomats");
    private DatabaseReference enteringRedomatRef;
    private DatabaseReference enteringRedomatRedomatLineRef;

    private String currentUser;
    //------------------------------------------------------------------------------------

    //View binding
    private DialogEnterAnRedomatBinding entNewRdmBinding;
    //---------------------------------------------------

    //TextInputLayout
    private TextInputLayout enterRedomatPinTxtInputLayout;
    //--------------------------------

    //TextInputEditText
    private TextInputEditText enterRedomatPinValue;
    //--------------------------------

    //Button
    private Button enterRedomatBtnConfirm;
    //--------------------------------

    //ProgressBar
    private ProgressBar enterRedomatProgressBar;
    //--------------------------------

    //Dialog listener
    private EnterAnNewRedomatListener listener;

    //Class helping variables
        //used in a method to check if redomat line exists
        private boolean redomatExists;
        //--------------------------------

        //variables to store pin and basic Redomat Line data such as number of people in the redomat and current Redomat position
        private String pin;

        private long numOfPeopleInRedomat;
        private long myPositionInARedomat;

        private String redomatName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Building an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        entNewRdmBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_enter_an_redomat, null, false);
        builder.setView(entNewRdmBinding.getRoot());

        //-------------------------------------------------------------------------------------

        builder.setTitle(getString(R.string.enterRedomatTitle))
                .setNegativeButton(getString(R.string.enterRedomatCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });

        //TextInputLayout
        enterRedomatPinTxtInputLayout = entNewRdmBinding.enterRedomatInputPin;
        //--------------------------------

        //TextInputEditText
        enterRedomatPinValue = entNewRdmBinding.enterRedomatInputPinValue;
        //--------------------------------

        //Button
        enterRedomatBtnConfirm = entNewRdmBinding.enterRedomatBtnConfirm;
        //--------------------------------

        //ProgressBar
        enterRedomatProgressBar = entNewRdmBinding.enterRedomatProgressBar;
        //--------------------------------

        //Hide progress bar
        enterRedomatProgressBar.setVisibility(View.GONE);


        enterRedomatBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show progress bar and disable button so it can't be spamed
                enterRedomatProgressBar.setVisibility(View.VISIBLE);
                enterRedomatBtnConfirm.setEnabled(false);

                //Unset error is it is set
                enterRedomatPinTxtInputLayout.setError(null);

                pin = String.valueOf(enterRedomatPinValue.getText());


                if(!String.valueOf(enterRedomatPinValue.getText()).isEmpty()){
                    //If entered string is not null search for the pin in the Firebase
                    redomatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(pin).exists()){
                                enteringRedomatRef = db.getReference("Redomats").child(pin);
                                enteringRedomatRedomatLineRef = db.getReference("Redomats").child(pin).child("redomatLine");

                                if(mAuth.getCurrentUser() != null){
                                    currentUser = mAuth.getCurrentUser().getUid();
                                } else {
                                    currentUser = enteringRedomatRef.getKey();
                                }

                                addUserToAnRedomat();
                                dismiss();
                            } else {
                                enterRedomatPinTxtInputLayout.setError(getString(R.string.enterRedomatInputErrorNotExists));
                                enterRedomatProgressBar.setVisibility(View.GONE);
                                enterRedomatBtnConfirm.setEnabled(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError){}
                    });
                } else {
                    enterRedomatPinTxtInputLayout.setError(getString(R.string.enterRedomatInputErrorEmpty));
                    enterRedomatProgressBar.setVisibility(View.GONE);
                    enterRedomatBtnConfirm.setEnabled(true);
                }
            }
        });


        return builder.create();
    }

    //METHODS

    //Add an user to the Redomat Line
    private void addUserToAnRedomat(){
        enteringRedomatRedomatLineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    numOfPeopleInRedomat = dataSnapshot.getChildrenCount();
                    myPositionInARedomat = numOfPeopleInRedomat + 1;
                } else {
                    myPositionInARedomat = 1;
                }

                AccountLine newUser = new AccountLine(currentUser);

                enteringRedomatRedomatLineRef.child(String.valueOf(myPositionInARedomat)).setValue(newUser);

                listener.enterAnNewRedomat(myPositionInARedomat, pin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Interface that sends data from the RedomatDialog to the MainMenuActivity
    public interface EnterAnNewRedomatListener{
        void enterAnNewRedomat(long myRedomatPosition, String pin);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (EnterAnNewRedomatListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(context, getString(R.string.errorHasOccured), Toast.LENGTH_SHORT).show();
        }
    }

    //Set an Redomat name
    private void setRedomatNameVariable(){
        enteringRedomatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                redomatName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
