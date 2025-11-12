import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class ChainSimulation extends Application {

	private Canvas canvas;
	private ChainPhysics chainPhysics;
	private ChainControls chainControls;
	private SimulationParameters simParams;

	@Override
	public void start(Stage stage) {
		canvas = new Canvas(1300, 800);

		// inicjalizacja parametrów symulacji
		simParams = new SimulationParameters();

		// inicjalizacja fizyki i kontrolek z parametrami
		chainPhysics = new ChainPhysics(canvas, simParams);
		chainControls = new ChainControls(chainPhysics, simParams);

		// ustawienie płótna
		Pane canvasPane = new Pane(canvas);
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());

		// kontrolki
		ScrollPane controlScroll = new ScrollPane(chainControls.getControlsGrid());
		controlScroll.setFitToWidth(true);
		controlScroll.setPrefHeight(150);
		controlScroll.setStyle("-fx-background: #222; -fx-border-color: #333;");

		BorderPane root = new BorderPane(canvasPane);
		root.setTop(controlScroll);

		Scene scene = new Scene(root, 1300, 800);

		// obsługa myszy
		chainPhysics.setupMouseHandlers(canvas);

		// animacja
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				chainPhysics.updatePhysics();
				chainPhysics.draw();
			}
		}.start();

		stage.setScene(scene);
		stage.setTitle("Symulacja liny");
		stage.setMinWidth(650);
		stage.setMinHeight(700);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
