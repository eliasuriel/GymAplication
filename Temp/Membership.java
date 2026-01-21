import java.time.LocalDate;

public class Membership {

    private long id;
    private long userId;
    private LocalDate fechaPago;
    private LocalDate fechaVencimiento;
    private String estado; // ACTIVA o VENCIDA

    public Membership() {}

    public Membership(long id, long userId, LocalDate fechaPago, LocalDate fechaVencimiento, String estado) {
        this.id = id;
        this.userId = userId;
        this.fechaPago = fechaPago;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    // GETTERS Y SETTERS

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}