package Main;

import AnalizadorSintactico.AnalizadorSintactico;
import AnalizadorSintactico.Tokenizador;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Tokenizador tokenizador = new Tokenizador();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n----- Menú Principal -----");
            System.out.println("1. Tokenizar archivo de texto");
            System.out.println("2. Guardar y salir");
            System.out.println("---------------------------");
            System.out.println("Seleccione una opción:");

            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    System.out.println("Ingrese el nombre del archivo de texto para tokenizar (o 'salir' para finalizar):");
                    String nombreArchivo = scanner.nextLine();

                    if (nombreArchivo.equalsIgnoreCase("salir")) {
                        break;
                    }

                    try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
                        StringBuilder texto = new StringBuilder();
                        String linea;
                        while ((linea = br.readLine()) != null) {
                            texto.append(linea).append(" ");
                        }

                        tokenizador.tokenizar(texto.toString());

                        System.out.println("Tokenización completada para el archivo: " + nombreArchivo);

                        // Mostrar y guardar los resultados después de tokenizar
                        tokenizador.mostrarPalabrasTokens(nombreArchivo);
                        tokenizador.generarTokens(nombreArchivo, texto.toString());

                        tokenizador.numeroArchivo++; // Incrementar el número de archivo
                    } catch (IOException e) {
                        System.out.println("Error al leer el archivo: " + e.getMessage());
                    }
                    break;

                case 2:
                    // Guardar los tokens y tipos en un archivo antes de salir
                    tokenizador.guardarTokens();

                    System.out.println("¡Programa finalizado!");
                    return;

                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción del menú.");
                    break;
            }
        }
    }
}
