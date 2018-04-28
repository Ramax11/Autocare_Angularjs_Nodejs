package com.autocare.apinfo;

import android.*;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.autocare.apinfo.model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView txt_token;
    Button btn_token;
    EditText edMsg, edNum;
    String mText, mNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edNum = (EditText) findViewById(R.id.input_number);
        edMsg = (EditText) findViewById(R.id.input_message);

        mText = "Hello from Autocare testing";
        mNum = "121";


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.SEND_SMS},1);
            }
        }

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("RegistrationTokens");

        final String token = FirebaseInstanceId.getInstance().getToken();

        final String no, msg;

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);

                Log.d(TAG, "Key: " + key + " Value: " + value);

            }
        }

        txt_token = (TextView)findViewById(R.id.txt_token);
        btn_token = (Button)findViewById(R.id.btn_token);

        btn_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();
                if (token != null) {
                    txt_token.setText(token);
                    Token object = new Token();
                    object.setToken(token);
                    object.setCreated_at(getCurrentTimeStamp());
                    myRef.push().setValue(object);
                    Log.d(TAG, token);
                } else {
                    txt_token.setText("null");
                }

                if (!edMsg.getText().toString().trim().equals("")){
                    mText = edMsg.getText().toString().trim();
                }
                if (!edNum.getText().toString().trim().equals("")){
                    mNum = edNum.getText().toString().trim();
                }

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(mNum, null, mText,null,null);
            }
        });

    }

    public String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());
            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
