package AnalizadorSintactico;

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

    private Map<String, Map<String, List<String>>> lexemasIdentificados; // Palabras y sus tipos y posiciones
    private Map<String, List<String>> diccionario; // Palabras y sus posibles tipos
    private Map<String, String> eleccionUsuario; // Elección de tipo de palabra realizada por el usuario

    public int numeroArchivo = 1; // Contador para el número de archivo leído

    public Tokenizador() {
        this.lexemasIdentificados = new HashMap<>();
        this.diccionario = new HashMap<>();
        this.eleccionUsuario = new HashMap<>();
        cargarTokens("tokens.txt"); // Cargar los tokens desde tokens.txt al iniciar el programa
    }

    public void tokenizar(String texto) {
        String[] palabras = dividirTextoEnPalabras(texto);

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i].toLowerCase();

            // Verificar si la palabra ya tiene tipos asignados
            if (!diccionario.containsKey(palabra)) {
                // Si no tiene tipos asignados, asignar nuevos tipos
                asignarTipo(palabra);
            }

            // Asignar el tipo de token según la elección del usuario o el contexto
            String tipo;
            if (eleccionUsuario.containsKey(palabra)) {
                tipo = eleccionUsuario.get(palabra);
            } else {
                tipo = obtenerTipo(palabra);
            }

            if (!lexemasIdentificados.containsKey(palabra)) {
                lexemasIdentificados.put(palabra, new HashMap<>());
            }
            if (!lexemasIdentificados.get(palabra).containsKey(tipo)) {
                lexemasIdentificados.get(palabra).put(tipo, new ArrayList<>());
            }
            lexemasIdentificados.get(palabra).get(tipo).add("TXT" + numeroArchivo + "-" + (i + 1));
        }
    }

    private String[] dividirTextoEnPalabras(String texto) {
        List<String> palabrasList = new ArrayList<>();
        StringBuilder palabraActual = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                palabraActual.append(c);
            } else if (c == ' ' || c == '!' || c == '.' || c == ',' || c == ';' || c == '?') {
                if (palabraActual.length() > 0) {
                    palabrasList.add(palabraActual.toString());
                    palabraActual.setLength(0); // Resetear el StringBuilder
                }
            }
        }

        // Añadir la última palabra si existe
        if (palabraActual.length() > 0) {
            palabrasList.add(palabraActual.toString());
        }

        // Convertir la lista de palabras a un array
        String[] palabrasArray = new String[palabrasList.size()];
        palabrasList.toArray(palabrasArray);

        return palabrasArray;
    }

    private void asignarTipo(String palabra) {
        List<String> tiposAsignados = diccionario.getOrDefault(palabra, new ArrayList<>());

        if (tiposAsignados.isEmpty()) {
            // Si no está en tokens.txt, preguntar al usuario y asignar
            Scanner scanner = new Scanner(System.in);
            System.out.println("¿A qué tipo(s) pertenece la palabra '" + palabra + "'?");
            System.out.println("Puede especificar múltiples tipos separados por comas.");
            System.out.println("Ejemplos de tipos: ARTICULO, SUSTANTIVO, VERBO, ADJETIVO, ADVERBIO, PRONOMBRE, CONJUNCION, PREPOSICION, OTROS");

            String tiposEntrada = scanner.nextLine().toUpperCase();
            String[] tipos = tiposEntrada.split("\\s*,\\s*");

            tiposAsignados.addAll(Arrays.asList(tipos));
            diccionario.put(palabra, tiposAsignados);
        }
    }

    private String obtenerTipo(String palabra) {
        if (eleccionUsuario.containsKey(palabra)) {
            return eleccionUsuario.get(palabra);
        }

        List<String> tipos = diccionario.get(palabra);
        if (tipos.size() > 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("La palabra '" + palabra + "' tiene múltiples tipos: " + tipos);
            System.out.println("Seleccione el tipo adecuado para el contexto actual:");
            for (int i = 0; i < tipos.size(); i++) {
                System.out.println((i + 1) + ". " + tipos.get(i));
            }
            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            String tipoElegido = tipos.get(opcion - 1);
            eleccionUsuario.put(palabra, tipoElegido); // Guardar la elección del usuario

            return tipoElegido;
        } else {
            String tipoUnico = tipos.get(0);
            eleccionUsuario.put(palabra, tipoUnico); // Guardar la elección del usuario
            return tipoUnico;
        }
    }

    public void mostrarPalabrasTokens(String nombreArchivo) {
        System.out.println("Mostrar Tokens:");

        // Crear un mapa para almacenar las palabras procesadas por tipo y posición
        Map<String, List<String>> palabrasProcesadasPorTipo = new HashMap<>();
        Map<String, Map<String, List<String>>> posicionesPorTipo = new HashMap<>();

        for (String palabra : lexemasIdentificados.keySet()) {
            for (String tipo : lexemasIdentificados.get(palabra).keySet()) {
                // Obtener la lista de palabras procesadas para el tipo actual
                List<String> palabrasProcesadas = palabrasProcesadasPorTipo.getOrDefault(tipo, new ArrayList<>());
                palabrasProcesadas.add(palabra);
                palabrasProcesadasPorTipo.put(tipo, palabrasProcesadas);

                // Obtener las posiciones para el tipo actual
                Map<String, List<String>> posicionesTipo = posicionesPorTipo.getOrDefault(tipo, new HashMap<>());
                posicionesTipo.put(palabra, lexemasIdentificados.get(palabra).get(tipo));
                posicionesPorTipo.put(tipo, posicionesTipo);
            }
        }

        // Mostrar el resultado formateado por tipo
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Resultados/" + nombreArchivo + "_TABLA.txt"))) {
            for (String tipo : palabrasProcesadasPorTipo.keySet()) {
                bw.write(tipo);
                bw.newLine();
                bw.write("\tPATRÓN: " + String.join(",", getPalabrasPorTipo(tipo)));
                bw.newLine();
                bw.write("\tLEXEMAS: " + String.join(",", palabrasProcesadasPorTipo.get(tipo)));
                bw.newLine();
                bw.write("\tPOSICIONES: ");
                Map<String, List<String>> posicionesTipo = posicionesPorTipo.get(tipo);
                for (String palabra : posicionesTipo.keySet()) {
                    List<String> posiciones = posicionesTipo.get(palabra);
                    for (int i = 0; i < posiciones.size(); i++) {
                        bw.write(posiciones.get(i));
                        bw.write(", ");
                    }
                }
                bw.newLine();
            }
            System.out.println("Resultados guardados en Resultados/" + nombreArchivo + "_TABLA.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private List<String> getPalabrasPorTipo(String tipo) {
        List<String> palabrasPorTipo = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : diccionario.entrySet()) {
            if (entry.getValue().contains(tipo)) {
                palabrasPorTipo.add(entry.getKey());
            }
        }
        return palabrasPorTipo;
    }

    public void generarTokens(String nombreArchivo, String texto) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Resultados/" + nombreArchivo + "_TOKENS.txt"))) {
            String[] palabras = dividirTextoEnPalabras(texto);

            for (String palabra : palabras) {
                String token = obtenerToken(palabra.toLowerCase());
                bw.write(token + " ");
            }

            System.out.println("Archivo de tokens generado: Resultados/" + nombreArchivo + "_TOKENS.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String obtenerToken(String palabra) {
        String tipo = obtenerTipo(palabra);
        return tipo.toUpperCase();
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
                diccionario.put(palabra.trim(), tipoList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarTokens() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("tokens.txt"))) {
            for (String palabra : diccionario.keySet()) {
                bw.write(palabra + ":" + String.join(",", diccionario.get(palabra)));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
