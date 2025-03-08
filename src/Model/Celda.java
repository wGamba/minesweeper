package Model;

/**
 * Clase que representa cada celda del tablero.
 */
public class Celda {
    private final boolean esMina;
    private boolean revelada;
    private boolean marcada; // Estado de marca de la celda
    private int minasAdyacentes;

    public Celda(boolean esMina) {
        this.esMina = esMina;
        this.revelada = false;
        this.marcada = false;
        this.minasAdyacentes = 0;
    }

    public boolean esMina() {
        return esMina;
    }

    public void setMinasAdyacentes(int minasAdyacentes) {
        this.minasAdyacentes = minasAdyacentes;
    }

    public int getMinasAdyacentes() {
        return minasAdyacentes;
    }

    public boolean estaRevelada() {
        return revelada;
    }

    public void revelar() {
        if (revelada) {
            throw new IllegalStateException("La celda ya ha sido revelada.");
        }
        if (marcada) {
            throw new IllegalStateException("No se puede revelar una celda marcada.");
        }
        this.revelada = true;
    }

    public boolean estaMarcada() {
        return marcada;
    }

    public void marcar() {
        if (revelada) {
            throw new IllegalStateException("No se puede marcar una celda que ya ha sido revelada.");
        }
        this.marcada = true;
    }

    public void desmarcar() {
        if (!marcada) {
            throw new IllegalStateException("La celda no está marcada.");
        }
        this.marcada = false;
    }

    public boolean isMarcada() {
        return marcada;
    }

    public void setMarcada(boolean marcada) {
        if (revelada) {
            throw new IllegalStateException("No se puede cambiar el estado de la marca de una celda revelada.");
        }
        this.marcada = marcada;
    }

    // Método adicional para reiniciar la celda
    public void reset() {
        this.revelada = false;
        this.marcada = false;
        this.minasAdyacentes = 0;
    }
}
