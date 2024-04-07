package com.example.donfranorders;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TotalActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<String> usuariosList;
    private ListView lvUsers;
    private TextView tvUser;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar variables
        usuariosList = new ArrayList<>();
        lvUsers = findViewById(R.id.lvUsers);
        tvUser = findViewById(R.id.tvUser);
        tvTotal = findViewById(R.id.tvTotal);
        Button btnPagado = findViewById(R.id.btnPagado);

        // Obtener nombres de usuario de la colección "orders" y mostrarlos en el ListView
        obtenerUsuarios();

        // Configurar clics en el ListView
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el nombre de usuario seleccionado
                String usuarioSeleccionado = usuariosList.get(position);

                // Mostrar el nombre de usuario seleccionado en tvUser
                tvUser.setText(usuarioSeleccionado);

                // Calcular y mostrar el total de los pedidos del usuario seleccionado
                calcularTotal(usuarioSeleccionado);
            }
        });

        // Configurar clics en el botón "Pagado"
        btnPagado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre de usuario seleccionado
                String usuarioSeleccionado = tvUser.getText().toString();

                // Verificar si se ha seleccionado un usuario
                if (!usuarioSeleccionado.isEmpty()) {
                    // Eliminar todas las órdenes del usuario seleccionado
                    eliminarOrdenes(usuarioSeleccionado);
                } else {
                    Toast.makeText(TotalActivity.this, "Selecciona un usuario primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void obtenerUsuarios() {
        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> mesasAsignadas = new ArrayList<>(); // Lista para almacenar las mesas asignadas al mesero
                            for (DocumentSnapshot document : task.getResult()) {
                                // Obtener el nombre de usuario de cada documento
                                String usuario = document.getString("usuario");

                                // Verificar si el usuario es Mesero
                                if (usuario.equals("Mesero")) {
                                    // Si es Mesero, obtener el número de mesa
                                    String numMesa = document.getString("numMesa");
                                    if (numMesa != null) {
                                        // Agregar al ArrayList con el formato "Mesero - Mesa: numMesa"
                                        usuariosList.add("Mesero - Mesa: " + numMesa);
                                    }
                                } else {
                                    // Si no es Mesero, agregar el usuario normalmente solo si no está en la lista
                                    if (!usuariosList.contains(usuario)) {
                                        usuariosList.add(usuario);
                                    }
                                }
                            }

                            // Crear un adaptador simple y establecerlo en el ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(TotalActivity.this,
                                    android.R.layout.simple_list_item_1, usuariosList);
                            lvUsers.setAdapter(adapter);
                        } else {
                            Toast.makeText(TotalActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void calcularTotal(String usuario) {
        // Verificar si el usuario es Mesero
        if (usuario.startsWith("Mesero")) {
            // Si es Mesero, extraer el número de mesa
            String numMesa = usuario.split(": ")[1];

            // Calcular el total solo para las órdenes de ese número de mesa
            db.collection("orders")
                    .whereEqualTo("usuario", "Mesero")
                    .whereEqualTo("numMesa", numMesa)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                double total = 0.0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Obtener el precio de cada pedido y sumarlo al total
                                    String precioConColones = document.getString("precio");
                                    String precioSinColones = precioConColones.replaceAll("[₡,]", ""); // Eliminar el símbolo de colones y cualquier coma
                                    total += Double.parseDouble(precioSinColones);
                                }

                                // Formatear el total con separadores de miles y dos decimales
                                DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
                                String totalFormateado = decimalFormat.format(total);

                                // Mostrar el total formateado en tvTotal
                                tvTotal.setText("Total a pagar: ₡" + totalFormateado);
                            } else {
                                Toast.makeText(TotalActivity.this, "Error al calcular el total", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Si no es Mesero, calcular el total normalmente
            db.collection("orders")
                    .whereEqualTo("usuario", usuario)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                double total = 0.0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Obtener el precio de cada pedido y sumarlo al total
                                    String precioConColones = document.getString("precio");
                                    String precioSinColones = precioConColones.replaceAll("[₡,]", ""); // Eliminar el símbolo de colones y cualquier coma
                                    total += Double.parseDouble(precioSinColones);
                                }

                                // Formatear el total con separadores de miles y dos decimales
                                DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
                                String totalFormateado = decimalFormat.format(total);

                                // Mostrar el total formateado en tvTotal
                                tvTotal.setText("Total a pagar: ₡" + totalFormateado);
                            } else {
                                Toast.makeText(TotalActivity.this, "Error al calcular el total", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    // Método para eliminar todas las órdenes del usuario
    private void eliminarOrdenes(String usuario) {
        // Verificar si el usuario es Mesero
        if (usuario.startsWith("Mesero")) {
            // Si es Mesero, extraer el número de mesa
            String numMesa = usuario.split(": ")[1];

            // Eliminar todas las órdenes del usuario Mesero para ese número de mesa
            db.collection("orders")
                    .whereEqualTo("usuario", "Mesero")
                    .whereEqualTo("numMesa", numMesa)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Eliminar cada orden
                                    db.collection("orders")
                                            .document(document.getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(TotalActivity.this, "Todas las órdenes de " + usuario + " para la mesa " + numMesa + " han sido eliminadas", Toast.LENGTH_SHORT).show();
                                                        // Actualizar la lista de usuarios y el total
                                                        obtenerUsuarios();
                                                        tvTotal.setText("Total a pagar: ₡0.00");
                                                    } else {
                                                        Toast.makeText(TotalActivity.this, "Error al eliminar órdenes", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(TotalActivity.this, "Error al obtener órdenes", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Si no es Mesero, eliminar todas las órdenes del usuario normalmente
            db.collection("orders")
                    .whereEqualTo("usuario", usuario)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Eliminar cada orden
                                    db.collection("orders")
                                            .document(document.getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Eliminar el estado de los botones correspondientes
                                                        String idPedido = document.getString("idpedido");
                                                        eliminarEstadoBotones(idPedido);
                                                        Toast.makeText(TotalActivity.this, "Todas las órdenes de " + usuario + " han sido eliminadas", Toast.LENGTH_SHORT).show();
                                                        // Actualizar la lista de usuarios y el total
                                                        obtenerUsuarios();
                                                        tvTotal.setText("Total a pagar: ₡0.00");
                                                    } else {
                                                        Toast.makeText(TotalActivity.this, "Error al eliminar órdenes", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(TotalActivity.this, "Error al obtener órdenes", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Método para eliminar el estado de los botones de la orden
    private void eliminarEstadoBotones(String idPedido) {
        db.collection("buttonStates")
                .document(idPedido)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Estado de los botones eliminado correctamente
                        } else {
                            // Error al eliminar el estado de los botones
                        }
                    }
                });
    }

}

