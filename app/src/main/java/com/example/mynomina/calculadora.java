package com.example.mynomina;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class calculadora extends AppCompatActivity {

    // Variables para el EditText del salario y el botón "Siguiente"
    private EditText salaryEditText;
    private Button b2;
    private double salary = 0.0;  // Variable para almacenar el salario ingresado por el usuario
    private int transportationAllowance = 0;  // Variable para almacenar el subsidio de transporte
    // Variables para las horas extras y dominicales/festivas

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "configuraciones", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_share) {
            Toast.makeText(this, "compartir", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            Toast.makeText(this, "Cerrando Sesion", Toast.LENGTH_SHORT).show();
            logOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        // Redirigir a la pantalla de login y limpiar la pila de actividades
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora);

        // Inicializar el EditText y el botón "Siguiente"
        salaryEditText = findViewById(R.id.salaryEditText);
        b2 = findViewById(R.id.btnSiguiente);
        Button b1 = findViewById(R.id.btnCalcular);

        // Configurar el listener para el botón "Siguiente"
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el salario ingresado como texto, eliminando las comas
                String salaryStr = salaryEditText.getText().toString().replace(",", "");
                try {
                    // Convertir el salario a un valor numérico
                    salary = Double.parseDouble(salaryStr);

                    // Calcular el subsidio de transporte basado en el salario
                    if (salary <= 2600000) {
                        transportationAllowance = 162000;
                    } else {
                        transportationAllowance = 0;
                    }

                    // Crear un Intent para la siguiente actividad y pasar los datos
                    Intent intent = new Intent(calculadora.this, calculadora_Final.class);
                    intent.putExtra("salarioUsuario", salary);
                    intent.putExtra("subsidioTransporte", transportationAllowance);

                    startActivity(intent);
                } catch (NumberFormatException e) {
                    Toast.makeText(calculadora.this, "Por favor, ingresa un salario válido.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el listener para el botón "Calcular"
        b1.setOnClickListener(v -> {
            String salaryStr = salaryEditText.getText().toString().replace(",", "");
            try {
                double salary = Double.parseDouble(salaryStr);
                int salaryInt = (int) salary;

                // Calcular el subsidio de transporte basado en el salario
                if (salary > 2600000) {
                    transportationAllowance = 0;
                } else {
                    transportationAllowance = 162000;
                }

                // Calcular el salario quincenal y diario
                int biweeklySalary = salaryInt / 2;
                int dailySalary = salaryInt / 30;

                // Calcular el valor de las horas extras y dominicales/festivas
                double extraDiurna = calcularHoraExtraDiurna(salaryInt);
                double extraNocturna = calcularHoraExtraNocturna(salaryInt);
                double extraDiurnaDominicalFestiva = calcularHoraExtraDiurnaDominicalFestiva(salaryInt);
                double extraNocturnaDominicalFestiva = calcularHoraExtraNocturnaDominicalFestiva(salaryInt);
                double valorDominicalFestivoCompleto = calcularValorDominicalFestivoCompleto(salaryInt);

                // Formatear los valores calculados para mostrarlos en el diálogo
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
                String formattedSalary = numberFormat.format(salary);
                String formattedTransportationAllowance = numberFormat.format(transportationAllowance);
                String formattedBiweeklySalary = numberFormat.format(biweeklySalary);
                String formattedDailySalary = numberFormat.format(dailySalary);
                String formattedExtraDiurna = numberFormat.format(extraDiurna);
                String formattedExtraNocturna = numberFormat.format(extraNocturna);
                String formattedExtraDiurnaDominicalFestiva = numberFormat.format(extraDiurnaDominicalFestiva);
                String formattedExtraNocturnaDominicalFestiva = numberFormat.format(extraNocturnaDominicalFestiva);
                String formattedValorDominicalFestivoCompleto = numberFormat.format(valorDominicalFestivoCompleto);

                // Inflar el diseño del diálogo y configurar su contenido
                LayoutInflater inflater = LayoutInflater.from(calculadora.this);
                View dialogView = inflater.inflate(R.layout.modal_info, null);

                // Obtener referencias a los TextView del diálogo
                TextView tvSalary = dialogView.findViewById(R.id.tvSalary);
                TextView tvTransportationAllowance = dialogView.findViewById(R.id.tvTransportationAllowance);
                TextView tvBiweeklySalary = dialogView.findViewById(R.id.tvBiweeklySalary);
                TextView tvValorDominicalFestivoCompleto = dialogView.findViewById(R.id.tvValorDominicalFestivoCompleto);
                TextView tvDailySalary = dialogView.findViewById(R.id.tvDailySalary);
                TextView tvExtraDiurna = dialogView.findViewById(R.id.tvExtraDiurna);
                TextView tvExtraNocturna = dialogView.findViewById(R.id.tvExtraNocturna);
                TextView tvExtraDiurnaDominicalFestiva = dialogView.findViewById(R.id.tvExtraDiurnaDominicalFestiva);
                TextView tvExtraNocturnaDominicalFestiva = dialogView.findViewById(R.id.tvExtraNocturnaDominicalFestiva);

                // Establecer los valores formateados en los TextView
                tvSalary.setText("Tu Salario: " + formattedSalary);
                tvTransportationAllowance.setText("Auxilio de transporte: " + formattedTransportationAllowance);
                tvBiweeklySalary.setText("Salario quincenal: " + formattedBiweeklySalary);
                tvValorDominicalFestivoCompleto.setText("Valor dominical o festivo completo: " + formattedValorDominicalFestivoCompleto);
                tvDailySalary.setText("Salario diario: " + formattedDailySalary);
                tvExtraDiurna.setText("Hora extra diurna: " + formattedExtraDiurna);
                tvExtraNocturna.setText("Hora extra nocturna: " + formattedExtraNocturna);
                tvExtraDiurnaDominicalFestiva.setText("Hora extra diurna dominical y festiva: " + formattedExtraDiurnaDominicalFestiva);
                tvExtraNocturnaDominicalFestiva.setText("Hora extra nocturna dominical y festiva: " + formattedExtraNocturnaDominicalFestiva);

                // Crear y mostrar el diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(calculadora.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(calculadora.this, "Ingresa un valor para calcular", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar la Toolbar como ActionBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Habilitar EdgeToEdge para la actividad
        EdgeToEdge.enable(this);

        // Aplicar ajustes de WindowInsetsCompat para manejar los insets de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Agregar un TextWatcher al EditText para formatear el salario ingresado
        salaryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementación aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se necesita implementación aquí
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Eliminar el TextWatcher para evitar bucles infinitos
                salaryEditText.removeTextChangedListener(this);
                String salaryStr = s.toString().replace(",", "");
                try {
                    // Convertir el salario a un valor numérico y formatearlo
                    double salary = Double.parseDouble(salaryStr);
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
                    String formattedSalary = decimalFormat.format(salary);
                    // Establecer el salario formateado en el EditText
                    salaryEditText.setText(formattedSalary);
                    salaryEditText.setSelection(formattedSalary.length());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                // Volver a agregar el TextWatcher
                salaryEditText.addTextChangedListener(this);
            }
        });
    }

    // Métodos para calcular las horas extras y dominicales/festivas
    private double calcularHoraExtraDiurna(double salarioMensual) {
        // El salario mensual se divide por 240 para obtener el valor de la hora ordinaria
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 1.25;  // Se aplica el recargo del 25% para horas extras diurnas
    }

    private double calcularHoraExtraNocturna(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 1.75;  // Se aplica el recargo del 75% para horas extras nocturnas
    }

    private double calcularHoraExtraDiurnaDominicalFestiva(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 2;  // Se aplica el recargo del 100% para horas extras diurnas en dominicales y festivos
    }

    private double calcularHoraExtraNocturnaDominicalFestiva(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 2.5;  // Se aplica el recargo del 150% para horas extras nocturnas en dominicales y festivos
    }

    private double calcularValorDominicalFestivoCompleto(double salarioMensual) {
        // El salario diario se obtiene dividiendo el salario mensual por 30
        double valorDiaOrdinario = salarioMensual / 30;
        return valorDiaOrdinario * 1.75;  // Se aplica el recargo del 75% para un día completo dominical o festivo
    }
}
