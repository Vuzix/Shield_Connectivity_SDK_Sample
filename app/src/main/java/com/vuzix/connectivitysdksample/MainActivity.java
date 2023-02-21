package com.vuzix.connectivitysdksample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vuzix.connectivity.sdk.BroadcastResultReceiver;
import com.vuzix.connectivity.sdk.Connectivity;
import com.vuzix.connectivity.sdk.Device;

public class MainActivity extends Activity {

    private static final String ACTION_SEND = "com.vuzix.connectivitysdksample.SEND";
    private static final String ACTION_GET = "com.vuzix.connectivitysdksample.GET";

    private static final String EXTRA_TEXT = "text";

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check for Connectivity framework
        if (!Connectivity.get(this).isAvailable()) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.main);
        mEditText = findViewById(R.id.text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(ACTION_SEND));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    public void sendClicked(View view) {
        Intent sendIntent = new Intent(ACTION_SEND);
        sendIntent.setPackage("com.vuzix.connectivitysdksample");
        sendIntent.putExtra(EXTRA_TEXT, mEditText.getText().toString());
        Connectivity.get(this).sendBroadcast(sendIntent);
        mEditText.setText(null);
    }

    public void getRemoteDeviceModelClicked(View view) {
        Connectivity connectivity = Connectivity.get(this);
        Device device = connectivity.getDevice();
        if (device != null) {
            Intent getIntent = new Intent(ACTION_GET);
            getIntent.setPackage("com.vuzix.connectivitysdksample");
            connectivity.sendOrderedBroadcast(device, getIntent, new BroadcastResultReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String model = getResultData();
                    if (model != null) {
                        Toast.makeText(context, model, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Connectivity.get(context).verify(intent, "com.vuzix.connectivitysdksample")) {
                String text = intent.getStringExtra(EXTRA_TEXT);
                if (text != null) {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
