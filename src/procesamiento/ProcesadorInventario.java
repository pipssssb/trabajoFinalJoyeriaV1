package procesamiento;

import modelo.InventarioJoyeria;

public class ProcesadorInventario implements ProcesadorOperacionesJoyeria {
    private double tasaComision;

    public ProcesadorInventario(double tasaComision) {
        this.tasaComision = tasaComision;
    }

    @Override
    public void agregarInventario(InventarioJoyeria inventario, double valor) {
        double valorNeto = valor * (1 - tasaComision);
        inventario.setValorTotal(inventario.getValorTotal() + valorNeto);
        if (tasaComision > 0) {
            System.out.println("Inventario incrementado: $" + valorNeto + " (comisión: $" + (valor - valorNeto) + ")");
        } else {
            System.out.println("Inventario incrementado: $" + valor);
        }
    }

    @Override
    public void realizarVenta(InventarioJoyeria inventario, double monto) {
        double montoTotal = monto * (1 + tasaComision);
        if (inventario.getValorTotal() >= monto) {
            inventario.setValorTotal(inventario.getValorTotal() - monto);
            if (tasaComision > 0) {
                System.out.println("Venta realizada: $" + monto + " (comisión ganada: $" + (montoTotal - monto) + ")");
            } else {
                System.out.println("Venta realizada: $" + monto);
            }
        } else {
            System.out.println("¡Valor de inventario insuficiente!");
        }
    }

    @Override
    public void comprarAProveedor(InventarioJoyeria inventario, double monto) {
        double montoConComision = monto * (1 + tasaComision);
        inventario.setValorTotal(inventario.getValorTotal() + monto);
        if (tasaComision > 0) {
            System.out.println("Compra a proveedor: $" + monto + " agregado al inventario (costo total con comisión: $" + montoConComision + ")");
        } else {
            System.out.println("Compra a proveedor: $" + monto + " agregado al inventario");
        }
    }
}
