package com.example.mikhno_ua.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class objectInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);
        Intent intent = getIntent();
        int ido = intent.getIntExtra("ido",0);

        Toast.makeText(this, String.valueOf(ido), Toast.LENGTH_LONG).show();
    }
}
