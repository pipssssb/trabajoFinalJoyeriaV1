package modelo;

import entidades.*;
import gestores.*;
import java.util.*;
import java.time.LocalDate;

interface EstrategiaOperacion {
    boolean ejecutar(InventarioJoyeria inventario, Object... parametros);
    String obtenerTipoOperacion();
    String obtenerMensajeError();
}

class OperacionIngresoInventario implements EstrategiaOperacion {
    private String mensajeError = "";

    @Override
    public boolean ejecutar(InventarioJoyeria inventario, Object... parametros) {
        double valor = (Double) parametros[0];
        String pin = (String) parametros[1];

        if (!inventario.autenticar(pin)) {
            mensajeError = "PIN incorrecto";
            return false;
        }

        if (!inventario.getValidador().validar("INGRESO_INVENTARIO", valor) ||
                !inventario.getValidador().validar("HORARIO")) {
            mensajeError = inventario.getValidador().obtenerMensajeError();
            return false;
        }

        double valorAnterior = inventario.getValorTotal();
        inventario.setValorTotal(inventario.getValorTotal() + valor);
        inventario.registrarOperacion("INGRESO_INVENTARIO", valor, "Ingreso de mercadería", valorAnterior);
        System.out.println("Ingreso exitoso. Nuevo valor total: $" + inventario.getValorTotal());
        return true;
    }

