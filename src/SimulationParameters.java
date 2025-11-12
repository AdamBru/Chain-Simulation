import javafx.beans.property.*;

public class SimulationParameters {

	private final IntegerProperty numNodes = new SimpleIntegerProperty(20);
	private final DoubleProperty linkLength = new SimpleDoubleProperty(20);
	private final DoubleProperty stiffness = new SimpleDoubleProperty(0.3);
	private final DoubleProperty damping = new SimpleDoubleProperty(0.98);
	private final DoubleProperty gravityY = new SimpleDoubleProperty(1.0);
	private final BooleanProperty elastic = new SimpleBooleanProperty(true);

	// Gettery i settery
	public IntegerProperty numNodesProperty() { return numNodes; }
	public DoubleProperty linkLengthProperty() { return linkLength; }
	public DoubleProperty stiffnessProperty() { return stiffness; }
	public DoubleProperty dampingProperty() { return damping; }
	public DoubleProperty gravityYProperty() { return gravityY; }
	public BooleanProperty elasticProperty() { return elastic; }

	public int getNumNodes() { return numNodes.get(); }
	public double getLinkLength() { return linkLength.get(); }
	public double getStiffness() { return stiffness.get(); }
	public double getDamping() { return damping.get(); }
	public double getGravityY() { return gravityY.get(); }
	public boolean isElastic() { return elastic.get(); }
}
