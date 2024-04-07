package com.example.donfranorders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);


        // Inicializar FirebaseFirestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button btnOrdenar = findViewById(R.id.btnOrdenar);
        EditText etNumMesa = findViewById(R.id.etNumMesa);

        ImageView ivStar1Fill = findViewById(R.id.ivStarFill1);
        ImageView ivStar2Fill = findViewById(R.id.ivStarFill2);
        ImageView ivStar3Fill = findViewById(R.id.ivStarFill3);
        ImageView ivStar4Fill = findViewById(R.id.ivStarFill4);
        ImageView ivStar5Fill = findViewById(R.id.ivStarFill5);

        ivStar1Fill.setVisibility(View.INVISIBLE);
        ivStar2Fill.setVisibility(View.INVISIBLE);
        ivStar3Fill.setVisibility(View.INVISIBLE);
        ivStar4Fill.setVisibility(View.INVISIBLE);
        ivStar5Fill.setVisibility(View.INVISIBLE);

        ImageView ivImagenProducto = findViewById(R.id.ivImagenProducto);

        TextView tvNombreProducto = findViewById(R.id.tvNombreProducto);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvPrecio = findViewById(R.id.tvPrecio);

        // Obtener datos pasados desde MenuActivity
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        String precio = getIntent().getStringExtra("precio");
        String stars = getIntent().getStringExtra("stars");
        String url = getIntent().getStringExtra("url");

        btnOrdenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombreProducto = tvNombreProducto.getText().toString();
                final String precioProducto = tvPrecio.getText().toString();
                final String numMesa = etNumMesa.getText().toString();
                final String usuario = "Mesero";

                if (!numMesa.isEmpty()){
                    sendOrder(nombreProducto, precioProducto, numMesa, usuario);
                } else{
                    Toast.makeText(ProductoActivity.this, "Ingresa un número de mesa", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (stars.equals("1")){
            ivStar1Fill.setVisibility(View.VISIBLE);
        } else if (stars.equals("2")) {
            ivStar1Fill.setVisibility(View.VISIBLE);
            ivStar2Fill.setVisibility(View.VISIBLE);
        } else if (stars.equals("3")) {
            ivStar1Fill.setVisibility(View.VISIBLE);
            ivStar2Fill.setVisibility(View.VISIBLE);
            ivStar3Fill.setVisibility(View.VISIBLE);
        } else if (stars.equals("4")) {
            ivStar1Fill.setVisibility(View.VISIBLE);
            ivStar2Fill.setVisibility(View.VISIBLE);
            ivStar3Fill.setVisibility(View.VISIBLE);
            ivStar4Fill.setVisibility(View.VISIBLE);
        } else if (stars.equals("5")){
            ivStar1Fill.setVisibility(View.VISIBLE);
            ivStar2Fill.setVisibility(View.VISIBLE);
            ivStar3Fill.setVisibility(View.VISIBLE);
            ivStar4Fill.setVisibility(View.VISIBLE);
            ivStar5Fill.setVisibility(View.VISIBLE);
        }

        // Cargar la información en los TextViews
        tvNombreProducto.setText(nombre);
        tvDescripcion.setText(descripcion);
        tvPrecio.setText(precio);

        // Cargar la imagen utilizando Picasso
        Picasso.get().load(url)
                .into(ivImagenProducto, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        // Manejar errores de carga de imagen
                        Toast.makeText(ProductoActivity.this, "La carga de la imagen ha fallado", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });

    }

    private void sendOrder(String nombre, String precio, String numMesa, String usuario) {
        Log.d("ProductoActivity", "sendOrder: Enviando pedido...");

        // Generar un UUID único para la orden
        String orderId = UUID.randomUUID().toString();

        // Crear un mapa con los datos del pedido
        Map<String, Object> order = new HashMap<>();
        order.put("idpedido", orderId); // Agregar el ID único al mapa
        order.put("nombre", nombre);
        order.put("precio", precio);
        order.put("numMesa", numMesa);
        order.put("usuario", usuario);

        Log.d("ProductoActivity", "sendOrder: Datos del pedido: " + order.toString());

        // Añadir los datos a la colección "orders" en Firestore
        db.collection("orders").add(order)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ProductoActivity", "sendOrder: Pedido enviado correctamente");
                    Toast.makeText(ProductoActivity.this, "Pedido enviado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductoActivity", "sendOrder: Error al enviar pedido", e);
                    Toast.makeText(ProductoActivity.this, "Error al enviar pedido", Toast.LENGTH_SHORT).show();
                });
    }

}
