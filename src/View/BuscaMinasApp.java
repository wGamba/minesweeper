package View;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import Controller.Juego;


public class BuscaMinasApp {
    private Button[][] botones;
    private Label contadorMinasLabel;
    private Label contadorTiempoLabel;
    private Juego juego;
    public boolean juegoEnCurso = true;
    private int filas;
    private int columnas;
    private int minas;
    private static final int SIZE = 50;
    private String tiempo = "00:00";
    private int tiempoLimite;
    private boolean modoContrarreloj = false;
    private Stage tableroStage;

// 1. Métodos de configuración y control del juego
    
    public void iniciarJuego(int filas, int columnas, int minas, int tiempoLimite) {
        this.filas = filas;
        this.columnas = columnas;
        this.minas = minas;
        this.tiempoLimite = tiempoLimite;

        // Activar el modo contrarreloj si se ha establecido un tiempo límite
        this.modoContrarreloj = tiempoLimite > 0;
        
        inicializarInterfaz();
        juego = new Juego(filas, columnas, minas, this);
        actualizarContadorBanderas(juego.getNumBanderasDisponibles());
        
        // Iniciar el temporizador adecuado
        if (modoContrarreloj) {
            iniciarTemporizadorContrarreloj();
        } else {
            iniciarContadorTiempo();
        }
    }
    
    public void inicializarInterfaz() {
        GridPane grid = new GridPane();
        botones = new Button[filas][columnas];
        double boardSize = 800;
        double cellSizeX = boardSize / columnas;
        double cellSizeY = boardSize / filas;
        double cellSize = Math.min(cellSizeX, cellSizeY);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Button btn = new Button();
                btn.setPrefSize(cellSize, cellSize);
                btn.setMinSize(cellSize, cellSize);
                btn.setMaxSize(cellSize, cellSize);

                final int fila = i;
                final int columna = j;

                btn.setStyle("-fx-background-color: #242424; -fx-background-radius: 0;");
                btn.setOnAction(e -> juego.seleccionarCelda(fila, columna));
                btn.setOnContextMenuRequested(e -> {  // Esta línea es clave para alternar la bandera
                    juego.marcarCelda(fila, columna);
                    e.consume();
                });

                btn.setOnMouseEntered(e -> {
                    if (!juego.estaCeldaRevelada(fila, columna)) {
                        btn.setStyle("-fx-border-color: blue; -fx-border-width: 2; " +
                                      "-fx-background-color: #242424; -fx-background-radius: 0;");
                    }
                });

                btn.setOnMouseExited(e -> {
                    if (!juego.estaCeldaRevelada(fila, columna)) {
                        btn.setStyle("-fx-background-color: #242424; -fx-background-radius: 0;");
                    }
                });

                botones[i][j] = btn;
                grid.add(btn, j, i);
            }
}

        contadorMinasLabel = new Label("🚩");
        contadorMinasLabel.setFont(new Font("Arial Black", 36));

        Image imagenTiempo = new Image(getClass().getResourceAsStream("/resources/reloj.png"));
        ImageView imageViewTiempo = new ImageView(imagenTiempo);
        imageViewTiempo.setFitWidth(40);
        imageViewTiempo.setFitHeight(40);

        contadorTiempoLabel = new Label("00:00");
        contadorTiempoLabel.setFont(new Font("Arial Black", 36));
        contadorTiempoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px;");

        HBox hboxTiempo = new HBox(5);
        hboxTiempo.getChildren().addAll(imageViewTiempo, contadorTiempoLabel);
        hboxTiempo.setStyle("-fx-alignment: center;");

        HBox hbox = new HBox(5);
        hbox.getChildren().addAll(contadorMinasLabel, hboxTiempo);
        hbox.setStyle("-fx-alignment: center;");

        VBox root = new VBox(10);
        root.getChildren().addAll(hbox, grid);
        root.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));
        root.setStyle("-fx-background-color: #1c1c1c;");

        Scene scene = new Scene(root);
        tableroStage = new Stage();  // Nuevo: Asignar el Stage principal a la variable de instancia
        tableroStage.setScene(scene);
        tableroStage.setTitle("BuscaMinas");
        Image minaImage = new Image(getClass().getResource("/resources/mina.png").toExternalForm());
        tableroStage.getIcons().add(minaImage);

        tableroStage.setResizable(false);
        tableroStage.setWidth(boardSize + 15);
        tableroStage.setHeight(boardSize + 112);
        tableroStage.show();
    }
    
    private void finalizarJuegoPorTiempo() {
    mostrarMensaje("Tiempo agotado. ¡Fin del juego!");
    desactivarCeldas();
    juegoEnCurso = false;
}
    
    public void desactivarCeldas() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                botones[i][j].setDisable(true);
            }
        }
    }
    
