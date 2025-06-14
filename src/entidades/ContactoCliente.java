package entidades;

public class ContactoCliente {
    private String nombre;
    private String telefono;
    private String email;
    private String tipoCliente; // VIP, Regular, Mayorista
    private double totalCompras;

    public ContactoCliente(String nombre, String telefono, String email, String tipoCliente) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.tipoCliente = tipoCliente;
        this.totalCompras = 0.0;
    }

    public void registrarCompra(double monto) {
        totalCompras += monto;
        actualizarTipoCliente();
    }

    private void actualizarTipoCliente() {
        if (totalCompras >= 50000) {
            tipoCliente = "VIP";
        } else if (totalCompras >= 20000) {
            tipoCliente = "Regular";
        }
    }

    public boolean esClienteVIP() {
        return "VIP".equals(tipoCliente);
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getTipoCliente() { return tipoCliente; }
    public double getTotalCompras() { return totalCompras; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - Total compras: $%.2f",
                nombre, tipoCliente, email, totalCompras);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ContactoCliente that = (ContactoCliente) obj;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
