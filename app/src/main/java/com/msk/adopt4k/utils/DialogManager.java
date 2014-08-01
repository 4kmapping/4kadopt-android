package com.msk.adopt4k.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogManager {
    private Context mContext;

    public DialogManager(Context context)
    {
        mContext = context;
    }

    public void showOzAdopted() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Omega Zone already adopted");
        alertDialogBuilder.setPositiveButton("OK, I'll choose another.", dismissListener);
        alertDialogBuilder.show();
    }

    public void showOzLocked() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Omega Zone is locked, Maybe someone else could consider to adopt this...");
        alertDialogBuilder.setPositiveButton("OK, I'll choose another.", dismissListener);
        alertDialogBuilder.show();
    }

    public void showNetworkWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Network not connected");
        alertDialogBuilder.setMessage("Your Device is not connected to any Network, Please check 3G/4G or Wifi status.");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void showRequiredField() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Required");
        alertDialogBuilder.setMessage("Your need to provied which Omega zone world ID you choose");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void showBadWorldID() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Bad World ID");
        alertDialogBuilder.setMessage("World ID that you had provided is not matches to Omega Zone Database");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    private DialogInterface.OnClickListener dismissListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

}
