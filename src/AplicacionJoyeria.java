import entidades.*;
import gestores.*;
import modelo.*;
import notificaciones.*;
import procesamiento.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AplicacionJoyeria {
    private static Scanner scanner = new Scanner(System.in);
    private static InventarioJoyeria inventario;
    private static Validador validador = new ValidadorOperacionesImpl();
    private static Procesador<InventarioJoyeria> notificador = new NotificadorUnificado();
    private static Procesador<InventarioJoyeria> procesadorOperaciones = new ProcesadorOperacionesUnificado();

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE GESTIÓN DE JOYERÍA ===");

        configurarSistema();
        inventario = new InventarioJoyeria("JOY001", "Joyería El Diamante", 50000.0, "1234");

        Gestor<ProveedorFavorito> gestorProv = inventario.getGestorProveedores();
        gestorProv.agregar(new ProveedorFavorito("Oro y Plata SA", "20123456789", "oroplata", "Proveedor Premium"));
        gestorProv.agregar(new ProveedorFavorito("Diamantes del Sur", "20987654321", "diamantes", "Especialista en Gemas"));

        notificador.procesar(inventario, "INICIO_SESION", "Bienvenido al sistema de gestión de joyería");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    agregarInventario();
                    break;
                case 2:
                    realizarVenta();
                    break;
                case 3:
                    comprarAProveedor();
                    break;
                case 4:
                    gestionarProveedores();
                    break;
                case 5:
                    consultarHistorial();
                    break;
                case 6:
                    mostrarResumenVentasPorCategoria();
                    break;
                case 7:
                    cambiarPin();
                    break;
                case 8:
                    consultarValorInventario();
                    break;
                case 0:
                    notificador.procesar(inventario, "CIERRE_SESION", "Gracias por usar nuestro sistema");
                    continuar = false;
                    System.out.println("¡Gracias por usar nuestro sistema de joyería!");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void configurarSistema() {
        notificador.configurar("CONSOLE");
        procesadorOperaciones.configurar(0.0);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Agregar al inventario");
        System.out.println("2. Realizar venta");
        System.out.println("3. Comprar a proveedor");
        System.out.println("4. Gestionar proveedores");
        System.out.println("5. Consultar historial");
        System.out.println("6. Resumen de ventas por categoría");
        System.out.println("7. Cambiar PIN");
        System.out.println("8. Consultar valor del inventario");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void agregarInventario() {
        System.out.print("Ingrese el valor a agregar al inventario: $");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Ingrese su PIN: ");
        String pin = scanner.nextLine();

        if (!validador.validar("INGRESO_INVENTARIO", valor)) {
            System.out.println("Error: " + validador.obtenerMensajeError());
            return;
        }

        if (!validador.validar("HORARIO")) {
            System.out.println("Error: " + validador.obtenerMensajeError());
            return;
        }

        if (inventario.autenticar(pin)) {
            procesadorOperaciones.procesar(inventario, "AGREGAR_INVENTARIO", valor);

            notificador.procesar(inventario, "INGRESO_INVENTARIO",
                    "Inventario incrementado por $" + valor + ". Valor total: $" + inventario.getValorTotal());
        } else {
            System.out.println("PIN incorrecto");
        }
    }

    private static void realizarVenta() {
        System.out.print("Ingrese el monto de la venta: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("\n=== SELECCIONAR CATEGORÍA DE JOYA ===");
        Gestor<CategoriaJoya> gestorCat = inventario.getGestorCategorias();
        ArrayList<CategoriaJoya> categorias = gestorCat.listar();

        System.out.println("0. Sin categoría");
        for (int i = 0; i < categorias.size(); i++) {
            CategoriaJoya cat = categorias.get(i);
            System.out.println((i + 1) + ". " + cat.getNombre() +
                    " (Meta mensual: $" + cat.getMetaMensual() + ")");
        }

        System.out.print("Seleccione una categoría (0-" + categorias.size() + "): ");
        int opcionCategoria = scanner.nextInt();
        scanner.nextLine();

        String categoria = null;
        if (opcionCategoria > 0 && opcionCategoria <= categorias.size()) {
            categoria = categorias.get(opcionCategoria - 1).getNombre();
        }

        System.out.print("Ingrese su PIN: ");
        String pin = scanner.nextLine();

        if (!validador.validar("VENTA", monto, inventario.getValorTotal())) {
            System.out.println("Error: " + validador.obtenerMensajeError());
            return;
        }

        if (inventario.realizarVenta(monto, pin, categoria)) {
            String mensaje = categoria != null ?
                    "Venta de $" + monto + " en categoría " + categoria :
                    "Venta de $" + monto;
            notificador.procesar(inventario, "VENTA", mensaje + ". Valor inventario: $" + inventario.getValorTotal());
        }
    }

    private static void comprarAProveedor() {
        Gestor<ProveedorFavorito> gestorProv = inventario.getGestorProveedores();
        ArrayList<ProveedorFavorito> proveedores = gestorProv.listar();

        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores favoritos");
            return;
        }

        System.out.println("\n=== PROVEEDORES FAVORITOS ===");
        for (int i = 0; i < proveedores.size(); i++) {
            System.out.println((i + 1) + ". " + proveedores.get(i));
        }

        System.out.print("Ingrese el alias del proveedor: ");
        String alias = scanner.nextLine();

        System.out.print("Ingrese el monto de la compra: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Ingrese su PIN: ");
        String pin = scanner.nextLine();

        ProveedorFavorito proveedor = gestorProv.buscar(alias);
        if (proveedor != null && inventario.comprarAProveedor(alias, monto, pin)) {
            notificador.procesar(inventario, "COMPRA_PROVEEDOR",
                    "Compra de $" + monto + " a " + proveedor.getNombre() +
                            ". Valor inventario: $" + inventario.getValorTotal());
        }
    }

    private static void gestionarProveedores() {
        System.out.println("\n=== GESTIÓN DE PROVEEDORES ===");
        System.out.println("1. Listar proveedores");
        System.out.println("2. Agregar proveedor");
        System.out.println("3. Eliminar proveedor");
        System.out.println("4. Buscar proveedor");
        System.out.print("Seleccione una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        Gestor<ProveedorFavorito> gestorProv = inventario.getGestorProveedores();

        switch (opcion) {
            case 1:
                ArrayList<ProveedorFavorito> proveedores = gestorProv.listar();
                if (proveedores.isEmpty()) {
                    System.out.println("No hay proveedores favoritos");
                } else {
                    System.out.println("\n=== PROVEEDORES FAVORITOS ===");
                    for (int i = 0; i < proveedores.size(); i++) {
                        System.out.println((i + 1) + ". " + proveedores.get(i));
                    }
                }
                break;
            case 2:
                System.out.print("Nombre: ");
                String nombre = scanner.nextLine();
                System.out.print("CUIT: ");
                String cuit = scanner.nextLine();
                System.out.print("Alias: ");
                String alias = scanner.nextLine();
                System.out.print("Tipo de proveedor: ");
                String tipo = scanner.nextLine();

                ProveedorFavorito nuevo = new ProveedorFavorito(nombre, cuit, alias, tipo);
                if (gestorProv.agregar(nuevo)) {
                    System.out.println("Proveedor agregado exitosamente");
                } else {
                    System.out.println("Error: Ya existe un proveedor con ese alias");
                }
                break;
            case 3:
                System.out.print("Alias a eliminar: ");
                String aliasEliminar = scanner.nextLine();
                if (gestorProv.eliminar(aliasEliminar)) {
                    System.out.println("Proveedor eliminado exitosamente");
                } else {
                    System.out.println("No se encontró un proveedor con ese alias");
                }
                break;
            case 4:
                System.out.print("Alias a buscar: ");
                String aliasBuscar = scanner.nextLine();
                ProveedorFavorito encontrado = gestorProv.buscar(aliasBuscar);
                if (encontrado != null) {
                    System.out.println("Proveedor encontrado: " + encontrado);
                } else {
                    System.out.println("No se encontró el proveedor");
                }
                break;
        }
    }

    private static void consultarHistorial() {
        System.out.println("\n=== CONSULTAR HISTORIAL ===");
        System.out.println("1. Mostrar todo el historial");
        System.out.println("2. Filtrar por tipo de operación");
        System.out.print("Seleccione una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch (opcion) {
            case 1:
                inventario.mostrarHistorial();
                break;
            case 2:
                System.out.print("Ingrese el tipo (INGRESO_INVENTARIO, VENTA, COMPRA_PROVEEDOR): ");
                String tipo = scanner.nextLine();
                inventario.mostrarHistorialPorTipo(tipo);
                break;
        }
    }

    private static void mostrarResumenVentasPorCategoria() {
        Gestor<CategoriaJoya> gestorCat = inventario.getGestorCategorias();
        if (gestorCat instanceof GestorCategoriasJoyas) {
            ((GestorCategoriasJoyas) gestorCat).mostrarResumenVentas();
        }
    }

    private static void cambiarPin() {
        System.out.print("Ingrese su PIN actual: ");
        String pinActual = scanner.nextLine();
        System.out.print("Ingrese su nuevo PIN (4 dígitos): ");
        String pinNuevo = scanner.nextLine();

        inventario.getGerente().cambiarPin(pinActual, pinNuevo);
    }

    private static void consultarValorInventario() {
        System.out.print("Ingrese su PIN: ");
        String pin = scanner.nextLine();

        if (inventario.autenticar(pin)) {
            System.out.println("Valor total del inventario: $" + inventario.getValorTotal());
        }
    }
}