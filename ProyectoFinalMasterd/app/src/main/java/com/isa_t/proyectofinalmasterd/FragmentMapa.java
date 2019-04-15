package com.isa_t.proyectofinalmasterd;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by isa_t on 10/10/2017.
 */

public class FragmentMapa extends Fragment {

    private RecyclerViewGenericAdapter<NotaPOJO> listAdapter;
    private GoogleMap mMap;
    private int mapZoom = 13;
    private Geolocation geolocationManager;
    private Marker lastMarker;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //paso las notas cargadas desde el fragment principal
        //por cada nota cojo lat y long y pongo el marker
        //el marker es un image button q al pulsar me abrira la pantalla editae relacionada con esa nota

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                geolocationManager=new Geolocation(false, getActivity());
                mMap = googleMap;

                mMap.getUiSettings().setZoomControlsEnabled(true);

                LatLng posActual = geolocationManager.getDevicePositionLatLng();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(posActual.latitude + 90d / Math.pow(2, mapZoom), posActual.longitude), mapZoom);
                mMap.animateCamera(cameraUpdate);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        NotaPOJO notaPojo = (NotaPOJO)marker.getTag();

                        SQLiteDatabase database = MainActivity.sqliteHelper.getReadableDatabase();
                        String tabla = "NOTAS";
                        Cursor selectCursor = database.query(tabla, null, "id = ?", new String[]{Integer.toString(notaPojo.getId())}, null, null, null);
                        selectCursor.moveToFirst();
                        notaPojo.setImage(Utils.getDatabaseField(selectCursor, selectCursor.getColumnIndex("foto"), new byte[]{}));
                        selectCursor.close();

                        FragmentEditar.notaPojoActual=notaPojo;
                        FragmentEditar.modoFragment = 1;
                        MainActivity.activity.loadFragment(new FragmentEditar());
                        return true;
                    }
                });

                ArrayList<NotaPOJO> notasPojo = FragmentPrincipal.listaNotasPOJO;

                for(NotaPOJO notaPojo: notasPojo){
                    Float latitud = notaPojo.getLat();
                    Float longitud = notaPojo.getLon();
                    String fecha = notaPojo.getFecha();
                    LatLng Posiciones = new LatLng(latitud, longitud);
                    MarkerOptions marker = new MarkerOptions().position(Posiciones).title(fecha);
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                    mMap.addMarker(marker).setTag(notaPojo);

                }
            }
        });
    }
}
