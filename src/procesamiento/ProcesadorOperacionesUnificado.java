package procesamiento;

import entidades.Procesador;
import modelo.InventarioJoyeria;

public class ProcesadorOperacionesUnificado implements Procesador<InventarioJoyeria> {
    private ProcesadorInventario procesadorInventario;

    @Override
    public void configurar(Object configuracion) {
        if (configuracion instanceof Double) {
            double tasaComision = (Double) configuracion;
            this.procesadorInventario = new ProcesadorInventario(tasaComision);
        }
    }

    @Override
    public void procesar(InventarioJoyeria inventario, String operacion, Object... parametros) {
        if (procesadorInventario == null) {
            System.out.println("Error: Procesador no configurado");
            return;
        }

        double monto = (Double) parametros[0];

        switch (operacion.toUpperCase()) {
            case "AGREGAR_INVENTARIO":
                procesadorInventario.agregarInventario(inventario, monto);
                break;
            case "VENTA":
                procesadorInventario.realizarVenta(inventario, monto);
                break;
            case "COMPRA_PROVEEDOR":
                procesadorInventario.comprarAProveedor(inventario, monto);
                break;
            default:
                System.out.println("Operación no reconocida: " + operacion);
                break;
        }
    }
}