    @Override
    public String obtenerTipoOperacion() {
        return "INGRESO_INVENTARIO";
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class OperacionVenta implements EstrategiaOperacion {
    private String mensajeError = "";

    @Override
    public boolean ejecutar(InventarioJoyeria inventario, Object... parametros) {
        double monto = (Double) parametros[0];
        String pin = (String) parametros[1];
        String categoria = parametros.length > 2 ? (String) parametros[2] : null;

        if (!inventario.autenticar(pin)) {
            mensajeError = "PIN incorrecto";
            return false;
        }

        inventario.actualizarVentasDiarias();

        if (!inventario.getValidador().validar("VENTA", monto, inventario.getValorTotal()) ||
                !inventario.getValidador().validar("HORARIO") ||
                !inventario.getValidador().validar("LIMITE_DIARIO", monto, inventario.getVentasDiarias())) {
            mensajeError = inventario.getValidador().obtenerMensajeError();
            return false;
        }

        if (categoria != null && !categoria.isEmpty()) {
            if (!inventario.getGestorCategorias().registrarVenta(categoria, monto)) {
                mensajeError = "Meta de categoría '" + categoria + "' ya alcanzada";
                return false;
            }
        }

        double valorAnterior = inventario.getValorTotal();
        inventario.setValorTotal(inventario.getValorTotal() - monto);
        inventario.setVentasDiarias(inventario.getVentasDiarias() + monto);

        String descripcion = categoria != null ? "Venta - Categoría: " + categoria : "Venta de joya";
        inventario.registrarOperacion("VENTA", monto, descripcion, valorAnterior);
        System.out.println("Venta exitosa. Nuevo valor total: $" + inventario.getValorTotal());
        return true;
    }

    @Override
    public String obtenerTipoOperacion() {
        return "VENTA";
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class OperacionCompraProveedor implements EstrategiaOperacion {
    private String mensajeError = "";

    @Override
    public boolean ejecutar(InventarioJoyeria inventario, Object... parametros) {
        String alias = (String) parametros[0];
        double monto = (Double) parametros[1];
        String pin = (String) parametros[2];

        if (!inventario.autenticar(pin)) {
            mensajeError = "PIN incorrecto";
            return false;
        }

        ProveedorFavorito proveedor = inventario.getGestorProveedores().buscarPorAlias(alias);
        if (proveedor == null) {
            mensajeError = "No se encontró el proveedor favorito: " + alias;
            return false;
        }

        if (!inventario.getValidador().validar("COMPRA_PROVEEDOR", monto) ||
                !inventario.getValidador().validar("HORARIO")) {
            mensajeError = inventario.getValidador().obtenerMensajeError();
            return false;
        }

        double valorAnterior = inventario.getValorTotal();
        inventario.setValorTotal(inventario.getValorTotal() + monto);

        String descripcion = "Compra a " + proveedor.getNombre() + " (" + proveedor.getTipoProveedor() + ")";
        inventario.registrarOperacion("COMPRA_PROVEEDOR", monto, descripcion, valorAnterior);
        System.out.println("Compra exitosa a " + proveedor.getNombre());
        return true;
    }

    @Override
    public String obtenerTipoOperacion() {
        return "COMPRA_PROVEEDOR";
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

interface FactoryGestores {
    GestorProveedores crearGestorProveedores();
    GestorCategoriasJoyas crearGestorCategorias();
    Validador crearValidador();
}

class FactoryGestoresDefault implements FactoryGestores {
    @Override
    public GestorProveedores crearGestorProveedores() {
        return new GestorProveedores();
    }

    @Override
    public GestorCategoriasJoyas crearGestorCategorias() {
        return new GestorCategoriasJoyas();
    }

    @Override
    public Validador crearValidador() {
        return new ValidadorOperacionesImpl();
    }
}

public class InventarioJoyeria {
    private String codigoInventario;
    private String nombreJoyeria;
    private double valorTotal;
    private ArrayList<Operacion> historialOperaciones;
    private GestorProveedores gestorProveedores;
    private GestorCategoriasJoyas gestorCategorias;
    private Gerente gerente;
    private Validador validador;
    private double ventasDiarias;
    private LocalDate fechaUltimasVentas;
    private Map<String, EstrategiaOperacion> operaciones;

    public InventarioJoyeria(String codigoInventario, String nombreJoyeria, double valorInicial, String pin) {
        this(codigoInventario, nombreJoyeria, valorInicial, pin, new FactoryGestoresDefault());
    }

    public InventarioJoyeria(String codigoInventario, String nombreJoyeria, double valorInicial, String pin, FactoryGestores factory) {
        this.codigoInventario = codigoInventario;
        this.nombreJoyeria = nombreJoyeria;
        this.valorTotal = valorInicial;
        this.historialOperaciones = new ArrayList<>();
        this.gestorProveedores = factory.crearGestorProveedores();
        this.gestorCategorias = factory.crearGestorCategorias();
        this.validador = factory.crearValidador();
        this.gerente = new Gerente(nombreJoyeria, pin);
        this.ventasDiarias = 0.0;
        this.fechaUltimasVentas = LocalDate.now();

        inicializarOperaciones();

        if (valorInicial > 0) {
            registrarOperacion("INVENTARIO_INICIAL", valorInicial, "Valor inicial del inventario", 0.0);
        }
    }

    private void inicializarOperaciones() {
        operaciones = new HashMap<>();
        operaciones.put("INGRESO_INVENTARIO", new OperacionIngresoInventario());
        operaciones.put("VENTA", new OperacionVenta());
        operaciones.put("COMPRA_PROVEEDOR", new OperacionCompraProveedor());
    }

    public boolean autenticar(String pin) {
        return gerente.autenticar(pin);
    }

    public boolean ejecutarOperacion(String tipoOperacion, Object... parametros) {
        EstrategiaOperacion operacion = operaciones.get(tipoOperacion.toUpperCase());
        if (operacion == null) {
            System.out.println("Tipo de operación no válido: " + tipoOperacion);
            return false;
        }

        boolean resultado = operacion.ejecutar(this, parametros);
        if (!resultado) {
            System.out.println("Error: " + operacion.obtenerMensajeError());
        }
        return resultado;
    }

    public boolean agregarInventario(double valor, String pin) {
        return ejecutarOperacion("INGRESO_INVENTARIO", valor, pin);
    }

    public boolean realizarVenta(double monto, String pin, String categoria) {
        return ejecutarOperacion("VENTA", monto, pin, categoria);
    }

    public boolean comprarAProveedor(String alias, double monto, String pin) {
        return ejecutarOperacion("COMPRA_PROVEEDOR", alias, monto, pin);
    }

    public void agregarOperacion(String tipo, EstrategiaOperacion operacion) {
        operaciones.put(tipo.toUpperCase(), operacion);
    }

    public void registrarOperacion(String tipo, double monto, String descripcion, double valorAnterior) {
        Operacion operacion = new Operacion(tipo, monto, descripcion, valorAnterior, valorTotal);
        historialOperaciones.add(operacion);
    }

    public void mostrarHistorial() {
        if (historialOperaciones.isEmpty()) {
            System.out.println("No hay operaciones registradas");
            return;
        }

        System.out.println("\n=== HISTORIAL DE OPERACIONES ===");
        for (Operacion o : historialOperaciones) {
            System.out.println(o);
        }
    }

    public void mostrarHistorialPorTipo(String tipo) {
        System.out.println("\n=== HISTORIAL - " + tipo.toUpperCase() + " ===");
        historialOperaciones.stream()
                .filter(o -> o.getTipo().equalsIgnoreCase(tipo))
                .forEach(System.out::println);
    }

    public void actualizarVentasDiarias() {
        LocalDate hoy = LocalDate.now();
        if (!hoy.equals(fechaUltimasVentas)) {
            ventasDiarias = 0.0;
            fechaUltimasVentas = hoy;
        }
    }

    // Getters y setters
    public GestorProveedores getGestorProveedores() { return gestorProveedores; }
    public GestorCategoriasJoyas getGestorCategorias() { return gestorCategorias; }
    public Gerente getGerente() { return gerente; }
    public Validador getValidador() { return validador; }
    public double getValorTotal() { return valorTotal; }
    public String getCodigoInventario() { return codigoInventario; }
    public String getNombreJoyeria() { return nombreJoyeria; }
    public double getVentasDiarias() { return ventasDiarias; }

    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public void setVentasDiarias(double ventasDiarias) { this.ventasDiarias = ventasDiarias; }
}
