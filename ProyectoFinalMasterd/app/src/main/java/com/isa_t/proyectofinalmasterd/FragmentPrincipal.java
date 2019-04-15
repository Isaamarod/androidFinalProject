package com.isa_t.proyectofinalmasterd;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by isa_t on 10/10/2017.
 */

public class FragmentPrincipal extends Fragment {

    public static ArrayList<NotaPOJO> listaNotasPOJO;
    private RecyclerViewGenericAdapter<NotaPOJO> listAdapter;
    public static final int TOMAR_FOTO =1; //CASO EN EL QUE SE TOMA UNA FOTO

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /**
         * Crear bd para que vaya cargado los datos como elementos de la lista
         */

        /*ImageButton agregarNotaButton = view.findViewById(R.id.agregarNota);
        ImageButton  abrirLocalizacion= view.findViewById(R.id.abrirLocalizacion);




        agregarNotaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEditar.notaPojoActual = new NotaPOJO();
                FragmentEditar.modoFragment = 3;
                MainActivity.activity.loadFragment(new FragmentEditar());

            }
        });

        abrirLocalizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.activity.loadFragment(new FragmentMapa());
            }
        });*/

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lista);
        listaNotasPOJO = cargarNotas();
        listAdapter = new RecyclerViewGenericAdapter<NotaPOJO>(getActivity(), listaNotasPOJO, R.layout.lista_row, new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotaPOJO notaPojo = listAdapter.getItem(position);

                SQLiteDatabase database = MainActivity.sqliteHelper.getReadableDatabase();
                String tabla = "NOTAS";
                Cursor selectCursor = database.query(tabla, null, "id = ?", new String[]{Integer.toString(notaPojo.getId())}, null, null, null);
                selectCursor.moveToFirst();
                notaPojo.setImage(Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("foto"), new byte[]{}));
                selectCursor.close();

                FragmentEditar.notaPojoActual = notaPojo;
                //poner variable a 2 y load el fragment editar

                FragmentEditar.modoFragment=1;

                MainActivity.activity.loadFragment(new FragmentEditar());
            }
        }) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                NotaPOJO nota = getItem(position);
                TextView titulo = (TextView) holder.view.findViewById(R.id.titulo);
                TextView fecha = (TextView) holder.view.findViewById(R.id.fecha);
                titulo.setText(nota.getTitulo());
                fecha.setText(nota.getFecha());
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(listAdapter);
    }

    public ArrayList<NotaPOJO> cargarNotas (){
        ArrayList<NotaPOJO> listaNotasPOJO = new ArrayList<>();
        SQLiteDatabase database = MainActivity.sqliteHelper.getReadableDatabase();
        //select * from MiPrimeraTabla
        String tabla = "NOTAS";
        String[] camposConsulta = new String[]{"id", "lat", "long", "fecha" , "descripcion", "titulo"}; //para todos los campos        String where = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = "fecha";
        Cursor selectCursor = database.query(tabla, camposConsulta, "", whereArgs, groupBy, having, orderBy);
        //mueve el cursor al primero y haya la latitud longitus etc si hay sguiente en el cursor sigue
        if (selectCursor.moveToFirst()) {
            do {
                NotaPOJO notaPOJO = new NotaPOJO();
                //selectCursor es una fila completa, ahora tengo que ir cogiendo cada columna de la fila y rellenando los
                int id= Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("id"), 0);
                float lat = Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("lat"), 0f);
                float longi = Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("long"), 0f);
                String fecha = Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("fecha"), "01/01/1970");
                String titulo = Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("titulo"), "Sin Titulo");
                String contenido = Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("descripcion"), "Sin contenido");
                //cargo los datos de cada fila en cada objeto
                notaPOJO.setId(id); //asociare el marker a la nota desde el id
                notaPOJO.setLat(lat);
                notaPOJO.setLon(longi);
                notaPOJO.setFecha(fecha);
                notaPOJO.setTitulo(titulo);
                notaPOJO.setDescripcion(contenido);
                listaNotasPOJO.add(notaPOJO);

            } while (selectCursor.moveToNext());
        }
        return listaNotasPOJO;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            FragmentEditar.notaPojoActual = new NotaPOJO();
            FragmentEditar.modoFragment = 3;
            MainActivity.activity.loadFragment(new FragmentEditar());
            return true;
        }else if (id == R.id.menu_map) {
            MainActivity.activity.loadFragment(new FragmentMapa());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
