package com.example.mynomina;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Registrar extends AppCompatActivity {

    Button b1;
    EditText T1,T2,T3,T4,T5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);

        b1 = findViewById(R.id.bL1);
        T1 = findViewById(R.id.editTextNumber);
        T2 = findViewById(R.id.editTextText);
        T3 = findViewById(R.id.editTextTextEmailAddress);
        T4 = findViewById(R.id.editTextTextPassword);
        T5 = findViewById(R.id.editTextPhone);

        b1.setOnClickListener(v -> {
            if (T1.getText().toString().isEmpty() || T2.getText().toString().isEmpty()|| T3.getText().toString().isEmpty()|| T4.getText().toString().isEmpty()|| T5.getText().toString().isEmpty()) {
                Toast.makeText(Registrar.this, "Faltan datos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Registrar.this, "Registrado Correctamente", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(Registrar.this, Login.class);
                startActivity(irLogin);
                finish();
            }
        });
    }
}
