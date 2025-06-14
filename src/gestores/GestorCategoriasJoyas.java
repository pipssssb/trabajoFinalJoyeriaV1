package gestores;
import entidades.*;
import java.util.ArrayList;
import java.util.HashMap;

interface ProveedorCategoriasJoyasIniciales {
    ArrayList<CategoriaJoya> obtenerCategoriasIniciales();
}

class ProveedorCategoriasJoyasDefault implements ProveedorCategoriasJoyasIniciales {
    @Override
    public ArrayList<CategoriaJoya> obtenerCategoriasIniciales() {
        ArrayList<CategoriaJoya> categorias = new ArrayList<>();
        categorias.add(new CategoriaJoya("ANILLOS", "Anillos de oro, plata y otros metales", 15000.0));
        categorias.add(new CategoriaJoya("COLLARES", "Collares y cadenas de diversos materiales", 12000.0));
        categorias.add(new CategoriaJoya("ARITOS", "Aretes y pendientes", 8000.0));
        categorias.add(new CategoriaJoya("PULSERAS", "Pulseras y brazaletes", 10000.0));
        categorias.add(new CategoriaJoya("RELOJES", "Relojes de lujo y ocasión", 25000.0));
        categorias.add(new CategoriaJoya("GEMAS", "Piedras preciosas y semipreciosas", 30000.0));
        categorias.add(new CategoriaJoya("OTROS", "Otros artículos de joyería", 5000.0));
        return categorias;
    }
}

class ProveedorCategoriasJoyasLujo implements ProveedorCategoriasJoyasIniciales {
    @Override
    public ArrayList<CategoriaJoya> obtenerCategoriasIniciales() {
        ArrayList<CategoriaJoya> categorias = new ArrayList<>();
        categorias.add(new CategoriaJoya("DIAMANTES", "Joyas con diamantes certificados", 100000.0));
        categorias.add(new CategoriaJoya("ORO_18K", "Joyas de oro 18 kilates", 50000.0));
        categorias.add(new CategoriaJoya("PLATINO", "Joyas de platino", 75000.0));
        categorias.add(new CategoriaJoya("ESMERALDAS", "Joyas con esmeraldas", 60000.0));
        categorias.add(new CategoriaJoya("RUBIES", "Joyas con rubíes", 55000.0));
        categorias.add(new CategoriaJoya("ZAFIROS", "Joyas con zafiros", 45000.0));
        return categorias;
    }
}

interface EstrategiaControlVenta {
    boolean puedeRegistrarVenta(CategoriaJoya categoria, double monto, double ventaActual);
    String obtenerMensajeError();
    String obtenerMensajeAdvertencia(CategoriaJoya categoria, double monto, double ventaActual);
}

class ControlVentaMetaMensual implements EstrategiaControlVenta {
    private String mensajeError = "";

    @Override
    public boolean puedeRegistrarVenta(CategoriaJoya categoria, double monto, double ventaActual) {
        // Siempre permite la venta, pero puede generar advertencias
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }

    @Override
    public String obtenerMensajeAdvertencia(CategoriaJoya categoria, double monto, double ventaActual) {
        double nuevaVenta = ventaActual + monto;
        double porcentajeMeta = (nuevaVenta / categoria.getMetaMensual()) * 100;

        if (porcentajeMeta >= 100) {
            return "¡Felicitaciones! Meta mensual alcanzada para " + categoria.getNombre();
        } else if (porcentajeMeta >= 80) {
            return "Cerca de la meta mensual para " + categoria.getNombre() + " (" + String.format("%.1f", porcentajeMeta) + "%)";
        } else if (porcentajeMeta >= 50) {
            return "Progreso medio en " + categoria.getNombre() + " (" + String.format("%.1f", porcentajeMeta) + "%)";
        }
        return null;
    }
}

class ControlVentaLimiteMaximo implements EstrategiaControlVenta {
    private String mensajeError = "";

    @Override
    public boolean puedeRegistrarVenta(CategoriaJoya categoria, double monto, double ventaActual) {
        if (ventaActual + monto > categoria.getMetaMensual() * 1.5) {
            mensajeError = "Límite máximo de ventas excedido para la categoría " + categoria.getNombre();
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }

    @Override
    public String obtenerMensajeAdvertencia(CategoriaJoya categoria, double monto, double ventaActual) {
        return null; // Esta estrategia no genera advertencias
    }
}

public class GestorCategoriasJoyas implements Gestor<CategoriaJoya> {
    private ArrayList<CategoriaJoya> categorias;
    private HashMap<String, Double> ventaPorCategoria;
    private ProveedorCategoriasJoyasIniciales proveedorCategorias;
    private EstrategiaControlVenta estrategiaControl;

    public GestorCategoriasJoyas() {
        this(new ProveedorCategoriasJoyasDefault(), new ControlVentaMetaMensual());
    }

