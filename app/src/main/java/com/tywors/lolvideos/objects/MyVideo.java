package com.tywors.lolvideos.objects;

/**
 * Created by Lenovo on 14/12/2015.
 */
public class MyVideo {
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVisitas() {
        return visitas;
    }

    public void setVisitas(String visitas) {
        this.visitas = visitas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fechas) {
        this.fecha = fechas;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    private String thumbnail;
    private String visitas;
    private String fecha;
    private String activo;
    private String ruta;
    private String id;
    private String favoritos;
    private String nota;
    private String visitas_unicas;

    public String getVotos() {
        return votos;
    }

    public void setVotos(String votos) {
        this.votos = votos;
    }

    private String votos;

    public String getVisitas_unicas() {
        return visitas_unicas;
    }

    public void setVisitas_unicas(String visitas_unicas) {
        this.visitas_unicas = visitas_unicas;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }



    public String getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(String favoritos) {
        this.favoritos = favoritos;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
