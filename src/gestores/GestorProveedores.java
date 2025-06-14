package gestores;
import entidades.*;
import java.util.ArrayList;
import java.util.function.Predicate;

interface EstrategiaValidacionProveedor {
    boolean esValido(ProveedorFavorito proveedor, ArrayList<ProveedorFavorito> proveedoresExistentes);
    String obtenerMensajeError();
}

class ValidacionAliasUnicoProveedor implements EstrategiaValidacionProveedor {
    private String mensajeError = "";

    @Override
    public boolean esValido(ProveedorFavorito proveedor, ArrayList<ProveedorFavorito> proveedoresExistentes) {
        boolean existeAlias = proveedoresExistentes.stream()
                .anyMatch(p -> p.getAlias().equalsIgnoreCase(proveedor.getAlias()));

        if (existeAlias) {
            mensajeError = "Ya existe un proveedor con ese alias";
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

class ValidacionCUITUnico implements EstrategiaValidacionProveedor {
    private String mensajeError = "";

    @Override
    public boolean esValido(ProveedorFavorito proveedor, ArrayList<ProveedorFavorito> proveedoresExistentes) {
        boolean existeCUIT = proveedoresExistentes.stream()
                .anyMatch(p -> p.getCuit().equals(proveedor.getCuit()));

        if (existeCUIT) {
            mensajeError = "Ya existe un proveedor con ese CUIT";
            return false;
        }
        return true;
    }

    @Override
    public String obtenerMensajeError() {
        return mensajeError;
    }
}

interface EstrategiaBusquedaProveedor<T> {
    ArrayList<T> buscar(ArrayList<T> lista, String criterio);
}

class BusquedaPorNombreProveedor implements EstrategiaBusquedaProveedor<ProveedorFavorito> {
    @Override
    public ArrayList<ProveedorFavorito> buscar(ArrayList<ProveedorFavorito> lista, String criterio) {
        return lista.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(criterio.toLowerCase()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}

class BusquedaPorTipoProveedor implements EstrategiaBusquedaProveedor<ProveedorFavorito> {
    @Override
    public ArrayList<ProveedorFavorito> buscar(ArrayList<ProveedorFavorito> lista, String criterio) {
        return lista.stream()
                .filter(p -> p.getTipoProveedor().toLowerCase().contains(criterio.toLowerCase()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}

class BusquedaPorCUITProveedor implements EstrategiaBusquedaProveedor<ProveedorFavorito> {
    @Override
    public ArrayList<ProveedorFavorito> buscar(ArrayList<ProveedorFavorito> lista, String criterio) {
        return lista.stream()
                .filter(p -> p.getCuit().contains(criterio))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}

public class GestorProveedores implements Gestor<ProveedorFavorito> {
    private ArrayList<ProveedorFavorito> proveedores;
    private EstrategiaValidacionProveedor estrategiaValidacion;

    public GestorProveedores() {
        this(new ValidacionAliasUnicoProveedor());
    }

    public GestorProveedores(EstrategiaValidacionProveedor estrategia) {
        this.proveedores = new ArrayList<>();
        this.estrategiaValidacion = estrategia;
    }

    @Override
    public boolean agregar(ProveedorFavorito proveedor) {
        if (!estrategiaValidacion.esValido(proveedor, proveedores)) {
            System.out.println(estrategiaValidacion.obtenerMensajeError());
            return false;
        }
        proveedores.add(proveedor);
        System.out.println("Proveedor favorito agregado exitosamente");
        return true;
    }

    @Override
    public boolean eliminar(String alias) {
        ProveedorFavorito proveedor = buscar(alias);
        if (proveedor != null) {
            proveedores.remove(proveedor);
            System.out.println("Proveedor eliminado exitosamente");
            return true;
        }
        System.out.println("No se encontró un proveedor con ese alias");
        return false;
    }

    @Override
    public ArrayList<ProveedorFavorito> listar() {
        return new ArrayList<>(proveedores);
    }

    @Override
    public ProveedorFavorito buscar(String alias) {
        return proveedores.stream()
                .filter(p -> p.getAlias().equalsIgnoreCase(alias))
                .findFirst()
                .orElse(null);
    }

    public boolean agregarProveedor(String nombre, String cuit, String alias, String tipoProveedor) {
        ProveedorFavorito nuevo = new ProveedorFavorito(nombre, cuit, alias, tipoProveedor);
        return agregar(nuevo);
    }

    public boolean eliminarProveedor(String alias) {
        return eliminar(alias);
    }

    public ProveedorFavorito buscarPorAlias(String alias) {
        return buscar(alias);
    }

    public ProveedorFavorito buscarPorCUIT(String cuit) {
        return proveedores.stream()
                .filter(p -> p.getCuit().equals(cuit))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<ProveedorFavorito> buscarPorNombre(String nombre) {
        EstrategiaBusquedaProveedor<ProveedorFavorito> busquedaNombre = new BusquedaPorNombreProveedor();
        return busquedaNombre.buscar(proveedores, nombre);
    }

    public ArrayList<ProveedorFavorito> buscarPorTipo(String tipo) {
        EstrategiaBusquedaProveedor<ProveedorFavorito> busquedaTipo = new BusquedaPorTipoProveedor();
        return busquedaTipo.buscar(proveedores, tipo);
    }

    public ArrayList<ProveedorFavorito> buscarConEstrategia(EstrategiaBusquedaProveedor<ProveedorFavorito> estrategia, String criterio) {
        return estrategia.buscar(proveedores, criterio);
    }

    public ArrayList<ProveedorFavorito> buscarConPredicado(Predicate<ProveedorFavorito> predicado) {
        return proveedores.stream()
                .filter(predicado)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void listarProveedores() {
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores favoritos");
            return;
        }
        System.out.println("\n=== PROVEEDORES FAVORITOS ===");
        for (int i = 0; i < proveedores.size(); i++) {
            System.out.println((i + 1) + ". " + proveedores.get(i));
        }
    }

    public ArrayList<ProveedorFavorito> obtenerProveedores() {
        return listar();
    }

    public void cambiarEstrategiaValidacion(EstrategiaValidacionProveedor nuevaEstrategia) {
        this.estrategiaValidacion = nuevaEstrategia;
    }

    public int obtenerCantidadProveedores() {
        return proveedores.size();
    }

    public boolean existeProveedor(String alias) {
        return buscar(alias) != null;
    }
}
