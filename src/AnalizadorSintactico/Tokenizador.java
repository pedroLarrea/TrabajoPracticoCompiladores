package AnalizadorSintactico;

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
    private Map<String, String> tokenMap;
    private Map<String, List<Integer>> tokenPositions;
    private static final String TOKEN_FILE = "tokens.txt";

    public Tokenizador() {
        tokenMap = new HashMap<>();
        tokenPositions = new HashMap<>();
        cargarTokens();
    }

    public String asignarToken(String palabra) {
        if (tokenMap.containsKey(palabra)) {
            return tokenMap.get(palabra);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Asigne un token para la palabra '" + palabra + "': ");
            String token = scanner.nextLine().toUpperCase();

            // Validar token
            if (!Arrays.asList("ARTICULO", "SUSTANTIVO", "VERBO", "ADJETIVO", "ADVERBIO", "OTROS").contains(token)) {
                token = "ERROR";
            }

            tokenMap.put(palabra, token);
            return token;
        }
    }

    public void agregarPosicion(String palabra, int posicion) {
        if (!tokenPositions.containsKey(palabra)) {
            tokenPositions.put(palabra, new ArrayList<>());
        }
        tokenPositions.get(palabra).add(posicion);
    }

    public void mostrarTokens() {
        for (Map.Entry<String, List<Integer>> entry : tokenPositions.entrySet()) {
            String palabra = entry.getKey();
            List<Integer> posiciones = entry.getValue();
            String token = tokenMap.get(palabra);
            System.out.println("Palabra: " + palabra + " | Token: " + token + " | Posiciones: " + posiciones);
        }
    }

    public void guardarTokens() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TOKEN_FILE))) {
            for (Map.Entry<String, String> entry : tokenMap.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo de tokens: " + e.getMessage());
        }
    }

    private void cargarTokens() {
        try (BufferedReader br = new BufferedReader(new FileReader(TOKEN_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":", 2);
                if (partes.length == 2) {
                    tokenMap.put(partes[0], partes[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo de tokens: " + e.getMessage());
        }
    }
}