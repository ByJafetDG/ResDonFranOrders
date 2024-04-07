package com.example.donfranorders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setTheme(R.style.Theme_DonFranOrders);
        setContentView(R.layout.activity_main);

        TextView tvTomarOrden = findViewById(R.id.tvTomarOrden);
        tvTomarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakeOrderActivity.class);
                startActivity(intent);
            }
        });

        TextView tvTotal = findViewById(R.id.tvTotalCuenta);
        tvTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TotalActivity.class);
                startActivity(intent);
            }
        });
        // Llamar al método para obtener y mostrar órdenes
        mostrarOrdenes();
    }

    private void mostrarOrdenes() {
        // Acceder a la colección "orders" en Firestore y ordenar por usuario
        db.collection("orders")
                .orderBy("usuario")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Error al escuchar los cambios en la colección 'orders': " + e.getMessage());
                            return;
                        }

                        // Limpiar el contenedor de órdenes antes de actualizarlo
                        LinearLayout layoutPrincipal = findViewById(R.id.layoutPrincipal);
                        layoutPrincipal.removeAllViews();


                        // Inicializar variables para el LinearLayout horizontal actual y el contador de pedidos
                        LinearLayout linearLayoutHorizontal = null;
                        int contador = 0;

                        // Iterar sobre los documentos en la colección
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Obtener datos de cada documento
                            String nombre = documentSnapshot.getString("nombre");
                            String numMesa = documentSnapshot.getString("numMesa");
                            String precio = documentSnapshot.getString("precio");
                            String usuario = documentSnapshot.getString("usuario");
                            String idPedido = documentSnapshot.getString("idpedido");

                            // Si no hay un LinearLayoutHorizontal creado o si ya tiene el máximo de pedidos, crear uno nuevo
                            if (linearLayoutHorizontal == null || contador == 0) {
                                linearLayoutHorizontal = new LinearLayout(MainActivity.this);
                                linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                if (layoutPrincipal.getChildCount() > 0) {
                                    layoutParams.topMargin = -10; // Ajustar el margen superior del LinearLayout horizontal actual
                                }
                                linearLayoutHorizontal.setLayoutParams(layoutParams);
                                layoutPrincipal.addView(linearLayoutHorizontal);
                            }

                            // Llamar al método para crear y mostrar cada orden
                            crearLinearLayout(linearLayoutHorizontal, nombre, numMesa, precio, usuario, idPedido);

                            contador++;

                            // Si ya se han mostrado 4 órdenes, reiniciar el contador
                            if (contador == 4) {
                                contador = 0;
                            }
                        }
                    }
                });
    }

    private void crearLinearLayout(LinearLayout linearLayout, String nombre, String numMesa, String precio, String usuario, String idPedido) {
        // Crear un nuevo LinearLayout vertical para cada orden
        LinearLayout linearLayoutVertical = new LinearLayout(this);
        linearLayoutVertical.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // Añadir márgenes para crear el espaciado entre cada LinearLayout vertical
        layoutParams.setMargins(20, 20, 20, 20);
        linearLayoutVertical.setLayoutParams(layoutParams);

        // Crear TextViews para mostrar los datos de la orden
        TextView tvNombre = new TextView(this);
        tvNombre.setText("Nombre: " + nombre);
        tvNombre.setTextColor(Color.WHITE);
        tvNombre.setTextSize(16); // Tamaño del texto
        tvNombre.setTypeface(Typeface.create("Fenix", Typeface.BOLD)); // Tipo de fuente
        linearLayoutVertical.addView(tvNombre);

        TextView tvNumMesa = new TextView(this);
        tvNumMesa.setText("Mesa: " + numMesa);
        tvNumMesa.setTextColor(Color.WHITE);
        tvNumMesa.setTextSize(16); // Tamaño del texto
        tvNumMesa.setTypeface(Typeface.create("Fenix", Typeface.BOLD)); // Tipo de fuente
        linearLayoutVertical.addView(tvNumMesa);

        TextView tvPrecio = new TextView(this);
        tvPrecio.setText("Precio: " + precio);
        tvPrecio.setTextColor(Color.WHITE);
        tvPrecio.setTextSize(16); // Tamaño del texto
        tvPrecio.setTypeface(Typeface.create("Fenix", Typeface.BOLD)); // Tipo de fuente
        linearLayoutVertical.addView(tvPrecio);

        TextView tvUsuario = new TextView(this);
        tvUsuario.setText("Usuario: " + usuario);
        tvUsuario.setTextColor(Color.WHITE);
        tvUsuario.setTextSize(16); // Tamaño del texto
        tvUsuario.setTypeface(Typeface.create("Fenix", Typeface.BOLD)); // Tipo de fuente
        linearLayoutVertical.addView(tvUsuario);

        // Crear el botón "En proceso"
        Button btnEnProceso = new Button(this);
        btnEnProceso.setText("En proceso");
        btnEnProceso.setBackgroundColor(Color.WHITE);
        btnEnProceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el color actual del botón
                int currentColor = ((ColorDrawable) btnEnProceso.getBackground()).getColor();
                // Cambiar el color del botón
                if (currentColor == Color.WHITE) {
                    // Si el color actual es blanco, cambia a rojo
                    btnEnProceso.setBackgroundColor(Color.parseColor("#FF5733"));
                } else {
                    // Si el color actual es rojo, cambia a blanco
                    btnEnProceso.setBackgroundColor(Color.WHITE);
                }
            }
        });
        linearLayoutVertical.addView(btnEnProceso);

        // Crear el botón "Listo"
        Button btnListo = new Button(this);
        btnListo.setText("Listo");
        btnListo.setBackgroundColor(Color.WHITE);
        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el color actual del botón
                int currentColor = ((ColorDrawable) btnListo.getBackground()).getColor();
                // Cambiar el color del botón
                if (currentColor == Color.WHITE) {
                    // Si el color actual es blanco, cambia a rojo
                    btnListo.setBackgroundColor(Color.GREEN);
                } else {
                    // Si el color actual es rojo, cambia a blanco
                    btnListo.setBackgroundColor(Color.WHITE);
                }
            }
        });
        linearLayoutVertical.addView(btnListo);

        // Crear y agregar el botón "Realizada"
        Button btnRealizada = new Button(this);
        btnRealizada.setText("Entregada");
        btnRealizada.setEnabled(false);
        btnRealizada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarEstadoBoton(idPedido, "Entregada");
                btnEnProceso.setEnabled(false);
                btnListo.setEnabled(false);
                btnListo.setBackgroundColor(Color.WHITE);
                btnRealizada.setBackgroundColor(Color.parseColor("#EEC800"));
            }
        });
        linearLayoutVertical.addView(btnRealizada);

        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarEstadoBoton(idPedido, "Listo");
                // Cambiar el color del fondo del botón Listo a verde
                btnListo.setBackgroundColor(Color.GREEN);
                // Cambiar el color del fondo del botón En proceso a blanco
                btnEnProceso.setBackgroundColor(Color.WHITE);

                btnRealizada.setEnabled(true);
            }
        });

        btnEnProceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarEstadoBoton(idPedido, "En proceso");
                // Cambiar el color del fondo del botón En proceso a rojo
                btnEnProceso.setBackgroundColor(Color.parseColor("#FF5733"));
                // Cambiar el color del fondo del botón Listo a verde
                btnListo.setBackgroundColor(Color.WHITE);

                btnRealizada.setEnabled(false);
            }
        });

        // Obtener el estado del botón desde la base de datos y aplicarlo al botón correspondiente
        db.collection("buttonStates").document(idPedido)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String estado = documentSnapshot.getString("estado");
                            switch (estado) {
                                case "En proceso":
                                    btnEnProceso.setBackgroundColor(Color.parseColor("#FF5733"));
                                    break;
                                case "Listo":
                                    btnListo.setBackgroundColor(Color.GREEN);
                                    break;
                                case "Entregada":
                                    btnRealizada.setBackgroundColor(Color.parseColor("#EEC800"));
                                    break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener el estado del botón para " + idPedido, e);
                    }
                });

        // Añadir márgenes inferiores a todos los TextViews excepto al último
        for (int i = 0; i < linearLayoutVertical.getChildCount() - 1; i++) {
            View childView = linearLayoutVertical.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) childView.getLayoutParams();
            params.bottomMargin = 20; // Ajusta el valor de los márgenes inferiores según tus preferencias
            ((View) childView).setLayoutParams(params);
        }

        // Agregar el LinearLayout vertical al LinearLayout horizontal
        linearLayout.addView(linearLayoutVertical);

    }

    private void guardarEstadoBoton(String idPedido, String estado) {
        // Guardar el estado del botón en la colección "buttonStates"
        db.collection("buttonStates").document(idPedido)
                .set(new HashMap<String, Object>() {{
                    put("estado", estado);
                }})
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Estado del botón guardado correctamente para " + idPedido);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al guardar el estado del botón para " + idPedido, e);
                    }
                });
    }

}