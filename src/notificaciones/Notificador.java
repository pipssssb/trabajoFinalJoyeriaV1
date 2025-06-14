package notificaciones;

import modelo.InventarioJoyeria;

public interface Notificador {
    void enviarNotificacion(InventarioJoyeria inventario, String mensaje);
}
