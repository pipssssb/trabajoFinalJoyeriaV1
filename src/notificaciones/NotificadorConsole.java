package notificaciones;

import modelo.InventarioJoyeria;

public class NotificadorConsole implements Notificador {
    @Override
    public void enviarNotificacion(InventarioJoyeria inventario, String mensaje) {
        System.out.println("Notificación para " + inventario.getNombreJoyeria() + ": " + mensaje);
    }
}