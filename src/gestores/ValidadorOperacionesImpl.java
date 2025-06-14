package gestores;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import entidades.Validador;

interface EstrategiaValidacion {
    boolean validar(Object... parametros);
    String obtenerMensajeError();
}

class ValidacionIngresoInventario implements EstrategiaValidacion {
    private String mensajeError = "";

    @Override
    public boolean validar(Object... parametros) {
        double valor = (Double) parametros[0];

        if (valor <= 0) {
            mensajeError = "El valor a agregar al inventario debe ser mayor a 0";
            return false;
        }
        if (valor > 50000) {
            mensajeError = "Valor máximo por ingreso de inventario: $50,000";
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class ValidacionVenta implements EstrategiaValidacion {
    private static final double INVENTARIO_MINIMO = 5000.0;
    private String mensajeError = "";

    @Override
    public boolean validar(Object... parametros) {
        double monto = (Double) parametros[0];
        double valorInventarioActual = (Double) parametros[1];

        if (monto <= 0) {
            mensajeError = "El monto de venta debe ser mayor a 0";
            return false;
        }
        if (valorInventarioActual - monto < INVENTARIO_MINIMO) {
            mensajeError = "No se puede realizar la venta. Valor mínimo de inventario requerido: $" + INVENTARIO_MINIMO;
            return false;
        }
        if (monto > valorInventarioActual * 0.8) {
            mensajeError = "No se puede vender más del 80% del valor del inventario en una sola transacción";
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class ValidacionCompraProveedor implements EstrategiaValidacion {
    private String mensajeError = "";

    @Override
    public boolean validar(Object... parametros) {
        double monto = (Double) parametros[0];

        if (monto <= 0) {
            mensajeError = "El monto de compra debe ser mayor a 0";
            return false;
        }
        if (monto > 25000) {
            mensajeError = "Monto máximo por compra a proveedor: $25,000";
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class ValidacionHorario implements EstrategiaValidacion {
    private static final int HORA_INICIO = 8;
    private static final int HORA_FIN = 20;
    private String mensajeError = "";

    @Override
    public boolean validar(Object... parametros) {
        int horaActual = LocalDateTime.now().getHour();
        if (horaActual < HORA_INICIO || horaActual >= HORA_FIN) {
            mensajeError = "Operaciones disponibles de " + HORA_INICIO + ":00 a " + HORA_FIN + ":00 (horario comercial)";
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class ValidacionLimiteDiario implements EstrategiaValidacion {
    private static final double LIMITE_DIARIO_JOYERIA = 100000.0;
    private String mensajeError = "";

    @Override
    public boolean validar(Object... parametros) {
        double montoOperacion = (Double) parametros[0];
        double montoOperadoHoy = (Double) parametros[1];

        if (montoOperadoHoy + montoOperacion > LIMITE_DIARIO_JOYERIA) {
            mensajeError = "Límite diario excedido. Límite: $" + LIMITE_DIARIO_JOYERIA +
                    ", Operado hoy: $" + montoOperadoHoy;
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class ValidacionStockMinimo implements EstrategiaValidacion {
    private static final double STOCK_CRITICO = 10000.0;
    private String mensajeError = "";

    @Override
    public boolean validar(Object... parametros) {
        double valorInventarioActual = (Double) parametros[0];
        double montoVenta = parametros.length > 1 ? (Double) parametros[1] : 0.0;

        if (valorInventarioActual - montoVenta < STOCK_CRITICO) {
            mensajeError = "¡ALERTA! El inventario quedará por debajo del nivel crítico de $" + STOCK_CRITICO;
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

public class ValidadorOperacionesImpl implements Validador {
    private Map<String, EstrategiaValidacion> estrategias;
    private String mensajeError = "";

    public ValidadorOperacionesImpl() {
        inicializarEstrategias();
    }

    private void inicializarEstrategias() {
        estrategias = new HashMap<>();

        // Validaciones específicas para joyería
        estrategias.put("INGRESO_INVENTARIO", new ValidacionIngresoInventario());
        estrategias.put("VENTA", new ValidacionVenta());
        estrategias.put("COMPRA_PROVEEDOR", new ValidacionCompraProveedor());
        estrategias.put("HORARIO", new ValidacionHorario());
        estrategias.put("LIMITE_DIARIO", new ValidacionLimiteDiario());
        estrategias.put("STOCK_MINIMO", new ValidacionStockMinimo());
    }

    @Override
    public boolean validar(String tipo, Object... parametros) {
        EstrategiaValidacion estrategia = estrategias.get(tipo.toUpperCase());
        if (estrategia == null) {
            mensajeError = "Tipo de validación no válido para joyería: " + tipo;
            return false;
        }

        boolean resultado = estrategia.validar(parametros);
        if (!resultado) {
            mensajeError = estrategia.obtenerMensajeError();
        }
        return resultado;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }

    public void agregarEstrategiaValidacion(String tipo, EstrategiaValidacion estrategia) {
        estrategias.put(tipo.toUpperCase(), estrategia);
    }

    public java.util.Set<String> obtenerTiposValidacion() {
        return estrategias.keySet();
    }
}
