package entidades;

public interface ValidadorOperaciones {
    boolean validarVenta(double monto, double valorInventario);
    boolean validarIngresoInventario(double valor);
    boolean validarHorario();
    boolean validarLimiteDiario(double montoOperacion, double montoVendidoHoy);
    String obtenerMensajeError();
}