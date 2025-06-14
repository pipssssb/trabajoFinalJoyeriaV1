package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

interface FormateadorOperacion {
    String formatear(Operacion operacion);
}

class FormateadorCompleto implements FormateadorOperacion {
    @Override
    public String formatear(Operacion operacion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %s - $%.2f | Valor: $%.2f -> $%.2f | %s",
                operacion.getFecha().format(formatter),
                operacion.getTipo(),
                operacion.getMonto(),
                operacion.getValorAnterior(),
                operacion.getValorPosterior(),
                operacion.getDescripcion());
    }
}

class FormateadorSimple implements FormateadorOperacion {
    @Override
    public String formatear(Operacion operacion) {
        return String.format("%s: $%.2f",
                operacion.getTipo(),
                operacion.getMonto());
    }
}

class FormateadorResumen implements FormateadorOperacion {
    @Override
    public String formatear(Operacion operacion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return String.format("%s | %s: $%.2f",
                operacion.getFecha().format(formatter),
                operacion.getTipo(),
                operacion.getMonto());
    }
}

class Operacion {
    private String tipo;
    private double monto;
    private LocalDateTime fecha;
    private String descripcion;
    private double valorAnterior;
    private double valorPosterior;
    private static FormateadorOperacion formateadorDefault = new FormateadorCompleto();

    public Operacion(String tipo, double monto, String descripcion, double valorAnterior, double valorPosterior) {
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.descripcion = descripcion;
        this.valorAnterior = valorAnterior;
        this.valorPosterior = valorPosterior;
    }

    public String toString(FormateadorOperacion formateador) {
        return formateador.formatear(this);
    }

    @Override
    public String toString() {
        return formateadorDefault.formatear(this);
    }

    public static void setFormateadorDefault(FormateadorOperacion formateador) {
        formateadorDefault = formateador;
    }

    // Getters
    public String getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public double getValorAnterior() { return valorAnterior; }
    public double getValorPosterior() { return valorPosterior; }
}
