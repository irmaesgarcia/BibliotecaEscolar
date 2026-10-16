import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private int cantidadDisponible;

    public Libro(String isbn, String titulo, String autor, int cantidadDisponible) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        setCantidadDisponible(cantidadDisponible);
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getCantidadDisponible() { return cantidadDisponible; }

    public void setCantidadDisponible(int cantidad) {
        this.cantidadDisponible = Math.max(cantidad, 0);
    }

    public boolean prestarEjemplar() {
        if (this.cantidadDisponible > 0) {
            this.cantidadDisponible--;
            return true;
        }
        return false;
    }

    public void devolverEjemplar() {
        this.cantidadDisponible++;
    }

    @Override
    public String toString() {
        return "Libro [ISBN=" + isbn + ", Título=" + titulo + ", Autor=" + autor + ", Disponibles=" + cantidadDisponible + "]";
    }
}

class Usuario {
    private String idUsuario;
    private String nombre;
    private String tipo;

    public Usuario(String idUsuario, String nombre, String tipo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }

    @Override
    public String toString() {
        return "Usuario [ID=" + idUsuario + ", Nombre=" + nombre + ", Tipo=" + tipo + "]";
    }
}

class Prestamo {
    private Libro libro;
    private Usuario usuario;
    private LocalDate fechaDevolucion;
    private boolean activo;

    public Prestamo(Libro libro, Usuario usuario) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaDevolucion = LocalDate.now().plusDays(14);
        this.activo = true;
    }

    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public boolean isActivo() { return activo; }

    public void cerrarPrestamo() { this.activo = false; }

    @Override
    public String toString() {
        String estado = activo ? "Activo (Devolución límite: " + fechaDevolucion + ")" : "Devuelto";
        return "Préstamo -> " + usuario.getNombre() + " | Libro: " + libro.getTitulo() + " | Estado: " + estado;
    }
}

public class Main {
    private static List<Libro> libros = new ArrayList<>();
    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<Prestamo> prestamos = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        inicializarDatosPrueba();
        int opcion = 0;

        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: registrarLibro(); break;
                    case 2: registrarUsuario(); break;
                    case 3: realizarPrestamo(); break;
                    case 4: registrarDevolucion(); break;
                    case 5: listarLibros(); break;
                    case 6: listarUsuarios(); break;
                    case 7: listarPrestamos(); break;
                    case 8: System.out.println("¡Saliendo del sistema!"); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
            if (opcion != 8) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        } while (opcion != 8);
    }

    private static void inicializarDatosPrueba() {
        libros.add(new Libro("978-987-1", "Cien años de soledad", "Gabriel García Márquez", 3));
        usuarios.add(new Usuario("U001", "Ana Pérez", "Estudiante"));
    }

    private static void mostrarMenu() {
        System.out.println("\n--- SISTEMA DE BIBLIOTECA ESCOLAR ---");
        System.out.println("1. Registrar nuevo libro");
        System.out.println("2. Registrar nuevo usuario");
        System.out.println("3. Realizar préstamo");
        System.out.println("4. Registrar devolución");
        System.out.println("5. Listar libros");
        System.out.println("6. Listar usuarios");
        System.out.println("7. Ver historial de préstamos");
        System.out.println("8. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void registrarLibro() {
        System.out.print("ISBN: "); String isbn = scanner.nextLine();
        System.out.print("Título: "); String titulo = scanner.nextLine();
        System.out.print("Autor: "); String autor = scanner.nextLine();
        System.out.print("Cantidad: "); int cant = Integer.parseInt(scanner.nextLine());
        libros.add(new Libro(isbn, titulo, autor, cant));
        System.out.println("¡Libro registrado con éxito!");
    }

    private static void registrarUsuario() {
        System.out.print("ID Usuario (ej. U002): "); String id = scanner.nextLine();
        System.out.print("Nombre: "); String nombre = scanner.nextLine();
        System.out.print("Tipo (Estudiante/Profesor): "); String tipo = scanner.nextLine();
        usuarios.add(new Usuario(id, nombre, tipo));
        System.out.println("¡Usuario registrado con éxito!");
    }

    private static void realizarPrestamo() {
        listarUsuarios();
        System.out.print("ID del Usuario: "); String id = scanner.nextLine();
        Usuario u = usuarios.stream().filter(x -> x.getIdUsuario().equalsIgnoreCase(id)).findFirst().orElse(null);

        listarLibros();
        System.out.print("ISBN del Libro: "); String isbn = scanner.nextLine();
        Libro l = libros.stream().filter(x -> x.getIsbn().equalsIgnoreCase(isbn)).findFirst().orElse(null);

        if (u != null && l != null && l.prestarEjemplar()) {
            prestamos.add(new Prestamo(l, u));
            System.out.println("¡Préstamo realizado exitosamente!");
        } else {
            System.out.println("Error: No se pudo realizar el préstamo (verifique datos o stock).");
        }
    }

    private static void registrarDevolucion() {
        prestamos.stream().filter(Prestamo::isActivo).forEach(System.out::println);
        System.out.print("Ingrese ISBN del libro a devolver: "); String isbn = scanner.nextLine();
        Prestamo p = prestamos.stream().filter(x -> x.isActivo() && x.getLibro().getIsbn().equalsIgnoreCase(isbn)).findFirst().orElse(null);

        if (p != null) {
            p.cerrarPrestamo();
            p.getLibro().devolverEjemplar();
            System.out.println("¡Devolución registrada con éxito!");
        } else {
            System.out.println("No se encontró un préstamo activo con ese ISBN.");
        }
    }

    private static void listarLibros() { libros.forEach(System.out::println); }
    private static void listarUsuarios() { usuarios.forEach(System.out::println); }
    private static void listarPrestamos() { prestamos.forEach(System.out::println); }
}