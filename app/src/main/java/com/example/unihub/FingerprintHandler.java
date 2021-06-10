package com.example.unihub;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//

        Toast.makeText(context, Config.FINGERPRINT_AUTHENTICATION_TEXT_ERROR + errString, Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//

    public void onAuthenticationFailed() {
        Toast.makeText(context, Config.FINGERPRINT_AUTHENTICATION_TEXT_FAILED, Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, Config.FINGERPRINT_AUTHENTICATION_TEXT_HELP + helpString, Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        playSoundForSuccessfulFingerprintIdentification();
        Toast.makeText(context, Config.FINGERPRINT_AUTHENTICATION_TEXT_SUCCESS, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    // This AsyncTask will help to play a melody to notify the user that
    // the fingerprint identification worked. The melody plays while you switch from this Activity to the MainActivity.
    public void playSoundForSuccessfulFingerprintIdentification() {

        class FingerPrintSoundTask extends AsyncTask<Context, Void, MediaPlayer> {

            @Override
            protected MediaPlayer doInBackground(Context... context) {
                // First we setup the audioplayer
                MediaPlayer successPlayer = MediaPlayer.create(context[0], R.raw.output_fingerprint_id_success);
                return successPlayer;
            }

            @Override
            protected void onPostExecute(MediaPlayer successPlayer) {
                // onPostExecute will get the an instance of the player ("successPlayer") to play the audio
                successPlayer.start();
            }
        }
        FingerPrintSoundTask deleteEntriesTask = new FingerPrintSoundTask();
        deleteEntriesTask.execute(context);
    }
}