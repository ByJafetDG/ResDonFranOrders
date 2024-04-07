package com.example.donfranorders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class TakeOrderActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeorder);

        firestore = FirebaseFirestore.getInstance();

        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnBebidas = findViewById(R.id.btnBebidas);

        btnMenu.setBackgroundColor(Color.parseColor("#EEC800"));

        ScrollView svBebidas = findViewById(R.id.svBebidas);
        ScrollView svMenu = findViewById(R.id.svMenu);

        // Aquí llamamos a un método para cargar las imágenes dinámicamente en cada contenedor
        loadImagesFromFirestore("entradas", R.id.linearlayoutEntradas);
        loadImagesFromFirestore("cortes", R.id.linearlayoutCortes);
        loadImagesFromFirestore("arroces", R.id.linearlayoutArroces);
        loadImagesFromFirestore("hamburguesas", R.id.linearlayoutHamburguesas);
        loadImagesFromFirestore("pastas", R.id.linearlayoutPastas);
        loadImagesFromFirestore("postres", R.id.linearlayoutPostres);
        loadImagesFromFirestore("gaseosas", R.id.linearlayoutGaseosas);
        loadImagesFromFirestore("cervezas", R.id.linearlayoutCervezas);
        loadImagesFromFirestore("naturales", R.id.linearlayoutNaturales);
        loadImagesFromFirestore("calientes", R.id.linearlayoutCalientes);


        btnBebidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hacer visible el ScrollView de Bebidas y ocultar el ScrollView de Menú
                svBebidas.setVisibility(View.VISIBLE);
                svMenu.setVisibility(View.GONE);
                // Cambiar el color de fondo
                btnBebidas.setBackgroundColor(Color.parseColor("#EEC800"));
                btnMenu.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hacer visible el ScrollView de Menú y ocultar el ScrollView de Bebidas
                svMenu.setVisibility(View.VISIBLE);
                svBebidas.setVisibility(View.GONE);
                // Cambiar el color de fondo y el color de borde del botón de Menú al color seleccionado
                btnMenu.setBackgroundColor(Color.parseColor("#EEC800"));
                btnBebidas.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }

    private void loadImagesFromFirestore(String collectionName, int linearLayoutId) {
        LinearLayout linearLayout = findViewById(linearLayoutId);
        firestore.collection(collectionName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String imageUrl = document.getString("url");
                    if (imageUrl != null) {
                        // Creamos un ImageView para mostrar la imagen
                        ImageView imageView = new ImageView(TakeOrderActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(510, 320);
                        layoutParams.setMargins(45, 35, 0, 50);
                        imageView.setLayoutParams(layoutParams);
                        // Cargamos la imagen utilizando Picasso
                        Picasso.get().load(imageUrl).into(imageView);
                        // Hacemos el ImageView clickeable y enviamos los datos del producto a ProductoActivity
                        imageView.setOnClickListener(view -> {
                            String productName = document.getString("nombre");
                            String productDescription = document.getString("descripcion");
                            String productPrice = document.getString("precio");
                            String productStars = document.getString("stars");
                            // Aquí enviamos los datos a ProductoActivity
                            Intent intent = new Intent(TakeOrderActivity.this, ProductoActivity.class);
                            intent.putExtra("nombre", productName);
                            intent.putExtra("descripcion", productDescription);
                            intent.putExtra("precio", productPrice);
                            intent.putExtra("stars", productStars);
                            intent.putExtra("url", imageUrl);
                            startActivity(intent);

                        });
                        // Agregamos el ImageView al LinearLayout
                        linearLayout.addView(imageView);
                    }
                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

}