// 2. Métodos relacionados con el tiempo
    
    private void iniciarTemporizadorContrarreloj() {
    Thread temporizadorThread = new Thread(() -> {
        int tiempoRestante = tiempoLimite;
        while (juegoEnCurso && tiempoRestante > 0) {
            try {
                Thread.sleep(1000);
                if (!juegoEnCurso) break;
                tiempoRestante--;

                String tiempoFormato = String.format("%02d:%02d", tiempoRestante / 60, tiempoRestante % 60);
                Platform.runLater(() -> actualizarContadorTiempo(tiempoFormato));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (juegoEnCurso && tiempoRestante == 0) {
            Platform.runLater(this::finalizarJuegoPorTiempo);  // Esta línea es donde se hace la invocación correcta
        }
    });
    temporizadorThread.setDaemon(true);
    temporizadorThread.start();
}

    private void iniciarContadorTiempo() {
    // Iniciar el contador de tiempo
    Thread contadorThread = new Thread(() -> {
        int segundos = 0;
        int minutos = 0;
        while (juegoEnCurso) {  // Solo sigue contando mientras el juego esté en curso
            try {
                Thread.sleep(1000);  // Actualizar cada segundo
                if (!juegoEnCurso) break;  // Si el juego no está en curso, salir del bucle
                segundos++;
                if (segundos == 60) {
                    segundos = 0;
                    minutos++;
                }
                String tiempo = String.format("%02d:%02d", minutos, segundos);
                
                // Ejecutar la actualización en el hilo de la interfaz gráfica
                Platform.runLater(() -> actualizarContadorTiempo(tiempo));  // Aquí usamos runLater
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    contadorThread.setDaemon(true);
    contadorThread.start();
}

    public void actualizarContadorTiempo(String tiempo) {
        contadorTiempoLabel.setText(tiempo);
    }

// 3. Métodos de actualización de la interfaz
    
    public void actualizarContadorBanderas(int banderasRestantes) {
          // Cambiar tamaño de la imagen
          Image imagen = new Image(getClass().getResourceAsStream("/resources/bandera.png"));
          ImageView imageView = new ImageView(imagen);
          imageView.setFitWidth(40);  // Aumentamos el tamaño de la imagen
          imageView.setFitHeight(40);

          // Cambiar el tamaño de la fuente a un tamaño mayor
          contadorMinasLabel.setGraphic(imageView);
          contadorMinasLabel.setText(" " + banderasRestantes);
          contadorMinasLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px;"); 
      }

    public void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("BuscaMinas");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Image minaImage = new Image(getClass().getResourceAsStream("/resources/mina.png"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(minaImage);
        alert.setGraphic(null);
        alert.getDialogPane().setStyle("-fx-background-color: #000000;");
        alert.getDialogPane().lookup(".content").setStyle("-fx-text-fill: #7539cd; -fx-font-family: Verdana; -fx-font-size: 32px;");

        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);

        okButton.setText("Aceptar");
        cancelButton.setText("Reiniciar");

        okButton.setOnAction(e -> Platform.exit());
        cancelButton.setOnAction(e -> {
            tableroStage.close();  // Nuevo: Cerrar la ventana del tablero
            new MenuDificultadApp().start(new Stage());
            alert.close();
        });

        alert.showAndWait();
    }

    public void actualizarBoton(int fila, int columna, String texto) {
        botones[fila][columna].setText("");
        botones[fila][columna].setFont(new Font("Arial Black", 24));

        if (texto.equals("🚩")) {
            actualizarBotonConBandera(fila, columna);
        } else if (!texto.isEmpty()) {
            botones[fila][columna].setStyle("-fx-background-color: #9659fe; -fx-text-fill: white; -fx-background-radius: 0;");
            botones[fila][columna].setText(texto);
        } else {
            botones[fila][columna].setStyle("-fx-background-color: #7539cd; -fx-text-fill: white; -fx-background-radius: 0;");
        }
    }

    public void actualizarBotonConBandera(int fila, int columna) {
    Image imagen = new Image(getClass().getResourceAsStream("/resources/bandera.png"));
    ImageView imageView = new ImageView(imagen);
    imageView.setFitWidth(SIZE);
    imageView.setFitHeight(SIZE);
    botones[fila][columna].setGraphic(imageView);
    botones[fila][columna].setStyle("-fx-background-color: #242424; -fx-background-radius: 0;");
}

    public void actualizarBotonConMina(int fila, int columna) {
        Image imagen = new Image(getClass().getResourceAsStream("/resources/mina.png"));
        ImageView imageView = new ImageView(imagen);
        imageView.setFitWidth(SIZE);
        imageView.setFitHeight(SIZE);
        botones[fila][columna].setGraphic(imageView);
        botones[fila][columna].setStyle("-fx-background-color: #242424; -fx-background-radius: 0;");
    }

    public void limpiarBoton(int fila, int columna) {
        botones[fila][columna].setGraphic(null);
        botones[fila][columna].setText("");
        botones[fila][columna].setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 0;");
    }

    public void actualizarBotonConImagen(int fila, int columna, ImageView imageView) {
        botones[fila][columna].setGraphic(imageView);
        botones[fila][columna].setText("");
    }

    public void actualizarEstiloBoton(int fila, int columna, String estilo) {
        botones[fila][columna].setStyle(estilo);
    }

   public void actualizarBotonSinBandera(int fila, int columna) {
    botones[fila][columna].setGraphic(null);
    botones[fila][columna].setText("");
    botones[fila][columna].setStyle("-fx-background-color: #242424; -fx-background-radius: 0;");
}
    
}
