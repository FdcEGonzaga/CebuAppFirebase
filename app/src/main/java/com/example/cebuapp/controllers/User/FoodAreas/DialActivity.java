package com.example.cebuapp.controllers.User.FoodAreas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cebuapp.R;

public class DialActivity extends AppCompatActivity {

    private EditText inputNumber;
    private Button callBtn;
    private String number, intentNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        castComponents();


        if (getIntent().getExtras() != null) {
            intentNumber = getIntent().getStringExtra("CONTACTNUMBER");
        } else {
            intentNumber = "";
        }

        if (intentNumber.isEmpty() == false) {
            inputNumber.setText(intentNumber);
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputNumber.getText() != null) {
                    number = inputNumber.getText().toString().trim();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(number)));
                    startActivity(intent);
                } else {
                    Toast.makeText(DialActivity.this, "Please input a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void castComponents() {
        inputNumber = findViewById(R.id.inputNumber);
        callBtn = findViewById(R.id.callBtn);
    }
}