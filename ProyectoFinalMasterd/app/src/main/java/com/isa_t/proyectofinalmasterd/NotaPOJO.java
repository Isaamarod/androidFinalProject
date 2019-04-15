package com.isa_t.proyectofinalmasterd;

/**
 * Created by isa_t on 05/10/2017.
 */

public class NotaPOJO{

    private int id;
    private byte[] image;
    private float lat;
    private float lon;
    private String fecha;
    private String descripcion;
    private String titulo;

    public NotaPOJO() {
    }

    public NotaPOJO(int id, byte[] image, float lat, float lon, String fecha, String descripcion, String titulo) {
        this.id = id;
        this.image = image;
        this.lat = lat;
        this.lon = lon;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
