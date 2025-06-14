package entidades;

public class ProveedorFavorito {
    private String nombre;
    private String cuit;
    private String alias;
    private String tipoProveedor;

    public ProveedorFavorito(String nombre, String cuit, String alias, String tipoProveedor) {
        this.nombre = nombre;
        this.cuit = cuit;
        this.alias = alias;
        this.tipoProveedor = tipoProveedor;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getCuit() { return cuit; }
    public String getAlias() { return alias; }
    public String getTipoProveedor() { return tipoProveedor; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipoProveedor(String tipoProveedor) { this.tipoProveedor = tipoProveedor; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - CUIT: %s", nombre, alias, tipoProveedor, cuit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProveedorFavorito that = (ProveedorFavorito) obj;
        return alias.equals(that.alias);
    }

    @Override
    public int hashCode() {
        return alias.hashCode();
    }
}
