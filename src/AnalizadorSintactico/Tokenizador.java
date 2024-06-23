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
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tokenizador {

    private Map<String, Map<String, List<Integer>>> tokens; // Palabras y sus tipos y posiciones
    private Map<String, List<String>> tipoTokens; // Palabras y sus posibles tipos

    public Tokenizador() {
        this.tokens = new HashMap<>();
        this.tipoTokens = new HashMap<>();
    }

    public void tokenizar(String texto) {
        String[] palabras = texto.split("[\\s!.,;?]+");

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i].toLowerCase();

            // Verificar si la palabra ya está registrada
            if (!tokens.containsKey(palabra)) {
                tokens.put(palabra, new HashMap<>());
                if (!tipoTokens.containsKey(palabra)) {
                    asignarTipo(palabra);
                }
            }

            // Asignar el tipo de token según el contexto
            String tipo = obtenerTipo(palabra);
            if (!tokens.get(palabra).containsKey(tipo)) {
                tokens.get(palabra).put(tipo, new ArrayList<>());
            }
            tokens.get(palabra).get(tipo).add(i);
        }
    }

    private void asignarTipo(String palabra) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("¿A qué tipo(s) pertenece la palabra '" + palabra + "'?");
        System.out.println("Puede especificar múltiples tipos separados por comas.");
        System.out.println("Ejemplos de tipos: ARTICULO, SUSTANTIVO, VERBO, ADJETIVO, ADVERBIO, PRONOMBRE, CONJUNCION, PREPOSICION, OTROS");

        String tiposEntrada = scanner.nextLine().toUpperCase();
        String[] tipos = tiposEntrada.split("\\s*,\\s*");

        List<String> tipoList = new ArrayList<>();
        for (String tipo : tipos) {
            tipoList.add(tipo);
        }
        tipoTokens.put(palabra, tipoList);
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

    public void mostrarPalabrasTokens() {
        System.out.println("Listado de Palabras y Tokens Asignados:");
        for (String palabra : tokens.keySet()) {
            System.out.println("Palabra: " + palabra);
            for (String tipo : tokens.get(palabra).keySet()) {
                System.out.print("  Tipo: " + tipo);
                System.out.print(" (posiciones: ");
                List<Integer> posiciones = tokens.get(palabra).get(tipo);
                for (int i = 0; i < posiciones.size(); i++) {
                    System.out.print(posiciones.get(i));
                    if (i < posiciones.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(")");
            }
        }
    }

    public void agregarPalabrasConTokens() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese las palabras junto con sus tokens separados por ':' (ejemplo: casa:SUSTANTIVO, perro:SUSTANTIVO+VERBO):");
        String entrada = scanner.nextLine();

        String[] pares = entrada.split("\\s*,\\s*");
        for (String par : pares) {
            String[] partes = par.split(":");
            String palabra = partes[0].toLowerCase();
            String[] nuevosTipos = partes[1].split("\\s*\\+\\s*");

            if (tokens.containsKey(palabra)) {
                List<String> tiposActuales = tipoTokens.get(palabra);
                for (String nuevoTipo : nuevosTipos) {
                    if (!tiposActuales.contains(nuevoTipo)) {
                        tiposActuales.add(nuevoTipo);
                    }
                }
            } else {
                List<String> tipoList = new ArrayList<>();
                for (String tipo : nuevosTipos) {
                    tipoList.add(tipo);
                }
                tokens.put(palabra, new HashMap<>());
                tipoTokens.put(palabra, tipoList);
            }
        }
    }

    public void guardarTokens() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("tokens.txt"));
            for (String palabra : tipoTokens.keySet()) {
                bw.write(palabra + ":" + String.join(",", tipoTokens.get(palabra)));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarTokens() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("tokens.txt"));
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                String palabra = partes[0];
                String[] tipos = partes[1].split(",");
                List<String> tipoList = new ArrayList<>();
                for (String tipo : tipos) {
                    tipoList.add(tipo);
                }
                tipoTokens.put(palabra, tipoList);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarResultados(String nombreArchivo) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo));
            for (String palabra : tokens.keySet()) {
                bw.write("Palabra: " + palabra);
                for (String tipo : tokens.get(palabra).keySet()) {
                    bw.write("  Tipo: " + tipo);
                    bw.write(" (posiciones: ");
                    List<Integer> posiciones = tokens.get(palabra).get(tipo);
                    for (int i = 0; i < posiciones.size(); i++) {
                        bw.write(posiciones.get(i).toString());
                        if (i < posiciones.size() - 1) {
                            bw.write(", ");
                        }
                    }
                    bw.write(")");
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
