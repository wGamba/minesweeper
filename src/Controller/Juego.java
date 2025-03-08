package Controller;

import Model.Celda;
import Model.Tablero;
import View.BuscaMinasApp;

public class Juego {
    private final Tablero tablero;
    private final BuscaMinasApp vista;
    private final int maxBanderas;
    private boolean[][] celdasReveladas;
    private boolean juegoTerminado;
    private int banderasMarcadas;

    private static final int MINAS_ADYACENTES_0 = 0;
    private static final String MENSAJE_VICTORIA = "¡Winner Winner Chicken Dinner!";
    private static final String MENSAJE_DERROTA = "Perdiste :c";

    public Juego(int filas, int columnas, int numMinas, BuscaMinasApp vista) {
        this.tablero = new Tablero(filas, columnas, numMinas);
        this.vista = vista;
        this.maxBanderas = numMinas;
        this.celdasReveladas = new boolean[filas][columnas];
        this.juegoTerminado = false;
        this.banderasMarcadas = 0;
        vista.actualizarContadorBanderas(maxBanderas);
    }

    // Métodos de selección y revelación de celdas
    
    public void seleccionarCelda(int fila, int columna) {
        if (juegoTerminado || estaCeldaRevelada(fila, columna)) return;

        Celda celda = tablero.getCelda(fila, columna);
        if (!celda.estaMarcada()) {
            revelarCelda(fila, columna, celda);
        }
        verificarYTerminarJuego();
    }

    private void revelarCelda(int fila, int columna, Celda celda) {
        celda.revelar();
        celdasReveladas[fila][columna] = true;

        if (celda.esMina()) {
            terminarJuego(false);  // Aquí llamamos a terminarJuego con false para indicar derrota
        } else {
            manejarCeldaNoMina(fila, columna, celda);
            verificarYTerminarJuego();
        }
    }

    private void manejarCeldaNoMina(int fila, int columna, Celda celda) {
        if (celda.estaMarcada()) {
            desmarcarCelda(fila, columna);
        }
        mostrarTextoCelda(fila, columna, celda);
        if (celda.getMinasAdyacentes() == MINAS_ADYACENTES_0) {
            revelarAdyacentes(fila, columna);
        }
    }

    private void mostrarTextoCelda(int fila, int columna, Celda celda) {
        String textoCelda = celda.getMinasAdyacentes() == MINAS_ADYACENTES_0 ? "" : String.valueOf(celda.getMinasAdyacentes());
        vista.actualizarBoton(fila, columna, textoCelda);
    }

    private void revelarAdyacentes(int fila, int columna) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nuevaFila = fila + i;
                int nuevaColumna = columna + j;
                if (tablero.esPosicionValida(nuevaFila, nuevaColumna) && !estaCeldaRevelada(nuevaFila, nuevaColumna)) {
                    Celda celdaAdyacente = tablero.getCelda(nuevaFila, nuevaColumna);
                    if (celdaAdyacente.estaMarcada() && !celdaAdyacente.esMina()) desmarcarCelda(nuevaFila, nuevaColumna);
                    seleccionarCelda(nuevaFila, nuevaColumna);
                }
            }
        }
    }
    
    // Métodos de marcado y desmarcado de celdas

   public void marcarCelda(int fila, int columna) {
    if (juegoTerminado || estaCeldaRevelada(fila, columna)) return;

    Celda celda = tablero.getCelda(fila, columna);
    if (celda.estaMarcada()) {
        desmarcarCelda(fila, columna); // Desmarcar si ya está marcada
    } else {
        cambiarMarcadoCelda(fila, columna, true); // Marcar si no está marcada
    }
    verificarYTerminarJuego();
}

    private void desmarcarCelda(int fila, int columna) {
        cambiarMarcadoCelda(fila, columna, false);
    }

    private void cambiarMarcadoCelda(int fila, int columna, boolean marcar) {
    Celda celda = tablero.getCelda(fila, columna);
    if (marcar) {
        if (hayBanderasDisponibles() && !celda.estaMarcada()) {
            celda.marcar();
            banderasMarcadas++;
            vista.actualizarBotonConBandera(fila, columna);
        }
    } else {
        if (celda.estaMarcada()) {
            celda.desmarcar();
            banderasMarcadas--;
            vista.actualizarBotonSinBandera(fila, columna);
        }
    }
    vista.actualizarContadorBanderas(maxBanderas - banderasMarcadas);
}

    private boolean hayBanderasDisponibles() {
        return banderasMarcadas < maxBanderas;
    }
    
     // Métodos de verificación de estado del juego

    private void verificarYTerminarJuego() {
    if (juegoTerminado) return;  // Evita verificar o terminar el juego si ya ha terminado
    if (verificarVictoria()) {
        terminarJuego(true);
    }
}

    private boolean verificarVictoria() {
        for (int i = 0; i < tablero.getFilas(); i++) {
            for (int j = 0; j < tablero.getColumnas(); j++) {
                Celda celda = tablero.getCelda(i, j);
                if (!celda.esMina() && !estaCeldaRevelada(i, j)) return false;
            }
        }
        return true;
    }

    private void terminarJuego(boolean victoria) {
        juegoTerminado = true;
        vista.juegoEnCurso = false;
        revelarTablero();
        vista.mostrarMensaje(victoria ? MENSAJE_VICTORIA : MENSAJE_DERROTA);
        vista.desactivarCeldas();
    }

    private void revelarTablero() {
        for (int i = 0; i < tablero.getFilas(); i++) {
            for (int j = 0; j < tablero.getColumnas(); j++) {
                Celda celda = tablero.getCelda(i, j);
                if (celda.esMina()) {
                    vista.actualizarBotonConMina(i, j);
                } else {
                    String texto = celda.getMinasAdyacentes() == MINAS_ADYACENTES_0 ? "" : String.valueOf(celda.getMinasAdyacentes());
                    vista.actualizarBoton(i, j, texto);
                }
            }
        }
    }
    
    // Métodos de utilidad
    
    public boolean estaCeldaRevelada(int fila, int columna) {
        return celdasReveladas[fila][columna];
    }

    public int getNumBanderasDisponibles() {
        return maxBanderas - banderasMarcadas;
    }
}
