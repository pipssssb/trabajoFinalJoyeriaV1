package notificaciones;

import modelo.InventarioJoyeria;

public class NotificadorEmail implements Notificador {
    @Override
    public void enviarNotificacion(InventarioJoyeria inventario, String mensaje) {
        System.out.println("Enviando correo a " + inventario.getNombreJoyeria() + ": " + mensaje);
    }
}