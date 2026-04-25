import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * * Validaciones Integradas:
 * 1. Menú restringido (1-6).
 * 2. Nombres: Únicos, solo letras, máximo 50 caracteres.
 * 3. Antigüedad: Máximo 90 años.
 * 4. Horas: Máximo 450 mensuales.
 * 5. Valores: Bloqueo total de números negativos.
 */
abstract class Empleado {
    protected String nombre;
    protected int anosAntiguedad;

    public Empleado(String nombre, int anosAntiguedad) {
        this.nombre = nombre;
        this.anosAntiguedad = anosAntiguedad;
    }

    public abstract String getTipoEmpleado();
    public abstract double calcularSalarioBruto();

    public double calcularDeducciones() {
        return calcularSalarioBruto() * 0.04; // Salud y Pensión (4%)
    }

    public double calcularSalarioNeto() {
        double bruto = calcularSalarioBruto();
        double neto = bruto - calcularDeducciones();
        return Math.max(neto, 0); 
    }

    public String getNombre() { return nombre; }
}

class EmpleadoAsalariado extends Empleado {
    private double salarioFijo;
    public EmpleadoAsalariado(String nombre, int anos, double salario) {
        super(nombre, anos);
        this.salarioFijo = salario;
    }
    @Override public String getTipoEmpleado() { return "Asalariado"; }
    @Override public double calcularSalarioBruto() {
        double bono = (anosAntiguedad > 5) ? salarioFijo * 0.10 : 0;
        return salarioFijo + bono + 1000000; // + Bono Alimentación
    }
}

class EmpleadoPorHoras extends Empleado {
    private double tarifaHora;
    private int horas;
    private boolean aceptaFondo;
    public EmpleadoPorHoras(String nombre, int anos, double tarifa, int horas, boolean aceptaFondo) {
        super(nombre, anos);
        this.tarifaHora = tarifa;
        this.horas = horas;
        this.aceptaFondo = aceptaFondo;
    }
    @Override public String getTipoEmpleado() { return "Por Horas"; }
    @Override public double calcularSalarioBruto() {
        double pago = (horas > 40) ? (40 * tarifaHora) + ((horas - 40) * tarifaHora * 1.5) : horas * tarifaHora;
        if (anosAntiguedad > 1 && aceptaFondo) pago -= (pago * 0.02);
        return pago;
    }
}

class EmpleadoComision extends Empleado {
    private double salarioBase;
    private double ventas;
    public EmpleadoComision(String nombre, int anos, double base, double ventas) {
        super(nombre, anos);
        this.salarioBase = base;
        this.ventas = ventas;
    }
    @Override public String getTipoEmpleado() { return "Comisión"; }
    @Override public double calcularSalarioBruto() {
        double comision = ventas * 0.05; 
        if (ventas > 20000000) comision += (ventas * 0.03);
        return salarioBase + comision + 1000000;
    }
}

class EmpleadoTemporal extends Empleado {
    private double salarioFijo;
    public EmpleadoTemporal(String nombre, int anos, double salario) {
        super(nombre, anos);
        this.salarioFijo = salario;
    }
    @Override public String getTipoEmpleado() { return "Temporal"; }
    @Override public double calcularSalarioBruto() { return salarioFijo; }
}

public class GestionNomina {
    private static Scanner sc = new Scanner(System.in);

    // VALIDACIÓN: Solo letras, Único y Máximo 50 caracteres
    private static String leerNombreValidado(String mensaje, List<Empleado> lista) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.nextLine().trim();
            
