package AnalizadorSintactico;

import Estructuras.SimpleMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Tokenizador {

    private Map<String, Map<String, List<Integer>>> tokens; // Palabras y sus tipos y posiciones
    private Map<String, List<String>> tipoTokens; // Palabras y sus posibles tipos

    public int numeroArchivo = 1; // Contador para el número de archivo leído

    public Tokenizador() {
        this.tokens = new HashMap<>();
        this.tipoTokens = new HashMap<>();
        cargarTokens("tokens.txt"); // Cargar los tokens desde tokens.txt al iniciar el programa
    }

    public void tokenizar(String texto) {
        String[] palabras = texto.split("[\\s!.,;?]+");

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i].toLowerCase();

            // Verificar si la palabra ya tiene tipos asignados
            if (!tipoTokens.containsKey(palabra)) {
                // Si no tiene tipos asignados, asignar nuevos tipos
                asignarTipo(palabra);
            }

            // Asignar el tipo de token según el contexto
            String tipo = obtenerTipo(palabra);
            if (!tokens.containsKey(palabra)) {
                tokens.put(palabra, new HashMap<>());
            }
            if (!tokens.get(palabra).containsKey(tipo)) {
                tokens.get(palabra).put(tipo, new ArrayList<>());
            }
            tokens.get(palabra).get(tipo).add(i);
        }
    }

    private void asignarTipo(String palabra) {
        List<String> tiposAsignados = tipoTokens.getOrDefault(palabra, new ArrayList<>());

        if (tiposAsignados.isEmpty()) {
            // Si no tiene tipos asignados, asignar desde el archivo tokens.txt si existe
            if (tipoTokens.containsKey(palabra)) {
                tiposAsignados.addAll(tipoTokens.get(palabra));
            } else {
                // Si no está en tokens.txt, preguntar al usuario y asignar
                Scanner scanner = new Scanner(System.in);
                System.out.println("¿A qué tipo(s) pertenece la palabra '" + palabra + "'?");
                System.out.println("Puede especificar múltiples tipos separados por comas.");
                System.out.println("Ejemplos de tipos: ARTICULO, SUSTANTIVO, VERBO, ADJETIVO, ADVERBIO, PRONOMBRE, CONJUNCION, PREPOSICION, OTROS");

                String tiposEntrada = scanner.nextLine().toUpperCase();
                String[] tipos = tiposEntrada.split("\\s*,\\s*");

                tiposAsignados.addAll(Arrays.asList(tipos));
            }
            tipoTokens.put(palabra, tiposAsignados);
        }
    }

    private String obtenerTipo(String palabra) {
        List<String> tipos = tipoTokens.get(palabra);
        if (tipos.size() > 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("La palabra '" + palabra + "' tiene múltiples tipos: " + tipos);
            System.out.println("Seleccione el tipo adecuado para el contexto actual:");
            for (int i = 0; i < tipos.size(); i++) {
                System.out.println((i + 1) + ". " + tipos.get(i));
            }
            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea
            return tipos.get(opcion - 1);
        } else {
            return tipos.get(0);
        }
    }

    public void mostrarPalabrasTokens(String nombreArchivo) {
        System.out.println("Mostrar Tokens:");

        // Crear un mapa para almacenar las palabras procesadas por tipo y posición
        Map<String, List<String>> palabrasProcesadasPorTipo = new HashMap<>();
        Map<String, Map<String, List<Integer>>> posicionesPorTipo = new HashMap<>();

        int numeroEntrada = 1; // Variable para contar las entradas procesadas

        for (String palabra : tokens.keySet()) {
            for (String tipo : tokens.get(palabra).keySet()) {
                // Obtener la lista de palabras procesadas para el tipo actual
                List<String> palabrasProcesadas = palabrasProcesadasPorTipo.getOrDefault(tipo, new ArrayList<>());
                palabrasProcesadas.add(palabra);
                palabrasProcesadasPorTipo.put(tipo, palabrasProcesadas);

                // Obtener las posiciones para el tipo actual
                Map<String, List<Integer>> posicionesTipo = posicionesPorTipo.getOrDefault(tipo, new HashMap<>());
                posicionesTipo.put(palabra, tokens.get(palabra).get(tipo));
                posicionesPorTipo.put(tipo, posicionesTipo);
            }
        }

        // Mostrar el resultado formateado por tipo
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo + "_TABLA.txt"))) {
            for (String tipo : palabrasProcesadasPorTipo.keySet()) {
                bw.write(tipo);
                bw.newLine();
                bw.write("\tPATRÓN: " + String.join(",", tipoTokens.keySet()));
                bw.newLine();
                bw.write("\tLEXEMAS: " + String.join(",", palabrasProcesadasPorTipo.get(tipo)));
                bw.newLine();
                bw.write("\tPOSICIONES: ");
                Map<String, List<Integer>> posicionesTipo = posicionesPorTipo.get(tipo);
                for (String palabra : posicionesTipo.keySet()) {
                    List<Integer> posiciones = posicionesTipo.get(palabra);
                    for (int i = 0; i < posiciones.size(); i++) {
                        bw.write("TXT" + numeroArchivo + "-" + (posiciones.get(i) + 1));
                        if (i < posiciones.size() - 1) {
                            bw.write(", ");
                        }
                    }
                }
                bw.newLine();
                numeroEntrada++; // Incrementar el número de entrada
            }
            System.out.println("Resultados guardados en " + nombreArchivo + "_RESULTADOS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarTokens(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                String palabra = partes[0];
                String[] tipos = partes[1].split(",");
                List<String> tipoList = new ArrayList<>();
                for (String tipo : tipos) {
                    tipoList.add(tipo.trim());
                }
                tipoTokens.put(palabra.trim(), tipoList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarTokens() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("tokens.txt"))) {
            for (String palabra : tipoTokens.keySet()) {
                bw.write(palabra + ":" + String.join(",", tipoTokens.get(palabra)));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}