import java.time.LocalDate;

public class BodyMeasurement {

    private long id;
    private long userId;
    private LocalDate fecha;
    private double peso;
    private double altura;
    private double cintura;

    public BodyMeasurement() {}

    public BodyMeasurement(long id, long userId, LocalDate fecha, double peso, double altura, double cintura) {
        this.id = id;
        this.userId = userId;
        this.fecha = fecha;
        this.peso = peso;
        this.altura = altura;
        this.cintura = cintura;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getCintura() {
        return cintura;
    }

    public void setCintura(double cintura) {
        this.cintura = cintura;
    }
}