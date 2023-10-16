package com.mycompany.login.logica;

import com.mycompany.login.persistencia.ControladoraPesistencia;
import java.util.List;

public class Controladora {

    ControladoraPesistencia controlPersis; //= new ControladoraPesistencia();

    public Controladora() {
        controlPersis = new ControladoraPesistencia();

    }

    public Usuario validarUsuario(String usuario, String contrasena) {
        //String mensaje
        Usuario usr = null;
        List<Usuario> listaUsuarios = controlPersis.traerUsuarios();
        for (Usuario usu : listaUsuarios) {
            if (usu.getNombreUsuario().equals(usuario)) {
                if (usu.getContrasenia().equals(contrasena)) {
                    usr = usu;
                    return usr;
                } else {
                    //mensaje = "Contrasena incorreta";
                    usr = null;
                    return usr;
                }

            } else {
                usr = null;
                // mensaje = "Usuario no encotrado";
                return usr;
            }

        }

        return usr;
    }

    public List<Usuario> traerUsuarios() {
        return controlPersis.traerUsuarios();
    }

    public List<Rol> traerRoles() {
        return controlPersis.traerRoles();
    }

    public void crearUsuario(String usuario, String contra, String rolRecibido) {
        Usuario usu = new Usuario();
        usu.setNombreUsuario(usuario);
        usu.setContrasenia(contra);

        Rol rolEncontrado = new Rol();
        rolEncontrado = this.traerRol(rolRecibido);
        if (rolEncontrado != null) {
            usu.setUnRol(rolEncontrado);
        }

        int id = buscarUltimaIdUsuario();
        usu.setId(id + 1);

        controlPersis.crearUsuario(usu);

    }

    private Rol traerRol(String rolRecibido) {
        List<Rol> listaRoles = controlPersis.traerRoles();
        for (Rol rol : listaRoles) {
            if (rol.getNombreRoll().equals(rolRecibido)) {
                return rol;
            }

        }
        return null;
    }

    private int buscarUltimaIdUsuario() {
        List<Usuario> listaUsuarios = this.traerUsuarios();
        Usuario usu = listaUsuarios.get(listaUsuarios.size() - 1);
        return usu.getId();
    }

    public void borrarUsuario(int id_usuario) {
        controlPersis.borrarUsuario(id_usuario);
    }

    public Usuario traerUsuario(int id_usuario) {
        return controlPersis.traerUsuario(id_usuario);
    }

    public void editarUsuario(Usuario usu, String usuario, String contra, String rolRecibido) {
        usu.setNombreUsuario(usuario);
        usu.setContrasenia(contra);

        Rol rolEncotrado = new Rol();
        rolEncotrado = this.traerRol(rolRecibido);
        if (rolEncotrado != null) {
            usu.setUnRol(rolEncotrado);
        }
        controlPersis.editarUsuario(usu);
    }
}