            // Verificación de longitud (Máximo 50)
            if (entrada.length() > 50) {
                System.out.println("¡ERROR! El nombre es demasiado largo (Máximo 50 letras).");
                continue;
            }
            if (entrada.length() < 2) {
                System.out.println("¡ERROR! El nombre debe tener al menos 2 letras.");
                continue;
            }
            // Verificación de caracteres (Solo letras)
            if (!entrada.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
                System.out.println("¡ERROR! El nombre solo puede contener letras y espacios.");
                continue;
            }
            // Verificación de duplicados
            boolean repetido = false;
            for (Empleado e : lista) {
                if (e.getNombre().equalsIgnoreCase(entrada)) {
                    repetido = true;
                    break;
                }
            }
            if (repetido) {
                System.out.println("¡ERROR! Este empleado ya está registrado.");
            } else {
                return entrada;
            }
        }
    }

    private static double leerNumeroPositivo(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String entrada = sc.next().replace(".", "").replace(",", ".");
                double valor = Double.parseDouble(entrada);
                if (valor >= 0) return valor;
                System.out.println("¡ERROR! No se permiten valores negativos.");
            } catch (Exception e) {
                System.out.println("¡ERROR! Ingrese un valor numérico válido.");
                sc.nextLine();
            }
        }
    }

    private static int leerEnteroRango(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.print(mensaje);
                int valor = sc.nextInt();
                if (valor >= min && valor <= max) return valor;
                System.out.println("¡ERROR! El valor debe estar entre " + min + " y " + max + ".");
            } catch (Exception e) {
                System.out.println("¡ERROR! Ingrese un número entero.");
                sc.next();
            }
        }
    }

    public static void main(String[] args) {
        List<Empleado> listaEmpleados = new ArrayList<>();
        int opcion = 0;

        do {
            System.out.println("\n--- SISTEMA DE NÓMINA - CIPA ---");
            System.out.println("1. Agregar Empleado Asalariado");
            System.out.println("2. Agregar Empleado por Horas");
            System.out.println("3. Agregar Empleado por Comisión");
            System.out.println("4. Agregar Empleado Temporal");
            System.out.println("5. Mostrar Reporte de Nómina");
            System.out.println("6. Salir");
            
            try {
                System.out.print("Seleccione una opción (1-6): ");
                opcion = sc.nextInt();
                sc.nextLine(); // Buffer
            } catch (InputMismatchException e) {
                System.out.println("¡ERROR! Ingrese un número del 1 al 6.");
                sc.next();
                continue;
            }

            if (opcion >= 1 && opcion <= 4) {
                String nombre = leerNombreValidado("Nombre completo (Max 50 letras): ", listaEmpleados);
                int anos = leerEnteroRango("Años en la empresa (0-90): ", 0, 90);

                switch (opcion) {
                    case 1 -> {
                        double sueldo = leerNumeroPositivo("Salario mensual fijo: ");
                        listaEmpleados.add(new EmpleadoAsalariado(nombre, anos, sueldo));
                    }
                    case 2 -> {
                        double tarifa = leerNumeroPositivo("Tarifa por hora: ");
                        int hrs = leerEnteroRango("Horas trabajadas (0-450): ", 0, 450);
                        System.out.print("¿Acepta fondo de ahorro? (true/false): ");
                        while(!sc.hasNextBoolean()) { sc.next(); System.out.print("Use 'true' o 'false': "); }
                        boolean fondo = sc.nextBoolean();
                        listaEmpleados.add(new EmpleadoPorHoras(nombre, anos, tarifa, hrs, fondo));
                    }
                    case 3 -> {
                        double base = leerNumeroPositivo("Salario base: ");
                        double vtas = leerNumeroPositivo("Total ventas mensuales: ");
                        listaEmpleados.add(new EmpleadoComision(nombre, anos, base, vtas));
                    }
                    case 4 -> {
                        double sueldo = leerNumeroPositivo("Salario mensual fijo (Temporal): ");
                        listaEmpleados.add(new EmpleadoTemporal(nombre, anos, sueldo));
                    }
                }
                sc.nextLine();
                System.out.println(">> Registro exitoso.");
            } else if (opcion == 5) {
                if (listaEmpleados.isEmpty()) {
                    System.out.println("No hay empleados en el sistema.");
                } else {
                    System.out.println("\n====================================================================");
                    System.out.printf("%-32s | %-12s | %-15s%n", "NOMBRE (MÁX 50)", "TIPO", "NETO A PAGAR");
                    System.out.println("====================================================================");
                    for (Empleado e : listaEmpleados) {
                        System.out.printf("%-32s | %-12s | $%14.2f%n", e.nombre, e.getTipoEmpleado(), e.calcularSalarioNeto());
                    }
                    System.out.println("====================================================================");
                }
            } else if (opcion != 6) {
                System.out.println("¡ERROR! Opción fuera de rango.");
            }
        } while (opcion != 6);
        System.out.println("Sistema finalizado. ¡CIPA #4 éxito!");
    }
}
