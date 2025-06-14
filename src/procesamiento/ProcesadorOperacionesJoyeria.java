package procesamiento;

import modelo.InventarioJoyeria;

public interface ProcesadorOperacionesJoyeria {
    void agregarInventario(InventarioJoyeria inventario, double valor);
    void realizarVenta(InventarioJoyeria inventario, double monto);
    void comprarAProveedor(InventarioJoyeria inventario, double monto);
}