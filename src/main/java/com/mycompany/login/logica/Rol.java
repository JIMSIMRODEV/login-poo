package com.mycompany.login.logica;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Rol implements Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nombreRoll;
    private String descripcion;
    @OneToMany(mappedBy = "unRol")
    private List<Usuario> listaUsusarios;

    public Rol() {

    }

    public Rol(int id, String nombreRoll, String descripcion, List<Usuario> listaUsusarios) {
        this.id = id;
        this.nombreRoll = nombreRoll;
        this.descripcion = descripcion;
        this.listaUsusarios = listaUsusarios;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreRoll() {
        return this.nombreRoll;
    }

    public void setNombreRoll(String nombreRoll) {
        this.nombreRoll = nombreRoll;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Usuario> getListaUsusarios() {
        return this.listaUsusarios;
    }

    public void setListaUsusarios(List<Usuario> listaUsusarios) {
        this.listaUsusarios = listaUsusarios;
    }

}
