package Main;

import AnalizadorSintactico.AnalizadorSintactico;
import AnalizadorSintactico.Tokenizador;

public class Main {

    public static void main(String[] args) {
        String texto = "Hola, mundo! Esto es un texto de prueba. 123 ¡Cuidado!";
        AnalizadorSintactico analizador = new AnalizadorSintactico(texto);
        Tokenizador tokenizador = new Tokenizador();

        String palabra;
        int posicion = 0;
        while ((palabra = analizador.siguientePalabra()) != null) {
            String token = tokenizador.asignarToken(palabra);
            tokenizador.agregarPosicion(palabra, posicion);
            posicion++;
        }

        System.out.println("Errores detectados: " + analizador.getErrores());
        tokenizador.mostrarTokens();
        tokenizador.guardarTokens();
    }

}
