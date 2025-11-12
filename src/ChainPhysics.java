import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ChainPhysics {

	private final Canvas canvas;
	private final GraphicsContext gc;
	private final SimulationParameters params;

	// stałe fizyki
	private static final double TIME_STEP = 0.3;
	private static final int CONSTRAINT_ITERATIONS = 6;

	// zmienne symulacji
	private final List<Point2D> positions = new ArrayList<>();
	private final List<Point2D> oldPositions = new ArrayList<>();
	private boolean dragging = false;
	private int draggedIndex = -1;
	private double mouseX, mouseY;

	public ChainPhysics(Canvas canvas, SimulationParameters params) {
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
		this.params = params; 
		initializeChain();
	}

	public void initializeChain() {
		positions.clear();
		oldPositions.clear();
		double startX = canvas.getWidth() / 2;
		double startY = 100;

		// tworzenie łańcucha 
		for (int i = 0; i < params.getNumNodes(); i++) {
			Point2D pos = new Point2D(startX, startY + i * params.getLinkLength());
			positions.add(pos);
			oldPositions.add(pos);
		}
	}

	public void setupMouseHandlers(Canvas canvas) {
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			mouseX = e.getX();
			mouseY = e.getY();
			// shift - końcówka, w przeciwnym razie najbliższy węzeł
			draggedIndex = e.isShiftDown() ? params.getNumNodes() - 1 : findClosestNode(mouseX, mouseY);
			dragging = true;
		});

		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			if (dragging && draggedIndex >= 0) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});

		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
			dragging = false;
			draggedIndex = -1;
		});
	}

	private int findClosestNode(double x, double y) {
		double minDist = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < positions.size(); i++) {
			double dist = positions.get(i).distance(x, y);
			if (dist < minDist) {
				minDist = dist;
				index = i;
			}
		}
		return index;
	}

	public void updatePhysics() {
		if (positions.isEmpty()) return;
		Point2D GRAVITY = new Point2D(0, params.getGravityY());

		// aktualizacja pozycji każdego węzła
		for (int i = 1; i < params.getNumNodes(); i++) {
			Point2D current = positions.get(i);
			Point2D old = oldPositions.get(i);
			Point2D velocity = current.subtract(old).multiply(params.getDamping());
			oldPositions.set(i, current);
			positions.set(i, current.add(velocity).add(GRAVITY.multiply(TIME_STEP)));
		}

		// jeden koniec przyczepiony na stałe
		positions.set(0, new Point2D(canvas.getWidth() / 2, 100));
		oldPositions.set(0, new Point2D(canvas.getWidth() / 2, 100));

		// przeciągnięcie myszy
		if (dragging && draggedIndex >= 0) {
			positions.set(draggedIndex, new Point2D(mouseX, mouseY));
		}

		// korekcja długości segmentów
		for (int iter = 0; iter < CONSTRAINT_ITERATIONS; iter++) {
			for (int i = 0; i < params.getNumNodes() - 1; i++) {
				Point2D p1 = positions.get(i);
				Point2D p2 = positions.get(i + 1);
				Point2D diff = p2.subtract(p1);
				double dist = diff.magnitude();
				if (dist == 0) continue;

				double error = dist - params.getLinkLength();
				Point2D correction = diff.normalize().multiply(error * 0.5 * (params.isElastic() ? params.getStiffness() : 1));

				// pierwszy węzeł przyczepiony
				if (i != 0) p1 = p1.add(correction);
				p2 = p2.subtract(correction);
				positions.set(i, p1);
				positions.set(i + 1, p2);
			}
		}
	}

	public void draw() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// rysowanie liny
		gc.setStroke(Color.LIGHTBLUE);
		gc.setLineWidth(3);
		for (int i = 0; i < params.getNumNodes() - 1; i++) {
			Point2D p1 = positions.get(i);
			Point2D p2 = positions.get(i + 1);
			gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}

		// rysowanie węzłów
		for (int i = 0; i < params.getNumNodes(); i++) {
			Point2D p = positions.get(i);
			gc.setFill(i == 0 ? Color.YELLOW : (i == params.getNumNodes() - 1 ? Color.RED : Color.LIGHTGREEN));
			gc.fillOval(p.getX() - 4, p.getY() - 4, 8, 8);
		}
	}
}
