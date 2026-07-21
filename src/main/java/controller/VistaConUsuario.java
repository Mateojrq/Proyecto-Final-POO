package controller;

import model.Usuario;

/**
 * Contrato que cumple cada controlador de pantalla que necesita saber
 * quien inicio sesion (Tarea, Entrega, Administrador, Profesor, Estudiante).
 * Gracias a esta interfaz, DashboardController puede cargar cualquier
 * subvista sin preguntar de que tipo concreto es (ABSTRACCION + POLIMORFISMO).
 */
public interface VistaConUsuario {
    void setUsuarioActual(Usuario usuario);
}
