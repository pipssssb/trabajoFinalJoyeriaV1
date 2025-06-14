package notificaciones;

import modelo.InventarioJoyeria;

public class NotificadorSMS implements Notificador {
    @Override
    public void enviarNotificacion(InventarioJoyeria inventario, String mensaje) {
        System.out.println("Enviando SMS a " + inventario.getNombreJoyeria() + ": " + mensaje);
    }
}
