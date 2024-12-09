package com.example.mynomina;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrar extends AppCompatActivity {

    Button btnRegistrar, btnLogin;
    EditText txtEmail, txtPassword;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);

        mAuth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.contra);
        btnRegistrar = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegistrar.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(Registrar.this, "Por favor, ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show();
            } else if (!email.contains("@")) {
                Toast.makeText(Registrar.this, "El correo electrónico debe contener @.", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(Registrar.this, "Por favor, ingresa tu contraseña.", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(Registrar.this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            } else if (!hasTwoDigits(password)) {
                Toast.makeText(Registrar.this, "La contraseña debe contener al menos 2 dígitos numéricos.", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(email, password);
            }
        });

        btnLogin.setOnClickListener(v -> {
            Intent irLogin = new Intent(Registrar.this, Login.class);
            startActivity(irLogin);
            finish();
        });
    }

    private boolean hasTwoDigits(String password) {
        Pattern pattern = Pattern.compile("\\d.*\\d");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(Registrar.this, "Registro exitoso. Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registrar.this, calculadora.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(Registrar.this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Registrar.this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
