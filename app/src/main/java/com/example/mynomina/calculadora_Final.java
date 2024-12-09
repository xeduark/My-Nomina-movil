package com.example.mynomina;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.NumberFormat;
import java.util.Locale;

public class calculadora_Final extends AppCompatActivity {
    private RadioGroup radioGroupCheckout;
    private EditText hrsT;
    private EditText dsT;
    private EditText diasSubsidioT;
    private EditText hED, hEF, hEN, hEFN, HNF, HDF;
    private Button b1;
    private double salarioTotalDias, subsidioTransporteDias, salarioHorasTotal, totalHorasExtras, salarioTotalConSubsidio;
    private double salarioBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calculadora_final);

        // Configuración de la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        hED = findViewById(R.id.hrsED);
        hEF = findViewById(R.id.hrsEDF);
        hEN = findViewById(R.id.hrsEN);
        hEFN = findViewById(R.id.hrsEND);
        HNF = findViewById(R.id.hrsND);
        HDF = findViewById(R.id.hrsDD);
        b1 = findViewById(R.id.btnCalcular);

        diasSubsidioT = findViewById(R.id.subTrans);

        Intent intent = getIntent();
        if (intent != null) {
            salarioBase = intent.getDoubleExtra("salarioUsuario", 0.0);
            int transportationAllowance = intent.getIntExtra("subsidioTransporte", 0);

            b1.setOnClickListener(v -> {
                try {
                    int diasTrabajados = Integer.parseInt(dsT.getText().toString());
                    double horasLaboradas = Double.parseDouble(hrsT.getText().toString());
                    int diasSubsidio = Integer.parseInt(diasSubsidioT.getText().toString());

                    double horasDiurnas = hED.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(hED.getText().toString());
                    double horasNocturnas = hEN.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(hEN.getText().toString());
                    double horasFestivasDiurnas = hEF.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(hEF.getText().toString());
                    double horasFestivasNocturnas = hEFN.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(hEFN.getText().toString());
                    double horasNocturnasFestivas = HNF.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(HNF.getText().toString());
                    double horasDiurnasFestivas = HDF.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(HDF.getText().toString());

                    if (diasTrabajados > 0 && diasTrabajados <= 365) {
                        double salarioDiario = salarioBase / 30;
                        salarioTotalDias = salarioDiario * diasTrabajados;
                    } else {
                        Toast.makeText(this, "Por favor ingresa un número válido de días trabajados (entre 1 y 365)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (diasSubsidio > 0 && diasSubsidio <= 30) {
                        if (salarioBase > 2600000) {
                            subsidioTransporteDias = 0;
                        } else {
                            subsidioTransporteDias = (transportationAllowance / 30.0) * diasSubsidio;
                        }
                    } else {
                        Toast.makeText(this, "Por favor ingresa un número válido de días de subsidio (entre 1 y 30)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double salarioPorHora = salarioBase / 240;
                    salarioHorasTotal = salarioPorHora * horasLaboradas;

                    double extraDiurna = calcularHoraExtraDiurna(salarioBase);
                    double extraNocturna = calcularHoraExtraNocturna(salarioBase);
                    double extraDiurnaDominicalFestiva = calcularHoraExtraDiurnaDominicalFestiva(salarioBase);
                    double extraNocturnaDominicalFestiva = calcularHoraExtraNocturnaDominicalFestiva(salarioBase);

                    totalHorasExtras = (horasDiurnas * extraDiurna) +
                            (horasNocturnas * extraNocturna) +
                            (horasFestivasDiurnas * extraDiurnaDominicalFestiva) +
                            (horasFestivasNocturnas * extraNocturnaDominicalFestiva) +
                            (horasNocturnasFestivas * extraNocturnaDominicalFestiva) +
                            (horasDiurnasFestivas * extraDiurnaDominicalFestiva);

                    salarioTotalConSubsidio = salarioTotalDias + salarioHorasTotal + totalHorasExtras + subsidioTransporteDias;

                    mostrarDialogoResultados();

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Por favor ingresa valores numéricos válidos en todos los campos.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Configurar el RadioGroup para el tipo de checkout (horas o días)
        radioGroupCheckout = findViewById(R.id.radioGroupContainer);
        hrsT = findViewById(R.id.HorasLsb);
        dsT = findViewById(R.id.DiasLab);

        radioGroupCheckout.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                RadioButton radioButtonDaysWorked = findViewById(R.id.radioButtonDaysWorked);
                radioButtonDaysWorked.setChecked(true);
            } else {
                RadioButton radioButton = findViewById(checkedId);
                if (radioButton != null) {
                    if (radioButton.getId() == R.id.radioButtonHoursWorked) {
                        hrsT.setVisibility(View.VISIBLE);
                        dsT.setVisibility(View.GONE);
                    } else if (radioButton.getId() == R.id.radioButtonDaysWorked) {
                        dsT.setVisibility(View.VISIBLE);
                        hrsT.setVisibility(View.GONE);
                    }
                }
            }
        });

        RadioButton radioButtonDaysWorked = findViewById(R.id.radioButtonDaysWorked);
        radioButtonDaysWorked.setChecked(true);

        Button btnOtros = findViewById(R.id.btnOtros);
        btnOtros.setOnClickListener(v -> {
            Intent intentOtros = new Intent(this, otrosDevengados.class);
            intentOtros.putExtra("salarioBase", salarioBase);
            intentOtros.putExtra("totalHorasExtras", totalHorasExtras);
            intentOtros.putExtra("totalAPagar", salarioTotalConSubsidio);
            intentOtros.putExtra("subsidioTransporteDias", subsidioTransporteDias);
            startActivity(intentOtros);
        });
    }

    private void mostrarDialogoResultados() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.resultados);

        TextView txtSalarioBase = dialog.findViewById(R.id.txtSalarioBase);
        TextView txtSubsidioTransporte = dialog.findViewById(R.id.txtSubsidioTransporte);
        TextView txtDiasHorasTrabajadas = dialog.findViewById(R.id.txtDiasHorasTrabajadas);
        TextView txtDomingoFestivo = dialog.findViewById(R.id.txtDomingoFestivo);
        TextView txtHorasExtrasDiurnas = dialog.findViewById(R.id.txtHorasExtrasDiurnas);
        TextView txtHorasExtrasDominical = dialog.findViewById(R.id.txtHorasExtrasDominical);
        TextView txtHorasExtrasNocturnas = dialog.findViewById(R.id.txtHorasExtrasNocturnas);
        TextView txtHorasExtrasDominicalNocturna = dialog.findViewById(R.id.txtHorasExtrasDominicalNocturna);
        TextView txtHorasNocturnasDominicalOrdinaria = dialog.findViewById(R.id.txtHorasNocturnasDominicalOrdinaria);
        TextView txtHorasDiurnasDominicalOrdinaria = dialog.findViewById(R.id.txtHorasDiurnasDominicalOrdinaria);
        TextView txtTotalHoras = dialog.findViewById(R.id.txtTotalHoras);
        TextView txtTotalPagar = dialog.findViewById(R.id.txtTotalPagar);

        // Formatear valores en pesos colombianos
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        txtSalarioBase.setText("Salario base: " + formatoMoneda.format(Math.round(salarioBase)));
        txtSubsidioTransporte.setText("Subsidio de transporte: " + formatoMoneda.format(Math.round(subsidioTransporteDias)));
        txtDiasHorasTrabajadas.setText("Horas/Días trabajados: " + (radioGroupCheckout.getCheckedRadioButtonId() == R.id.radioButtonDaysWorked ? dsT.getText().toString() + " días" : hrsT.getText().toString() + " horas"));
        txtDomingoFestivo.setText("Horas Domingo y Festivo: " + Math.round(Double.parseDouble(HDF.getText().toString().isEmpty() ? "0.0" : HDF.getText().toString())) + " horas");
        txtHorasExtrasDiurnas.setText("Horas extras diurnas: " + Math.round(Double.parseDouble(hED.getText().toString().isEmpty() ? "0.0" : hED.getText().toString())) + " horas");
        txtHorasExtrasDominical.setText("Horas extras dominical: " + Math.round(Double.parseDouble(hEF.getText().toString().isEmpty() ? "0.0" : hEF.getText().toString())) + " horas");
        txtHorasExtrasNocturnas.setText("Horas extras nocturnas: " + Math.round(Double.parseDouble(hEN.getText().toString().isEmpty() ? "0.0" : hEN.getText().toString())) + " horas");
        txtHorasExtrasDominicalNocturna.setText("Horas extras dominical nocturna: " + Math.round(Double.parseDouble(hEFN.getText().toString().isEmpty() ? "0.0" : hEFN.getText().toString())) + " horas");
        txtHorasNocturnasDominicalOrdinaria.setText("Horas nocturnas dominical ordinaria: " + Math.round(Double.parseDouble(HNF.getText().toString().isEmpty() ? "0.0" : HNF.getText().toString())) + " horas");
        txtHorasDiurnasDominicalOrdinaria.setText("Horas diurnas dominical ordinaria: " + Math.round(Double.parseDouble(HDF.getText().toString().isEmpty() ? "0.0" : HDF.getText().toString())) + " horas");
        txtTotalHoras.setText("Total horas extras: " + formatoMoneda.format(Math.round(totalHorasExtras)));
        txtTotalPagar.setText("Total a pagar: " + formatoMoneda.format(Math.round(salarioTotalConSubsidio)));

        Button btnCerrar = dialog.findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Configuraciones", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_share) {
            Toast.makeText(this, "Compartir", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            Toast.makeText(this, "Cerrando Sesión", Toast.LENGTH_SHORT).show();
            logOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private double calcularHoraExtraDiurna(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 1.25;
    }

    private double calcularHoraExtraNocturna(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 1.75;
    }

    private double calcularHoraExtraDiurnaDominicalFestiva(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 2;
    }

    private double calcularHoraExtraNocturnaDominicalFestiva(double salarioMensual) {
        double valorHoraOrdinaria = salarioMensual / 240;
        return valorHoraOrdinaria * 2.5;
    }
}
