package com.example.mynomina;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

public class otrosDevengados extends AppCompatActivity {
    private EditText otrosDevengadosT1, otrosDevengadosT2, otrosDevengadosT3;
    private EditText descuentosT;
    private Button b1, b2;
    private double salarioBase;
    private double totalHorasExtras;
    private double salarioTotalConSubsidio;
    private double subsidioTransporteDias;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_otros_devengados);

        // Configuración de la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // Verificar y solicitar permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        // Vinculación de EditText y Button con sus respectivos elementos en el layout
        otrosDevengadosT1 = findViewById(R.id.otrosDevengados1);
        otrosDevengadosT2 = findViewById(R.id.otrosDevengados2);
        otrosDevengadosT3 = findViewById(R.id.otrosDevengados3);
        descuentosT = findViewById(R.id.descuentoNomina);
        b1 = findViewById(R.id.btnCalcular);

        Intent intent = getIntent();
        if (intent != null) {
            // Obtención de los datos pasados desde la actividad anterior
            salarioBase = intent.getDoubleExtra("salarioBase", 0.0);
            totalHorasExtras = intent.getDoubleExtra("totalHorasExtras", 0.0);
            salarioTotalConSubsidio = intent.getDoubleExtra("totalAPagar", 0.0);
            subsidioTransporteDias = intent.getDoubleExtra("subsidioTransporteDias", 0.0);
        }

        // Acción al hacer clic en el botón de calcular
        b1.setOnClickListener(v -> {
            try {
                // Obtención de valores ingresados por el usuario
                double otrosDevengados1 = otrosDevengadosT1.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(otrosDevengadosT1.getText().toString());
                double otrosDevengados2 = otrosDevengadosT2.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(otrosDevengadosT2.getText().toString());
                double otrosDevengados3 = otrosDevengadosT3.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(otrosDevengadosT3.getText().toString());
                double descuentos = descuentosT.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(descuentosT.getText().toString());

                // Cálculos de totales
                double totalOtrosDevengados = otrosDevengados1 + otrosDevengados2 + otrosDevengados3;
                double totalDevengado = salarioTotalConSubsidio + totalOtrosDevengados;

                double deduccionSalud = salarioBase * 0.04;
                double deduccionPension = salarioBase * 0.04;
                double totalDeducciones = deduccionSalud + deduccionPension + descuentos;

                double totalDevengadoConDeducciones = totalDevengado - totalDeducciones;
                double totalAPagarConOtros = totalDevengadoConDeducciones - subsidioTransporteDias; // Restar el subsidio de transporte si aplica

                // Mostrar los resultados en un diálogo
                mostrarDialogoResultados(totalOtrosDevengados, deduccionSalud, deduccionPension, descuentos, totalDevengado, totalDeducciones, totalAPagarConOtros);

                // Acción al hacer clic en el botón de exportar a PDF
                b2.setOnClickListener(v2 -> exportToPdf(
                        String.valueOf(salarioBase),
                        String.valueOf(subsidioTransporteDias),
                        String.valueOf(totalHorasExtras),
                        String.valueOf(totalOtrosDevengados),
                        String.valueOf(deduccionSalud),
                        String.valueOf(deduccionPension),
                        String.valueOf(descuentos),
                        String.valueOf(totalDevengado),
                        String.valueOf(totalDeducciones),
                        String.valueOf(totalAPagarConOtros)
                ));

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor ingresa valores numéricos válidos en todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });

        b2 = findViewById(R.id.btnPdf);
    }

    public void exportToPdf(String salarioBase, String subsidioTransporte, String totalHorasExtras, String totalOtrosDevengados, String deduccionSalud, String deduccionPension, String descuentos, String totalDevengado, String totalDeducciones, String totalAPagar) {
        try {
            // Definir el archivo PDF
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "InformeNomina.pdf");

            OutputStream outputStream = new FileOutputStream(pdfFile);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Añadir título
            document.add(new Paragraph("Informe de Nómina").setFontSize(20).setBold());

            // Crear una tabla
            float[] columnWidths = {1, 3};
            Table table = new Table(columnWidths);

            // Añadir celdas a la tabla
            table.addCell("Salario Base");
            table.addCell(salarioBase);

            table.addCell("Subsidio Transporte");
            table.addCell(subsidioTransporte);

            table.addCell("Total Horas Extras");
            table.addCell(totalHorasExtras);

            table.addCell("Total Otros Devengados");
            table.addCell(totalOtrosDevengados);

            table.addCell("Deducción Salud");
            table.addCell(deduccionSalud);

            table.addCell("Deducción Pensión");
            table.addCell(deduccionPension);

            table.addCell("Descuentos");
            table.addCell(descuentos);

            table.addCell("Total Devengado");
            table.addCell(totalDevengado);

            table.addCell("Total Deducciones");
            table.addCell(totalDeducciones);

            table.addCell("Total a Pagar");
            table.addCell(totalAPagar);

            // Añadir la tabla al documento
            document.add(table);

            // Cerrar el documento
            document.close();

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "PDF exportado correctamente: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al exportar PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Métodos para inflar el menú y manejar las acciones de los elementos del menú
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

    // Método para cerrar sesión
    private void logOut() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogoResultados(double totalOtrosDevengados, double deduccionSalud, double deduccionPension, double descuentos, double totalDevengado, double totalDeducciones, double totalAPagarConOtros) {
        // Inflar la vista del diálogo final
        View dialogView = LayoutInflater.from(this).inflate(R.layout.resultado_total, null);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);

        TextView txtSalarioBase = dialog.findViewById(R.id.txtSalarioBase);
        TextView txtSubsidioTransporte = dialog.findViewById(R.id.txtSubsidioTransporte);
        TextView txtTotalHoras = dialog.findViewById(R.id.txtTotalHoras);
        TextView txtDeven = dialog.findViewById(R.id.Tod);
        TextView txtTotalDeven = dialog.findViewById(R.id.txtTotalDeven);
        TextView txtSalud = dialog.findViewById(R.id.sal);
        TextView txtPension = dialog.findViewById(R.id.pen);
        TextView txtOtrosDesNom = dialog.findViewById(R.id.otrosDesNo);
        TextView txtTotalDes = dialog.findViewById(R.id.txtTotalDes);
        TextView txtTotalDevConDeducciones = dialog.findViewById(R.id.totalDevDec);
        TextView txtTotalPagar = dialog.findViewById(R.id.txtTotalPagar);

        // Formatear valores en pesos colombianos
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        // Calcular el total devengado con deducciones
        double totalDevengadoConDeducciones = totalDevengado - totalDeducciones;

        // Calcular el total a pagar considerando el subsidio de transporte
        double totalAPagar = totalDevengadoConDeducciones;
        if (totalDevengadoConDeducciones > 2600000) {
            totalAPagar -= subsidioTransporteDias;
        }

        // Asignar los valores a los TextViews
        txtSalarioBase.setText("Salario base: " + formatoMoneda.format(Math.round(salarioBase)));
        txtSubsidioTransporte.setText("Subsidio de transporte: " + formatoMoneda.format(Math.round(subsidioTransporteDias)));
        txtTotalHoras.setText("Total horas extras: " + formatoMoneda.format(Math.round(totalHorasExtras)));
        txtDeven.setText("Total Otros Devengados: " + formatoMoneda.format(Math.round(totalOtrosDevengados)));
        txtTotalDeven.setText("Total Devengado: " + formatoMoneda.format(Math.round(totalOtrosDevengados + totalHorasExtras)));
        txtSalud.setText("Salud: " + formatoMoneda.format(Math.round(deduccionSalud)));
        txtPension.setText("Pensión: " + formatoMoneda.format(Math.round(deduccionPension)));
        txtOtrosDesNom.setText("Otros Descuentos por Nomina: " + formatoMoneda.format(Math.round(descuentos)));
        txtTotalDes.setText("Total Deducciones: " + formatoMoneda.format(Math.round(totalDeducciones)));
        txtTotalDevConDeducciones.setText("Total Devengado con Deducciones: " + formatoMoneda.format(Math.round(totalDevengadoConDeducciones)));
        txtTotalPagar.setText("Total a pagar: " + formatoMoneda.format(Math.round(totalAPagar)));

        // Buscar el botón de cerrar y asignarle la acción para cerrar el diálogo
        Button btnCerrar = dialog.findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }


    //PERMISO DE ESCRITURA EN PDF
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de escritura en almacenamiento denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
