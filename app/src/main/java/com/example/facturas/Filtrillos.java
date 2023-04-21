package com.example.facturas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Filtrillos extends AppCompatActivity {
    Context context = this;
    DatePickerDialog datePickerDialog;
    private boolean pagadas = false;
    private boolean anuladas = false;
    private boolean cuotaFija = false;
    private boolean pendientesPago = false;
    private boolean planPago = false;
    private Date fechaInicio = null;
    private Date fechaFin = null;
    private int importeMax = 0;
    private SeekBar importeSeekBar;
    private Activity filtrillosActivity = this;
    private Filtrillos instance = this;
    private int valorActualSeekBar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrillos);
        Button resetFiltrosButton = findViewById(R.id.eliminar);
        resetFiltrosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFiltros();
            }
        });
        MenuHost menu = this;
        menu.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_filtrillos, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.vuelta:
                        Intent intent = new Intent(filtrillosActivity, MainActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });


        TextView valorSeekBar = (TextView) findViewById(R.id.valorSeekBar);
        Button fechaDesde = (Button) findViewById(R.id.fechaDesde);
        Button fechaHasta = (Button) findViewById(R.id.fechaHasta);

        int valorRealMax = MainActivity.maxImporte.intValue()+1;

        importeSeekBar = findViewById(R.id.seekBar);
        importeSeekBar.setMax(valorRealMax);
        importeSeekBar.setProgress(valorRealMax);
        valorSeekBar.setText(String.valueOf(valorRealMax));
        valorActualSeekBar = valorRealMax;



        importeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView importeTextView = findViewById(R.id.valorSeekBar);
                importeTextView.setText(String.valueOf(progress));
                valorActualSeekBar = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        fechaDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(Filtrillos.this, (view, year1, monthofyear, dayofmonth) ->
                        fechaDesde.setText(dayofmonth + "/" + (monthofyear+1) + "/" + year1), year, month, day);
                dpd.show();

            }

        });
        fechaHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(Filtrillos.this, (view, year1, monthofyear, dayofmonth) ->
                        fechaHasta.setText(dayofmonth + "/" + (monthofyear+1) + "/" + year1), year, month, day);
                dpd.show();
            }
        });

        Button botonFiltrar = findViewById(R.id.aplicar);
        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox checkBox2 = (CheckBox) findViewById(R.id.anuladas);
        CheckBox checkBox3 = (CheckBox) findViewById(R.id.cuotaFija);
        CheckBox checkBox4 = (CheckBox) findViewById(R.id.pendientesPago);
        CheckBox checkBox5 = (CheckBox) findViewById(R.id.planPago);
        Button botonDesde = (Button) findViewById(R.id.fechaDesde);
        Button botonHasta = (Button) findViewById(R.id.fechaHasta);

        botonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(instance, MainActivity.class);
                intent.putExtra("importe", (double )valorActualSeekBar);
                intent.putExtra("pagada", checkBox1.isChecked());
                intent.putExtra("anulada", checkBox2.isChecked());
                intent.putExtra("cuotaFija", checkBox3.isChecked());
                intent.putExtra("pendientePago", checkBox4.isChecked());
                intent.putExtra("planPago", checkBox5.isChecked());
                intent.putExtra("fechaDesde", botonDesde.getText().toString());
                intent.putExtra("fechaHasta", botonHasta.getText().toString());
                startActivity(intent);
            }
        });
    }
    private void resetFiltros() {
// Restablecer valores de fecha
        Button fechaDesde = findViewById(R.id.fechaDesde);
        fechaDesde.setText("Dia/Mes/Año");
        Button fechaHasta = findViewById(R.id.fechaHasta);
        fechaHasta.setText("Dia/Mes/Año");

// Restablecer valor de seekBar
        SeekBar seekBar = findViewById(R.id.seekBar);
        int maxImporte = MainActivity.maxImporte.intValue() + 1;
        seekBar.setMax(maxImporte);
        seekBar.setProgress(maxImporte);
        TextView tvValorImporte = findViewById(R.id.valorSeekBar);
        tvValorImporte.setText(String.valueOf(maxImporte));

// Restablecer valores de checkboxes
        CheckBox pagadas = findViewById(R.id.checkBox1);
        pagadas.setChecked(false);
        CheckBox anuladas = findViewById(R.id.planPago);
        anuladas.setChecked(false);
        CheckBox cuotaFija = findViewById(R.id.pendientesPago);
        cuotaFija.setChecked(false);
        CheckBox pendientesPago = findViewById(R.id.anuladas);
        pendientesPago.setChecked(false);
        CheckBox planPago = findViewById(R.id.cuotaFija);
        planPago.setChecked(false);
    }

}