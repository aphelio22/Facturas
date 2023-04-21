package com.example.facturas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Facturas> facturas;
    private RequestQueue rq;
    private RecyclerView rv1;
    private AdaptadorFacturas adaptadorFacturas;
    RelativeLayout layout;
    public static Double maxImporte = 0.0;
    private MainActivity instance = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MenuHost menu = this;

        menu.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_ida:
                        Intent intent = new Intent(instance, Filtrillos.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        facturas = new ArrayList<>();

        rq = Volley.newRequestQueue(this);
        cargarFacturas();
        rv1 = findViewById(R.id.rv1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv1.setLayoutManager(linearLayoutManager);
        adaptadorFacturas = new AdaptadorFacturas();
        rv1.setAdapter(adaptadorFacturas);


    }


    private void cargarFacturas() {
        String url = "https://viewnextandroid.wiremockapi.cloud/facturas";
        JsonObjectRequest requerimiento = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String valor = response.get("facturas").toString();
                    JSONArray arreglo = new JSONArray(valor);
                    for (int i = 0; i < arreglo.length(); i++) {
                        JSONObject objeto = arreglo.getJSONObject(i);
                        String estado = objeto.getString("descEstado");
                        String importe = objeto.getString("importeOrdenacion");
                        String fecha = objeto.getString("fecha");
                        Facturas factura = new Facturas(estado, importe, fecha);
                        facturas.add(factura);
                    }
                    maxImporte = Double.valueOf(facturas.stream().max(Comparator.comparing(Facturas::getImporte)).get().getImporte());

                    Bundle extras = getIntent().getExtras();

                    if (extras != null) {
                        ArrayList<Facturas> listFiltro = new ArrayList<>();

                        double importeFiltro = getIntent().getDoubleExtra("importe", (double) maxImporte);

                        for (Facturas factura : facturas) {
                            if (Double.parseDouble(factura.getImporte()) < importeFiltro) {
                                listFiltro.add(factura);
                            }
                        }
                        TextView textView = new TextView(instance);
                        textView.setText("Aquí no hay nada");
                        textView.setTextSize(24);
                        textView.setVisibility(View.INVISIBLE);


                        boolean checkBoxPagadas = getIntent().getBooleanExtra("pagada", false);
                        boolean checkBoxPagadas2 = getIntent().getBooleanExtra("pendientePago", false);
                        boolean checkBoxPagadas3 = getIntent().getBooleanExtra("anulada", false);
                        boolean checkBoxPagadas4 = getIntent().getBooleanExtra("cuotaFija", false);
                        boolean checkBoxPagadas5 = getIntent().getBooleanExtra("planPago", false);
                        //lista solo para checkbox
                        if (checkBoxPagadas || checkBoxPagadas2 || checkBoxPagadas3 || checkBoxPagadas4 || checkBoxPagadas5) {
                            ArrayList<Facturas> listFiltro2 = new ArrayList<>();

                            for (Facturas factura : listFiltro) {
                                if (factura.getEstado().equals("Pagada") && checkBoxPagadas) {
                                    listFiltro2.add(factura);
                                }
                                if (factura.getEstado().equals("Pendiente de pago") && checkBoxPagadas2) {
                                    listFiltro2.add(factura);
                                }
                                if (factura.getEstado().equals("Anuladas") && checkBoxPagadas3) {
                                    listFiltro2.add(factura);
                                }
                                if (factura.getEstado().equals("cuotaFija") && checkBoxPagadas4) {
                                    listFiltro2.add(factura);
                                }
                                if (factura.getEstado().equals("planPago") && checkBoxPagadas5) {
                                    listFiltro2.add(factura);
                                }
                            }
                            listFiltro = listFiltro2;
                        }

                        if (!getIntent().getStringExtra("fechaDesde").equals("Dia/Mes/Año") && !getIntent().getStringExtra("fechaHasta").equals("Dia/Mes/Año")) {
                            ArrayList<Facturas> listFiltro3 = new ArrayList<>();


                            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyyy");
                            Date fechaDesde = null;
                            Date fechaHasta = null;



                            try {
                                fechaDesde = sdf.parse(getIntent().getStringExtra("fechaDesde"));
                                fechaHasta = sdf.parse(getIntent().getStringExtra("fechaHasta"));

                            }catch (ParseException e){
                                e.printStackTrace();
                            }


                            for (Facturas factura : facturas) {
                                Date fechaFactura = sdf.parse(factura.getFecha());
                                if (fechaFactura.after(fechaDesde) && fechaFactura.before(fechaHasta)) {
                                    listFiltro3.add(factura);
                                }

                            }
                            listFiltro = listFiltro3;
                        }


                        if (listFiltro.isEmpty()) {
                            textView.setVisibility(View.VISIBLE);
                            RelativeLayout layout = new RelativeLayout(instance);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                            layout.addView(textView, params);
                            setContentView(layout);
                        }

                        facturas = listFiltro;
                    }

                    adaptadorFacturas.notifyItemRangeInserted(facturas.size(), 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        rq.add(requerimiento);
    }

    private class AdaptadorFacturas extends RecyclerView.Adapter<AdaptadorFacturas.AdaptadorFacturasHolder> {
        @NonNull
        @Override
        public AdaptadorFacturasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdaptadorFacturasHolder(getLayoutInflater().inflate(R.layout.layout_facturas, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorFacturasHolder holder, int position) {
            holder.imprimir(position);
        }

        @Override
        public int getItemCount() {
            return facturas.size();
        }

        class AdaptadorFacturasHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvEstado, tvImporte, tvFecha;
            Dialog mDialog;

            public AdaptadorFacturasHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                tvEstado = itemView.findViewById(R.id.item_estado);
                tvImporte = itemView.findViewById(R.id.item_importe);
                tvFecha = itemView.findViewById(R.id.item_fecha);
                mDialog = new Dialog(itemView.getContext());
            }

            public void imprimir(int position) {
                tvFecha.setText(facturas.get(position).getFecha());
                tvImporte.setText(facturas.get(position).getImporte());
                tvEstado.setText(facturas.get(position).getEstado());
                if (facturas.get(position).getEstado().equals("Pendiente de pago")) {
                    tvEstado.setTextColor(Color.RED);
                } else {
                    tvEstado.setTextColor(Color.BLUE);
                }
            }

            @Override
            public void onClick(View v) {
                mDialog.setContentView(R.layout.popup);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView mensajePopup = mDialog.findViewById(R.id.mensajePopup);
                mensajePopup.setText("Esta funcionalidad aún no está disponible");
                mDialog.show();
                Button cerrarButton = mDialog.findViewById(R.id.botón5);
                cerrarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss(); // Cierra el diálogo al pulsar el botón "Cerrar"
                    }

                });

            }

        }
    }
}