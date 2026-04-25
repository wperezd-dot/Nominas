import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 *  PRINCIPIOS SOLID Y CÓDIGO LIMPIO
 */
abstract class Empleado {
    protected String nombre;
    protected int anosAntiguedad;

    public Empleado(String nombre, int anosAntiguedad) {
        this.nombre = nombre;
        this.anosAntiguedad = anosAntiguedad;
    }

    // Nuevo método para identificar el tipo de contrato en el reporte
    public abstract String getTipoEmpleado();
    public abstract double calcularSalarioBruto();

    public double calcularDeducciones() {
        return calcularSalarioBruto() * 0.04; // [cite: 35] Seguro y Pensión
    }

    public double calcularSalarioNeto() {
        double bruto = calcularSalarioBruto();
        double neto = bruto - calcularDeducciones();
        return Math.max(neto, 0); // [cite: 47] Validación: No neto negativo
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
        double bono = (anosAntiguedad > 5) ? salarioFijo * 0.10 : 0; // [cite: 16]
        return salarioFijo + bono + 1000000; // [cite: 42] Bono Alimentación
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
        double pago = (horas > 40) ? (40 * tarifaHora) + ((horas - 40) * tarifaHora * 1.5) : horas * tarifaHora; // [cite: 19]
        if (anosAntiguedad > 1 && aceptaFondo) pago -= (pago * 0.02); // [cite: 45]
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
        if (ventas > 20000000) comision += (ventas * 0.03); // [cite: 28]
        return salarioBase + comision + 1000000; // [cite: 42]
    }
}

public class GestionNomina {
    private static Scanner sc = new Scanner(System.in);

    private static double leerNumero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String entrada = sc.next().replace(".", "").replace(",", "."); // Manejo de puntos/comas
                return Double.parseDouble(entrada);
            } catch (Exception e) {
                System.out.println("¡ERROR! Ingrese un valor numérico válido.");
                sc.nextLine();
            }
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("¡ERROR! Ingrese solo el número.");
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
            opcion = leerEntero("Seleccione una opción: ");
            sc.nextLine();

            if (opcion >= 1 && opcion <= 3) {
                System.out.print("Nombre completo: ");
                String nombre = sc.nextLine();
                int anos = leerEntero("Años en la empresa: ");

                switch (opcion) {
                    case 1 -> {
                        double sueldo = leerNumero("Salario mensual fijo: ");
                        listaEmpleados.add(new EmpleadoAsalariado(nombre, anos, sueldo));
                    }
                    case 2 -> {
                        double tarifa = leerNumero("Tarifa por hora: ");
                        int hrs = leerEntero("Horas trabajadas: ");
                        System.out.print("¿Acepta fondo de ahorro? (true/false): ");
                        boolean fondo = sc.nextBoolean();
                        listaEmpleados.add(new EmpleadoPorHoras(nombre, anos, tarifa, hrs, fondo));
                    }
                    case 3 -> {
                        double base = leerNumero("Salario base: ");
                        double vtas = leerNumero("Total ventas del mes: ");
                        listaEmpleados.add(new EmpleadoComision(nombre, anos, base, vtas));
                    }
                }
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
    }
}