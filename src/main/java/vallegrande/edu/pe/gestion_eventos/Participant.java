package vallegrande.edu.pe.gestion_eventos;

public class Participant {

    private final int id;
    private final String nombres;
    private final String apellidos;
    private final String dni;
    private final String correo;
    private final String telefono;
    private final String institucion;

    public Participant(
            int id,
            String nombres,
            String apellidos,
            String dni,
            String correo,
            String telefono,
            String institucion
    ) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.correo = correo;
        this.telefono = telefono;
        this.institucion = institucion;
    }

    public int getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDni() {
        return dni;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getInstitucion() {
        return institucion;
    }
}