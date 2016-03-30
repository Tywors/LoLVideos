package com.tywors.lolvideos.objects;

/**
 * Created by W7 on 18/01/2015.
 */
public class Thumbnail {

    public String getImage_url_1() {
        return image_url_1;
    }

    public void setImage_url_1(String image_url_1) {
        this.image_url_1 = image_url_1;
    }

    public String getImage_url_2() {
        return image_url_2;
    }

    public void setImage_url_2(String image_url_2) {
        this.image_url_2 = image_url_2;
    }

    private String image_url_1;
    private String image_url_2;

    public String getVisita_1() {
        return visita_1;
    }

    public void setVisita_1(String visita_1) {
        this.visita_1 = visita_1;
    }

    public String getFecha_2() {
        return fecha_2;
    }

    public void setFecha_2(String fecha_2) {
        this.fecha_2 = fecha_2;
    }

    public String getFecha_1() {
        return fecha_1;
    }

    public void setFecha_1(String fecha_1) {
        this.fecha_1 = fecha_1;
    }

    public String getRuta_video_2() {
        return ruta_video_2;
    }

    public void setRuta_video_2(String ruta_video_2) {
        this.ruta_video_2 = ruta_video_2;
    }

    public String getRuta_video_1() {
        return ruta_video_1;
    }

    public void setRuta_video_1(String ruta_video_1) {
        this.ruta_video_1 = ruta_video_1;
    }

    public String getVisita_2() {
        return visita_2;
    }

    public void setVisita_2(String visita_2) {
        this.visita_2 = visita_2;
    }

   private String visita_1;
    private String visita_2;
    private String ruta_video_1;
    private String ruta_video_2;
    private String fecha_1;
    private String fecha_2;
    private String nota_1;
    private String nota_2;
    private int favorito_1;
    private int favorito_2;
    private String id_1;
    private String id_2;



    public String getNota_1() {
        return nota_1;
    }

    public void setNota_1(String nota_1) {
        this.nota_1 = nota_1;
    }

    public String getNota_2() {
        return nota_2;
    }

    public void setNota_2(String nota_2) {
        this.nota_2 = nota_2;
    }




    public String getCont_videos_1() {
        return cont_videos_1;
    }

    public void setCont_videos_1(String cont_videos_1) {
        this.cont_videos_1 = cont_videos_1;
    }

    public String getCont_videos_2() {
        return cont_videos_2;
    }

    public void setCont_videos_2(String cont_videos_2) {
        this.cont_videos_2 = cont_videos_2;
    }

    private String cont_videos_1;
    private String cont_videos_2;

    public int getFavorito_2() {
        return favorito_2;
    }

    public void setFavorito_2(int favorito_2) {
        this.favorito_2 = favorito_2;
    }

    public int getFavorito_1() {
        return favorito_1;
    }

    public void setFavorito_1(int favorito_1) {
        this.favorito_1 = favorito_1;
    }



    public String getId_1() {
        return id_1;
    }

    public void setId_1(String id_1) {
        this.id_1 = id_1;
    }

    public String getId_2() {
        return id_2;
    }

    public void setId_2(String id_2) {
        this.id_2 = id_2;
    }




    @Override
    public String toString(){
        return "Movie{" +
                "image_url_1='" + image_url_1 + '\'' +
                ", image_url_2='" + image_url_2 + '\'' +
                '}';

    }
}
