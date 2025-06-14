package entidades;

public class CategoriaJoya {
    private String nombre;
    private String descripcion;
    private double metaMensual;
    private double ventaActual;

    public CategoriaJoya(String nombre, String descripcion, double metaMensual) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.metaMensual = metaMensual;
        this.ventaActual = 0.0;
    }

    public boolean puedeVender(double monto) {
        // Permite ventas hasta 3 veces la meta mensual (control de ventas excesivas)
        return (ventaActual + monto) <= (metaMensual * 3);
    }

    public void registrarVenta(double monto) {
        ventaActual += monto;
    }

    public double getFaltaParaMeta() {
        return Math.max(0, metaMensual - ventaActual);
    }

    public double getPorcentajeCumplimiento() {
        return metaMensual > 0 ? (ventaActual / metaMensual) * 100 : 0;
    }

    public void reiniciarVentas() {
        ventaActual = 0.0;
    }

    public boolean metaAlcanzada() {
        return ventaActual >= metaMensual;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getMetaMensual() { return metaMensual; }
    public double getVentaActual() { return ventaActual; }

    public void setMetaMensual(double metaMensual) {
        this.metaMensual = metaMensual;
    }

    @Override
    public String toString() {
        return nombre + " (" + descripcion + ") - Meta: $" + metaMensual +
                " - Vendido: $" + ventaActual +
                " (" + String.format("%.1f", getPorcentajeCumplimiento()) + "%)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CategoriaJoya that = (CategoriaJoya) obj;
        return nombre.equals(that.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }
}