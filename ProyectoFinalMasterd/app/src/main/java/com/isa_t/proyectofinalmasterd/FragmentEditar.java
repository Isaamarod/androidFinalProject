package com.isa_t.proyectofinalmasterd;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by isa_t on 10/10/2017.
 */

public class FragmentEditar extends Fragment {

    public static NotaPOJO notaPojoActual = null;
    private Geolocation geolocationManager;
    private GoogleMap mMap;
    private Marker lastMarker;
    private int zoom = 16;
    private ImageView foto;
    private EditText titulo;
    private EditText contenido;
    //1 -> VER, 2 -> EDITAR, 3-> NUEVO
    public static Integer modoFragment = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        geolocationManager = new Geolocation(false, FragmentEditar.this.getActivity());
        titulo = (EditText)view.findViewById(R.id.tituloNota);
        contenido = (EditText) view.findViewById(R.id.contenidoNota);
        final ImageButton botonFoto = (ImageButton) view.findViewById(R.id.hacerFoto);
        final ImageButton botonAccion = (ImageButton) view.findViewById(R.id.botonAccion);
        final ImageButton botonLocalizar = (ImageButton) view.findViewById(R.id.buscarLocalizacion);
        final ImageButton botonEliminar = (ImageButton) view.findViewById(R.id.botonEliminar);
        foto = (ImageView) view.findViewById(R.id.foto);


        if (modoFragment != 3)
            rellenarDatos();
        else {
            botonEliminar.setVisibility(View.GONE);
            botonAccion.setImageResource(android.R.drawable.ic_menu_save);
        }

        if (modoFragment == 1)
            toggleReadMode(false);
        else {
            botonFoto.setVisibility(View.VISIBLE);
            botonLocalizar.setVisibility(View.VISIBLE);
        }

        botonLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used
                LatLng posicionActual = geolocationManager.getDevicePositionLatLng();
                notaPojoActual.setLat((float)posicionActual.latitude);
                notaPojoActual.setLon((float)posicionActual.longitude);
                Toast.makeText(getActivity(), "Localización es " +posicionActual.latitude + ", " +posicionActual.longitude, Toast.LENGTH_LONG).show();

            }
        });


        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup().setPickTypes(EPickType.GALLERY, EPickType.CAMERA))
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                if (r.getError() == null) {
                                    Bitmap bm = r.getBitmap();
                                    foto.setVisibility(View.VISIBLE);
                                    foto.setImageBitmap(bm);
                                    notaPojoActual.setImage(Utils.bitmapToByte(bm));
                                } else {
                                    Toast.makeText(getActivity(), "Se ha cancelado la foto", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show(getActivity().getSupportFragmentManager());
            }
        });

        botonAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modoFragment == 1) {
                    //PONER MODO EDITAR
                    toggleReadMode(true);
                    modoFragment = 2;
                    botonFoto.setVisibility(View.VISIBLE);
                    botonLocalizar.setVisibility(View.VISIBLE);
                    botonAccion.setImageResource(android.R.drawable.ic_menu_save);
                } else {
                    //cojo los campos editables y los introduzco en la base de datos
                    String tituloIntroducido = titulo.getText().toString();
                    String textoIntroducido = contenido.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String fechaActual = sdf.format(new Date());

                    notaPojoActual.setFecha(fechaActual);
                    notaPojoActual.setTitulo(tituloIntroducido);
                    notaPojoActual.setDescripcion(textoIntroducido);
                    if(modoFragment == 2)
                        updateNota();
                    else
                        insertaBD();
                    //una vez guardado, voy al fragment principal

                    notaPojoActual = null;
                    MainActivity.activity.loadFragment(new FragmentPrincipal());
                }
            }
        });

        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminaNota();
                notaPojoActual = null;
                MainActivity.activity.loadFragment(new FragmentPrincipal());
            }
        });

    }

    private void insertaBD() {
        //select * from MiPrimeraTabla
        String tabla = "NOTAS";

        //en el onclick necesito capturar la posicion y meterlo en lat y longi
        //Bloque 1 :coger los campos
        Float lat = notaPojoActual.getLat();
        Float longi = notaPojoActual.getLon();
        String fecha = notaPojoActual.getFecha();
        String titulo = notaPojoActual.getTitulo();
        String descripcion = notaPojoActual.getDescripcion();
        byte[] foto = notaPojoActual.getImage();
        //Bloque 2: insertarlos
        SQLiteDatabase database = MainActivity.sqliteHelper.getWritableDatabase();
        //INSERT INTO MiPrimeraTabla (texto) VALUES (textoRecibido)

        String nullColumnHack = null; //como quiero tratar los null a la hora de hacer los insert
        ContentValues valuesToInsert = new ContentValues();
        valuesToInsert.put("lat", lat);
        valuesToInsert.put("long", longi);
        valuesToInsert.put("fecha", fecha);
        valuesToInsert.put("foto", foto);
        valuesToInsert.put("titulo", titulo);
        valuesToInsert.put("descripcion", descripcion);

        database.insert(tabla, nullColumnHack, valuesToInsert);

    }

    private void toggleReadMode(boolean enabled) {
        contenido.setEnabled(enabled);
        titulo.setEnabled(enabled);
    }

    private void rellenarDatos() {
        titulo.setText(notaPojoActual.getTitulo());
        contenido.setText(notaPojoActual.getDescripcion());
        foto.setVisibility(View.VISIBLE);
        if (notaPojoActual.getImage().length > 0)
            foto.setImageBitmap(Utils.byteToBitmap(notaPojoActual.getImage()));
    }

    private void eliminaNota() {
        //todo eliminar nota
        SQLiteDatabase database = MainActivity.sqliteHelper.getWritableDatabase();
        String table = "NOTAS";
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(notaPojoActual.getId())};
        database.delete(table, whereClause, whereArgs);
    }

     private void updateNota(){
         SQLiteDatabase database = MainActivity.sqliteHelper.getWritableDatabase();
         String table = "NOTAS";
         String whereClause = "id = ?";

         Float lat = notaPojoActual.getLat();
         Float longi = notaPojoActual.getLon();
         String fecha = notaPojoActual.getFecha();
         String titulo = notaPojoActual.getTitulo();
         String descripcion = notaPojoActual.getDescripcion();
         byte[] foto = notaPojoActual.getImage();
         //Bloque 2: modificarlos
         //update MiPrimeraTabla  set ... where..

         String nullColumnHack = null; //como quiero tratar los null a la hora de hacer los insert
         ContentValues values = new ContentValues();
         values.put("lat", lat);
         values.put("long", longi);
         values.put("fecha", fecha);
         values.put("foto", foto);
         values.put("titulo", titulo);
         values.put("descripcion", descripcion);


         String[] whereArgs = new String[]{String.valueOf(notaPojoActual.getId())};
         database.update(table,values, whereClause,whereArgs);

     }

}
