package com.cinema.BlackScreen.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;

@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 100, message = "El titulo no puede superar 100 caracteres")
    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @NotBlank(message = "El género es obligatorio")
    @Size(max = 15, message = "El género no puede superar 15 caracteres")
    @Column(name = "genero", nullable = false, length = 15)
    private String genero;

    @NotBlank(message = "La duración es obligatoria")
    @Size(max = 10, message = "La duración no puede superar 10 caracteres")
    @Column(name = "duracion", nullable = false, length = 10)
    private String duracion;

    @NotBlank(message = "La clasificación es obligatoria")
    @Size(max = 5, message = "La clasificación no puede superar 5 caracteres")
    @Column(name = "clasificacion", nullable = false, length = 5)
    private String clasificacion;

    @Size(max = 2000, message = "La sinopsis no puede superar 2000 caracteres")
    @Column(name = "sinopsis", length = 2000)
    private String sinopsis;

    @Size(max = 2048, message = "El enlace del póster no puede superar 2048 caracteres")
    @Column(name = "poster_url", length = 2048)
    private String posterUrl;

    @Size(max = 2048, message = "El enlace del tráiler no puede superar 2048 caracteres")
    @Column(name = "trailer_url", length = 2048)
    private String trailerUrl;

    public Pelicula() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
}
