package com.example.mynomina;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {
    Button btnIngresar,b1;
    EditText txtUser, txtPassword;
    FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        txtUser = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        btnIngresar = findViewById(R.id.bL1);
        b1 = findViewById(R.id.btnRgr);

        btnIngresar.setOnClickListener(v -> {
            String email = txtUser.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(Login.this, "Ingresar los datos",
                        Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish();
                        startActivity(new Intent(Login.this,calculadora.class));
                        Toast.makeText(Login.this,"BIENVENIDO",Toast.LENGTH_SHORT).show();
                    } else {
// Si el inicio de sesión falla, mostrar un mensaje al usuario
                        Toast.makeText(Login.this, "Error al iniciar sesión: " +
                                Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Login.this,"Error al iniciar sesión: "
                        , Toast.LENGTH_SHORT).show());
        b1.setOnClickListener(v -> {
            Intent irRegistro = new Intent(Login.this, Registrar.class);
            startActivity(irRegistro);
        });
    }
}
