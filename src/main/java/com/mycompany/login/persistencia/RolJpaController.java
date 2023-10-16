package com.mycompany.login.persistencia;

import com.mycompany.login.logica.Rol;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.mycompany.login.logica.Usuario;
import com.mycompany.login.persistencia.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Jimmis J. Simanca
 */
public class RolJpaController implements Serializable {

    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //Administra y crea las entitades
    public RolJpaController() {
        emf = Persistence.createEntityManagerFactory("LoginPU");
    }

    public void create(Rol rol) {
        if (rol.getListaUsusarios() == null) {
            rol.setListaUsusarios(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Usuario> attachedListaUsusarios = new ArrayList<Usuario>();
            for (Usuario listaUsusariosUsuarioToAttach : rol.getListaUsusarios()) {
                listaUsusariosUsuarioToAttach = em.getReference(listaUsusariosUsuarioToAttach.getClass(), listaUsusariosUsuarioToAttach.getId());
                attachedListaUsusarios.add(listaUsusariosUsuarioToAttach);
            }
            rol.setListaUsusarios(attachedListaUsusarios);
            em.persist(rol);
            for (Usuario listaUsusariosUsuario : rol.getListaUsusarios()) {
                Rol oldUnRolOfListaUsusariosUsuario = listaUsusariosUsuario.getUnRol();
                listaUsusariosUsuario.setUnRol(rol);
                listaUsusariosUsuario = em.merge(listaUsusariosUsuario);
                if (oldUnRolOfListaUsusariosUsuario != null) {
                    oldUnRolOfListaUsusariosUsuario.getListaUsusarios().remove(listaUsusariosUsuario);
                    oldUnRolOfListaUsusariosUsuario = em.merge(oldUnRolOfListaUsusariosUsuario);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Rol rol) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol persistentRol = em.find(Rol.class, rol.getId());
            List<Usuario> listaUsusariosOld = persistentRol.getListaUsusarios();
            List<Usuario> listaUsusariosNew = rol.getListaUsusarios();
            List<Usuario> attachedListaUsusariosNew = new ArrayList<Usuario>();
            for (Usuario listaUsusariosNewUsuarioToAttach : listaUsusariosNew) {
                listaUsusariosNewUsuarioToAttach = em.getReference(listaUsusariosNewUsuarioToAttach.getClass(), listaUsusariosNewUsuarioToAttach.getId());
                attachedListaUsusariosNew.add(listaUsusariosNewUsuarioToAttach);
            }
            listaUsusariosNew = attachedListaUsusariosNew;
            rol.setListaUsusarios(listaUsusariosNew);
            rol = em.merge(rol);
            for (Usuario listaUsusariosOldUsuario : listaUsusariosOld) {
                if (!listaUsusariosNew.contains(listaUsusariosOldUsuario)) {
                    listaUsusariosOldUsuario.setUnRol(null);
                    listaUsusariosOldUsuario = em.merge(listaUsusariosOldUsuario);
                }
            }
            for (Usuario listaUsusariosNewUsuario : listaUsusariosNew) {
                if (!listaUsusariosOld.contains(listaUsusariosNewUsuario)) {
                    Rol oldUnRolOfListaUsusariosNewUsuario = listaUsusariosNewUsuario.getUnRol();
                    listaUsusariosNewUsuario.setUnRol(rol);
                    listaUsusariosNewUsuario = em.merge(listaUsusariosNewUsuario);
                    if (oldUnRolOfListaUsusariosNewUsuario != null && !oldUnRolOfListaUsusariosNewUsuario.equals(rol)) {
                        oldUnRolOfListaUsusariosNewUsuario.getListaUsusarios().remove(listaUsusariosNewUsuario);
                        oldUnRolOfListaUsusariosNewUsuario = em.merge(oldUnRolOfListaUsusariosNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = rol.getId();
                if (findRol(id) == null) {
                    throw new NonexistentEntityException("The rol with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Rol rol;
            try {
                rol = em.getReference(Rol.class, id);
                rol.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rol with id " + id + " no longer exists.", enfe);
            }
            List<Usuario> listaUsusarios = rol.getListaUsusarios();
            for (Usuario listaUsusariosUsuario : listaUsusarios) {
                listaUsusariosUsuario.setUnRol(null);
                listaUsusariosUsuario = em.merge(listaUsusariosUsuario);
            }
            em.remove(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(int maxResults, int firstResult) {
        return findRolEntities(false, maxResults, firstResult);
    }

    private List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Rol findRol(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Rol> rt = cq.from(Rol.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