    public GestorCategoriasJoyas(ProveedorCategoriasJoyasIniciales proveedor, EstrategiaControlVenta estrategia) {
        this.categorias = new ArrayList<>();
        this.ventaPorCategoria = new HashMap<>();
        this.proveedorCategorias = proveedor;
        this.estrategiaControl = estrategia;
        inicializarCategorias();
    }

    private void inicializarCategorias() {
        ArrayList<CategoriaJoya> categoriasIniciales = proveedorCategorias.obtenerCategoriasIniciales();
        for (CategoriaJoya categoria : categoriasIniciales) {
            agregar(categoria);
        }
    }

    @Override
    public boolean agregar(CategoriaJoya categoria) {
        categorias.add(categoria);
        ventaPorCategoria.put(categoria.getNombre(), 0.0);
        return true;
    }

    @Override
    public boolean eliminar(String nombre) {
        boolean eliminado = categorias.removeIf(c -> c.getNombre().equalsIgnoreCase(nombre));
        if (eliminado) {
            ventaPorCategoria.remove(nombre.toUpperCase());
        }
        return eliminado;
    }

    @Override
    public ArrayList<CategoriaJoya> listar() {
        return new ArrayList<>(categorias);
    }

    @Override
    public CategoriaJoya buscar(String nombre) {
        return categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public boolean registrarVenta(String categoria, double monto) {
        CategoriaJoya cat = buscar(categoria);
        if (cat == null) return false;

        double ventaActual = ventaPorCategoria.getOrDefault(categoria.toUpperCase(), 0.0);

        if (!estrategiaControl.puedeRegistrarVenta(cat, monto, ventaActual)) {
            System.out.println(estrategiaControl.obtenerMensajeError());
            return false;
        }

        ventaPorCategoria.put(categoria.toUpperCase(), ventaActual + monto);

        // Mostrar advertencia si existe
        String advertencia = estrategiaControl.obtenerMensajeAdvertencia(cat, monto, ventaActual);
        if (advertencia != null) {
            System.out.println(advertencia);
        }

        return true;
    }

    public CategoriaJoya buscarCategoria(String nombre) {
        return buscar(nombre);
    }

    public ArrayList<CategoriaJoya> obtenerCategorias() {
        return listar();
    }

    public void mostrarResumenVentas() {
        System.out.println("\n=== RESUMEN DE VENTAS POR CATEGORÍA ===");
        double totalVentas = 0.0;
        double totalMetas = 0.0;

        for (CategoriaJoya categoria : categorias) {
            double vendido = ventaPorCategoria.getOrDefault(categoria.getNombre(), 0.0);
            double meta = categoria.getMetaMensual();
            double porcentaje = (vendido / meta) * 100;

            totalVentas += vendido;
            totalMetas += meta;

            String estado = porcentaje >= 100 ? " ✓ META ALCANZADA" :
                    porcentaje >= 80 ? " ⚡ CERCA DE META" :
                            porcentaje >= 50 ? " ⚠ PROGRESO MEDIO" : " ❌ BAJO RENDIMIENTO";

            System.out.printf("%s: $%.2f vendido de $%.2f meta (%.1f%%)%s\n",
                    categoria.getNombre(), vendido, meta, porcentaje, estado);
        }

        System.out.println("\n--- RESUMEN GENERAL ---");
        System.out.printf("Total vendido: $%.2f\n", totalVentas);
        System.out.printf("Total metas: $%.2f\n", totalMetas);
        System.out.printf("Porcentaje general: %.1f%%\n", (totalVentas / totalMetas) * 100);
    }

    public void mostrarTopCategorias(int cantidad) {
        System.out.println("\n=== TOP " + cantidad + " CATEGORÍAS POR VENTAS ===");

        categorias.stream()
                .sorted((c1, c2) -> Double.compare(
                        ventaPorCategoria.getOrDefault(c2.getNombre(), 0.0),
                        ventaPorCategoria.getOrDefault(c1.getNombre(), 0.0)))
                .limit(cantidad)
                .forEach(cat -> {
                    double vendido = ventaPorCategoria.getOrDefault(cat.getNombre(), 0.0);
                    System.out.printf("%s: $%.2f\n", cat.getNombre(), vendido);
                });
    }

    public double obtenerVentaCategoria(String categoria) {
        return ventaPorCategoria.getOrDefault(categoria.toUpperCase(), 0.0);
    }

    public double obtenerTotalVentas() {
        return ventaPorCategoria.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public void reiniciarVentas() {
        for (String categoria : ventaPorCategoria.keySet()) {
            ventaPorCategoria.put(categoria, 0.0);
        }
        System.out.println("Ventas reiniciadas para todas las categorías");
    }

    public void cambiarEstrategiaControl(EstrategiaControlVenta nuevaEstrategia) {
        this.estrategiaControl = nuevaEstrategia;
    }

    public void cambiarProveedorCategorias(ProveedorCategoriasJoyasIniciales nuevoProveedor) {
        this.proveedorCategorias = nuevoProveedor;
    }

    public String obtenerMensajeError() {
        return estrategiaControl.obtenerMensajeError();
    }
}
