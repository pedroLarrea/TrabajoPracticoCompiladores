package AnalizadorSintactico;

import java.util.HashSet;
import java.util.Set;

public class AnalizadorSintactico {
    private String texto;
    private int posicionActual;
    private Set<Character> letras;
    private Set<Character> delimitadores;
    private StringBuilder palabraActual;
    private StringBuilder erroresDetectados;

    public AnalizadorSintactico(String texto) {
        this.texto = texto;
        this.posicionActual = 0;
        this.letras = new HashSet<>();
        this.delimitadores = new HashSet<>();
        this.palabraActual = new StringBuilder();
        this.erroresDetectados = new StringBuilder();

        // Inicializar conjunto de letras
        for (char c = 'a'; c <= 'z'; c++) {
            letras.add(c);
            letras.add(Character.toUpperCase(c));
        }

        // Inicializar conjunto de delimitadores
        char[] delimitadoresArray = {' ', '\t', '\n', '.', ',', ';', ':', '!', '?', '(', ')', '[', ']', '{', '}', '<', '>'};
        for (char c : delimitadoresArray) {
            delimitadores.add(c);
        }
    }

    public String siguientePalabra() {
        palabraActual.setLength(0);  // Limpiar palabra actual

        while (posicionActual < texto.length()) {
            char c = texto.charAt(posicionActual);
            posicionActual++;

            if (letras.contains(c)) {
                palabraActual.append(c);
            } else if (delimitadores.contains(c)) {
                if (palabraActual.length() > 0) {
                    return palabraActual.toString();
                }
            } else {
                erroresDetectados.append(c);
                if (palabraActual.length() > 0) {
                    return palabraActual.toString();
                }
            }
        }

        if (palabraActual.length() > 0) {
            return palabraActual.toString();
        }
        return null;  // No quedan más palabras
    }

    public String getErrores() {
        return erroresDetectados.toString();
    }
}