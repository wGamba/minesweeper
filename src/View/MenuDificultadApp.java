package View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuDificultadApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear un VBox para organizar los elementos verticalmente
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #000000; -fx-alignment: center;");

        // Cargar y mostrar el logo de la mina en la barra de la ventana
        Image minaImage = new Image(getClass().getResource("/resources/mina.png").toExternalForm());
        primaryStage.getIcons().add(minaImage); // Establecer el logo de la ventana

        // Cargar el logo en el centro de la interfaz (opcional)
        ImageView logoView = new ImageView(minaImage);
        logoView.setFitWidth(200);
        logoView.setFitHeight(200);

        // Título con fuente Verdana en negrita
        Label titulo = new Label("Modo De Juego");
        titulo.setFont(Font.font("Verdana", FontWeight.BOLD, 56));
        titulo.setStyle("-fx-text-fill: #7539cd;");

        // Botones de dificultad con estilo
        Button facilBtn = new Button("Fácil");
        Button medioBtn = new Button("Medio");
        Button dificilBtn = new Button("Difícil");
        Button contrarrelojBtn = new Button("Contrarreloj"); // Nuevo botón Contrarreloj

        // Estilo para los botones
        String buttonStyle = "-fx-background-color: #7539cd; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 32px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-padding: 10px 20px; " +
                             "-fx-background-radius: 50px;"; 

        facilBtn.setStyle(buttonStyle);
        medioBtn.setStyle(buttonStyle);
        dificilBtn.setStyle(buttonStyle);
        contrarrelojBtn.setStyle(buttonStyle); // Aplicar estilo al botón Contrarreloj

        facilBtn.setPrefWidth(600);
        medioBtn.setPrefWidth(600);
        dificilBtn.setPrefWidth(600);
        contrarrelojBtn.setPrefWidth(600); // Ajustar el ancho del botón Contrarreloj

        // Establecer acciones para cada botón
        facilBtn.setOnAction(e -> iniciarJuego(primaryStage, 8, 8, 10, 0)); // Modo Fácil sin tiempo límite
        medioBtn.setOnAction(e -> iniciarJuego(primaryStage, 12, 12, 30, 0)); // Modo Medio sin tiempo límite
        dificilBtn.setOnAction(e -> iniciarJuego(primaryStage, 16, 16, 50, 0)); // Modo Difícil sin tiempo límite

        // El botón Contrarreloj tendrá un límite de tiempo de 60 segundos
        contrarrelojBtn.setOnAction(e -> iniciarJuego(primaryStage, 12, 12, 30, 60)); // Modo Contrarreloj con tiempo límite

        // Agregar el logo, título y botones al VBox
        root.getChildren().addAll(logoView, titulo, facilBtn, medioBtn, dificilBtn, contrarrelojBtn);

        // Crear la escena y mostrarla
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setTitle("Modo de Juego");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void iniciarJuego(Stage primaryStage, int filas, int columnas, int minas, int tiempoLimite) {
        BuscaMinasApp juegoApp = new BuscaMinasApp();
        juegoApp.iniciarJuego(filas, columnas, minas, tiempoLimite);
        primaryStage.close(); // Cierra el menú de selección al iniciar el juego
    }

    public static void main(String[] args) {
        launch(args);
    }
}
