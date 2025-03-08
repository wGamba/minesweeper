package Model;

import java.util.Random;

/**
 * Representa el tablero del juego de BuscaMinas.
 */
public class Tablero {
    private final Celda[][] celdas;
    private final int filas;
    private final int columnas;
    private final int numMinas;

    public Tablero(int filas, int columnas, int numMinas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("El tablero debe tener un tamaño válido (filas y columnas mayores a 0).");
        }
        if (numMinas <= 0 || numMinas >= filas * columnas) {
            throw new IllegalArgumentException("El número de minas debe ser mayor a 0 y menor que el número total de celdas.");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.numMinas = numMinas;
        this.celdas = new Celda[filas][columnas];
        inicializarTablero();
        colocarMinas();
        calcularMinasAdyacentes();
    }

    public void reiniciar() {
        inicializarTablero();
        colocarMinas();
        calcularMinasAdyacentes();
    }

    private void inicializarTablero() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                celdas[i][j] = new Celda(false);
            }
        }
    }

    private void colocarMinas() {
        Random rand = new Random();
        int minasColocadas = 0;
        while (minasColocadas < numMinas) {
            int fila = rand.nextInt(filas);
            int columna = rand.nextInt(columnas);
            if (!celdas[fila][columna].esMina()) {
                celdas[fila][columna] = new Celda(true);
                minasColocadas++;
            }
        }
    }

    private void calcularMinasAdyacentes() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (!celdas[i][j].esMina()) {
                    int minas = contarMinasAdyacentes(i, j);
                    celdas[i][j].setMinasAdyacentes(minas);
                }
            }
        }
    }

    private int contarMinasAdyacentes(int fila, int columna) {
        int contador = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nuevaFila = fila + i;
                int nuevaColumna = columna + j;
                if (esPosicionValida(nuevaFila, nuevaColumna) && celdas[nuevaFila][nuevaColumna].esMina()) {
                    contador++;
                }
            }
        }
        return contador;
    }

    public boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public Celda getCelda(int fila, int columna) {
        if (!esPosicionValida(fila, columna)) {
            throw new IndexOutOfBoundsException("Posición fuera de los límites del tablero.");
        }
        return celdas[fila][columna];
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
}
