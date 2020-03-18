package com.redomat.improved;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PauseDialog extends DialogFragment {

    //Date picker
    private DatePicker pauseDialogInputDate;

    //Time picker
    private TimePicker pauseDialogInputTime;

    //Button
    private Button pauseDialogBtnPause;

    //ProgressBar
    private ProgressBar pauseDialogProgressBar;

    //TextView
    private TextView pauseDialogErrorInvalidTimeDate;

    //Dialog listener
    private PauseDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_pause, null);

        builder.setView(view);

        //Inizializing variables - date picker
        pauseDialogInputDate = view.findViewById(R.id.pauseDialogInputDate);

        //Inizializing variables - time picker
        pauseDialogInputTime= view.findViewById(R.id.pauseDialogInputTime);

        //Inizializing variables - button
        pauseDialogBtnPause = view.findViewById(R.id.pauseDialogBtnPause);

        //Inizializing variables - progress bar
        pauseDialogProgressBar = view.findViewById(R.id.pauseDialogProgressBar);
        pauseDialogProgressBar.setVisibility(View.GONE);

        //Inizializing variables - text view
        pauseDialogErrorInvalidTimeDate = view.findViewById(R.id.pauseDialogErrorInvalidTimeDate);
        pauseDialogErrorInvalidTimeDate.setVisibility(view.INVISIBLE);


        //CODE
        builder.setTitle(getString(R.string.pauseDialogTitle))
                .setNegativeButton(getString(R.string.pauseDialogCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Close dialog
                    }
                });

        pauseDialogBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseDialogProgressBar.setVisibility(View.VISIBLE);
                pauseDialogErrorInvalidTimeDate.setVisibility(view.INVISIBLE);

                int day = pauseDialogInputDate.getDayOfMonth();
                int month = pauseDialogInputDate.getMonth();
                int year = pauseDialogInputDate.getYear();

                int hour = pauseDialogInputTime.getCurrentHour();
                int minute = pauseDialogInputTime.getCurrentMinute();

                Calendar timeDateNow = Calendar.getInstance();

                Calendar selectedTimeDate = Calendar.getInstance();
                selectedTimeDate.set(year, month, day, hour, minute);

                if(selectedTimeDate.compareTo(timeDateNow) > 0 || selectedTimeDate.compareTo(timeDateNow) == 0){

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String formatedDate = sdf.format(selectedTimeDate.getTime());

                    listener.pause(formatedDate);
                    dismiss();
                    pauseDialogProgressBar.setVisibility(View.GONE);
                } else {
                    pauseDialogErrorInvalidTimeDate.setVisibility(view.VISIBLE);
                    pauseDialogProgressBar.setVisibility(View.GONE);
                }
            }
        });


        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PauseDialog.PauseDialogListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(context, getString(R.string.pauseDialogError), Toast.LENGTH_SHORT).show();
        }
    }

    //Pause interface that passes date to the RedomatAdmin so pause time can be applied
    public interface PauseDialogListener{
        void pause(String date);
    }
}
