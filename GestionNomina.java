import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * SISTEMA DE NÓMINA PROFESIONAL - CIPA
 * Aplica SOLID, Código Limpio y Validaciones de Seguridad.
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
        return calcularSalarioBruto() * 0.04; // Seguro Social y Pensión (4%)
    }

    public double calcularSalarioNeto() {
        double bruto = calcularSalarioBruto();
        double neto = bruto - calcularDeducciones();
        return Math.max(neto, 0); // Validación: No neto negativo
    }
}

class EmpleadoAsalariado extends Empleado {
    private double salarioFijo;

    public EmpleadoAsalariado(String nombre, int anos, double salario) {
        super(nombre, anos);
        this.salarioFijo = salario;
    }

    @Override
    public String getTipoEmpleado() { return "Asalariado"; }

    @Override
    public double calcularSalarioBruto() {
        double bono = (anosAntiguedad > 5) ? salarioFijo * 0.10 : 0;
        return salarioFijo + bono + 1000000; // Salario + Bono Antigüedad + Bono Alimentación
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

    @Override
    public String getTipoEmpleado() { return "Por Horas"; }

    @Override
    public double calcularSalarioBruto() {
        double pago = (horas > 40) ? (40 * tarifaHora) + ((horas - 40) * tarifaHora * 1.5) : horas * tarifaHora;
        if (anosAntiguedad > 1 && aceptaFondo) pago -= (pago * 0.02); // Fondo de ahorro
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

    @Override
    public String getTipoEmpleado() { return "Comisión"; }

    @Override
    public double calcularSalarioBruto() {
        double comision = ventas * 0.05; 
        if (ventas > 20000000) comision += (ventas * 0.03); // Bono adicional si ventas > 20M
        return salarioBase + comision + 1000000; // Incluye Bono Alimentación
    }
}

public class GestionNomina {
    private static Scanner sc = new Scanner(System.in);

    // VALIDACIÓN: Solo letras y espacios
    private static String leerNombre(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.nextLine().trim();
            if (entrada.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$") && !entrada.isEmpty()) {
                return entrada;
            } else {
                System.out.println("¡ERROR! El nombre solo puede contener letras y no puede estar vacío.");
            }
        }
    }

    // VALIDACIÓN: Números positivos para dinero
    private static double leerNumeroPositivo(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String entrada = sc.next().replace(".", "").replace(",", ".");
                double valor = Double.parseDouble(entrada);
                if (valor >= 0) return valor;
                System.out.println("¡ERROR! El valor no puede ser negativo.");
            } catch (Exception e) {
                System.out.println("¡ERROR! Ingrese un número válido.");
                sc.nextLine();
            }
        }
    }

    // VALIDACIÓN: Años con límite de 90
    private static int leerAnosEmpresa(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                int valor = sc.nextInt();
                if (valor >= 0 && valor <= 90) return valor;
                if (valor < 0) System.out.println("¡ERROR! Los años no pueden ser negativos.");
                else System.out.println("¡ERROR! El límite máximo permitido es de 90 años.");
            } catch (InputMismatchException e) {
                System.out.println("¡ERROR! Ingrese un número entero.");
                sc.next();
            }
        }
    }

    // VALIDACIÓN: Solo enteros positivos para opciones
    private static int leerEnteroPositivo(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                int valor = sc.nextInt();
                if (valor >= 0) return valor;
                System.out.println("¡ERROR! No se permiten números negativos.");
            } catch (InputMismatchException e) {
                System.out.println("¡ERROR! Ingrese un número entero.");
                sc.next();
            }
        }
    }

    public static void main(String[] args) {
        List<Empleado> listaEmpleados = new ArrayList<>();
        int opcion;

        do {
            System.out.println("\n--- SISTEMA DE NÓMINA - CIPA #4 ---");
            System.out.println("1. Agregar Empleado Asalariado");
            System.out.println("2. Agregar Empleado por Horas");
            System.out.println("3. Agregar Empleado por Comisión");
            System.out.println("4. Mostrar Reporte de Nómina");
            System.out.println("5. Salir");
            opcion = leerEnteroPositivo("Seleccione una opción: ");
            sc.nextLine(); // Limpiar buffer

            if (opcion >= 1 && opcion <= 3) {
                String nombre = leerNombre("Nombre completo: ");
                int anos = leerAnosEmpresa("Años en la empresa (Máximo 90): ");

                switch (opcion) {
                    case 1 -> {
                        double sueldo = leerNumeroPositivo("Salario mensual fijo: ");
                        listaEmpleados.add(new EmpleadoAsalariado(nombre, anos, sueldo));
                    }
                    case 2 -> {
                        double tarifa = leerNumeroPositivo("Tarifa por hora: ");
                        int hrs = leerEnteroPositivo("Horas trabajadas: ");
                        System.out.print("¿Acepta fondo de ahorro? (true/false): ");
                        while(!sc.hasNextBoolean()) {
                            System.out.println("¡ERROR! Responda solo true o false.");
                            sc.next();
                        }
                        boolean fondo = sc.nextBoolean();
                        listaEmpleados.add(new EmpleadoPorHoras(nombre, anos, tarifa, hrs, fondo));
                    }
                    case 3 -> {
                        double base = leerNumeroPositivo("Salario base: ");
                        double vtas = leerNumeroPositivo("Total ventas del mes: ");
                        listaEmpleados.add(new EmpleadoComision(nombre, anos, base, vtas));
                    }
                }
                sc.nextLine(); // Limpiar buffer final
                System.out.println(">> Empleado registrado correctamente.");
            } else if (opcion == 4) {
                System.out.println("\n====================================================================");
                System.out.printf("%-30s | %-12s | %-15s%n", "NOMBRE", "TIPO", "NETO A PAGAR");
                System.out.println("====================================================================");
                for (Empleado e : listaEmpleados) {
                    System.out.printf("%-30s | %-12s | $%14.2f%n", e.nombre, e.getTipoEmpleado(), e.calcularSalarioNeto());
                }
                System.out.println("====================================================================");
            }
        } while (opcion != 5);
        
        System.out.println("Cerrando sistema...");
    }
}
