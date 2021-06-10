package com.example.unihub;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;


public class AppSettings extends AppCompatActivity {


    private Switch fingerprintSwitch;
    private boolean fingerprintEnabled;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // All Activities except the ImageViewer are only made to be used in portrait mode.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.app_settings);
        setupActionBar();
        getFingerprintSettings();
        setupSwitch();
    }

    // This method will save the latest setting of the usage of the fingerprint sensor
    private void getFingerprintSettings() {
        SharedPreferences getFingerprintPreferences = getSharedPreferences(Config.SHARED_PREFS, MODE_PRIVATE);
        fingerprintEnabled = getFingerprintPreferences.getBoolean(Config.FINGERPRINT_SENSOR_ENABLED, false);
    }

    private void setupActionBar() {
        // setDisplayHomeAsUpEnabled(true); will enable the functionality that
        // you will get to the MainActivity if you press the back button in the actionbar,
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Config.FINGERPRINT_ACTIVITY_TITLE);
    }

    private void setupSwitch() {
        fingerprintSwitch = findViewById(R.id.fingerprint_switch);
        setupSwitchPositionBeforeItWasClicked();
        setupSwitchClickListener();
    }

    // This method will check if the device has a fingerprintsensor.
    private boolean checkIfDeviceHasAFingerprintSensor() {
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (!fingerprintManager.isHardwareDetected()) {
            return false;
        }
        return true;
    }

    private void setupSwitchClickListener() {
        fingerprintSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfDeviceHasAFingerprintSensor()) {
                    if (fingerprintEnabled) {
                        turnOffFingerprintSensor();
                    } else {
                        turnOnFingerprintSensor();
                    }
                } else {
                    // The switch can't be changed to checked if the device doesn't have a fingerprint sensor
                    fingerprintSwitch.setChecked(false);
                    notifyUserThatHisDeviceDoesNotHaveAFingerprintSensor();
                }
            }
        });
    }

    private void notifyUserThatHisDeviceDoesNotHaveAFingerprintSensor() {
        Toast noFingerPrintSensorAvailableToast = Toast.makeText(this, Config.TOAST_NO_FINGERPRINT_SENSOR_TEXT, Toast.LENGTH_LONG);
        noFingerPrintSensorAvailableToast.show();
    }

    // turnOffFingerPrintSensor and turnOnFingerprintSensor are both created to
    // change the SharedPreference setting for the usage of the fingerprint sensor
    private void turnOffFingerprintSensor() {
        SharedPreferences fingerprintPreferences = getSharedPreferences(Config.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = fingerprintPreferences.edit();
        editor.putBoolean(Config.FINGERPRINT_SENSOR_ENABLED, false);
        editor.apply();
        fingerprintEnabled = false;
    }

    private void turnOnFingerprintSensor() {
        SharedPreferences fingerprintPreferences = getSharedPreferences(Config.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = fingerprintPreferences.edit();
        editor.putBoolean(Config.FINGERPRINT_SENSOR_ENABLED, true);
        editor.apply();
        fingerprintEnabled = true;
    }

    // This method will check if the switches have to be activated. It will see if the fingerprintSensor is
    // enabled in the settings
    private void setupSwitchPositionBeforeItWasClicked() {
        if (fingerprintEnabled) {
            fingerprintSwitch.setChecked(true);
        } else {
            fingerprintSwitch.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}