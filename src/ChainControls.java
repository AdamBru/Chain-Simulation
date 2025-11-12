import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class ChainControls {

	private final ChainPhysics chainPhysics;
	private final SimulationParameters params;
	private final GridPane controlsGrid;

	public ChainControls(ChainPhysics chainPhysics, SimulationParameters params) {
		this.chainPhysics = chainPhysics;
		this.params = params;
		this.controlsGrid = new GridPane();
		buildControls();
	}

	private void buildControls() {
		controlsGrid.setPadding(new Insets(8));
		controlsGrid.setHgap(12);
		controlsGrid.setVgap(12);
		controlsGrid.setMaxWidth(650);

		int row = 0;

		// Wiersz 0
		addSlider("Sztywność:", 0.1, 1.0, params.getStiffness(),
				val -> params.stiffnessProperty().set(val), 0, row);
		addIntSlider("Liczba ogniw:", 5, 100, params.numNodesProperty().get(),
				val -> { params.numNodesProperty().set(val); chainPhysics.initializeChain(); }, 3, row);

		row++;

		// Wiersz 1
		addSlider("Grawitacja:", 0, 2, params.getGravityY(),
				val -> params.gravityYProperty().set(val), 0, row);
		addSlider("Długość ogniwa:", 10, 50, params.getLinkLength(),
				val -> { params.linkLengthProperty().set(val); chainPhysics.initializeChain(); }, 3, row);
		row++;

		// Wiersz 2
		addSlider("Tłumienie:", 0.90, 1.0, params.getDamping(),
				val -> params.dampingProperty().set(val), 0, row);

		// Checkbox rozciągliwości
		CheckBox elasticBox = new CheckBox("Rozciągliwość");
		elasticBox.setSelected(params.isElastic());
		elasticBox.setTextFill(Color.WHITE);
		elasticBox.selectedProperty().addListener((obs, oldV, newV) -> params.elasticProperty().set(newV));
		controlsGrid.add(elasticBox, 3, row, 2, 1);
		row++;

		// Informacja dla użytkownika
		Label info = new Label("Kliknij i przeciągnij dowolne ogniwo. \nShift + przeciągnij = przeciągnięcie końca liny");
		info.setTextFill(Color.LIGHTGRAY);
		info.setLineSpacing(6);
		info.setWrapText(true);
		controlsGrid.add(info, 0, row, 6, 1);
	}

	private void addSlider(String name, double min, double max, double init,
		java.util.function.DoubleConsumer listener, int col, int row) {
		Label label = new Label(name);
		label.setTextFill(Color.WHITE);

		Slider slider = new Slider(min, max, init);
		slider.valueProperty().addListener((obs, oldV, newV) -> listener.accept(newV.doubleValue()));

		// Label pokazujący wartość suwaka
		Label valueLabel = new Label();
		valueLabel.setTextFill(Color.LIGHTGRAY);
		valueLabel.textProperty().bind(Bindings.format("%.2f", slider.valueProperty()));

		controlsGrid.add(label, col, row);
		controlsGrid.add(slider, col + 1, row);
		controlsGrid.add(valueLabel, col + 2, row);
		GridPane.setHgrow(slider, Priority.ALWAYS);
	}

	private void addIntSlider(String name, int min, int max, int init,
		java.util.function.IntConsumer listener, int col, int row) {
		Label label = new Label(name);
		label.setTextFill(Color.WHITE);

		Slider slider = new Slider(min, max, init);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setSnapToTicks(true);

		slider.valueProperty().addListener((obs, oldV, newV) -> listener.accept(newV.intValue()));

		Label valueLabel = new Label();
		valueLabel.setTextFill(Color.LIGHTGRAY);
		valueLabel.textProperty().bind(slider.valueProperty().asString("%.0f"));

		controlsGrid.add(label, col, row);
		controlsGrid.add(slider, col + 1, row);
		controlsGrid.add(valueLabel, col + 2, row);
		GridPane.setHgrow(slider, Priority.ALWAYS);
	}


	public GridPane getControlsGrid() {
		return controlsGrid;
	}
}
