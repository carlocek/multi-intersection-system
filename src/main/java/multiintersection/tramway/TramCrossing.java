package multiintersection.tramway;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import multiintersection.tramway.analysis.GreenProbabilityVisitor;

/**
 * The crossing of the tram over the crossroad is intended to be an obstacle for
 * car flows. This class model this concept.
 */
public class TramCrossing {

	private double[] availability;
	private TramWay tramWay;
	private String tramLineId;
	private double[] periodicAvailability;
	private boolean analyzed;

	/**
	 * A TramCrossing, of course, has a tramWay. This is the only parameter of the
	 * constructor.
	 * 
	 * @param tramWay
	 */
	public TramCrossing(TramWay tramWay, String tramLineId) 
	{
		this.tramWay = tramWay;
		this.tramLineId=tramLineId;
		this.analyzed = false;
	}

	// Utils

	/**
	 * This launch the analysis of the tramway to get the availability of the
	 * obstacle.
	 *
	 * @param tramWayVisitor the green probability visitor we want to use for the
	 *                       analysis
	 * @param timeStep       the temporal resolution that will be used for the
	 *                       analysis
	 */
	public void analyze(GreenProbabilityVisitor tramWayVisitor, BigDecimal timeStep) {
		this.availability = tramWayVisitor.computeGreenProbability(tramWay, timeStep).getResult();
		this.periodicAvailability = tramWayVisitor.getPeriodicResult();
		this.analyzed = true;
	}

	private void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	private void setPeriodicAvailability(double[] periodicAvailability) {
		this.periodicAvailability = periodicAvailability.clone();
	}

	public TramCrossing getClone() {
		TramCrossing clone = new TramCrossing(this.tramWay, this.tramLineId);
		clone.setAvailability(this.availability);
		clone.setAnalyzed(this.analyzed);
		clone.setPeriodicAvailability(this.periodicAvailability);
		return clone;
	}

	public String getLineId(){
		return this.tramLineId;
	}

	/**
	 * It returns the availability of the TramCrossing to be passed at a certain time.
	 *
	 * @param timeStep the temporal index we are interested in
	 * @return the availability at the given index
	 */
	public double getAvailability(int timeStep) {
		if (!analyzed) {
			throw new IllegalAccessError("TramCrossing not yet analyzed. Please, invoke analyze() first.");
		}
		if (timeStep < this.availability.length)
			return getAvailabilityStandard(timeStep);
		else
			return this.periodicAvailability[(timeStep - this.availability.length) % periodicAvailability.length];
	}

	public double[] getPeriodicAvailability() {
		if (!analyzed) {
			throw new IllegalAccessError("TramCrossing not yet analyzed. Please, invoke analyze() first.");
		}

		return this.periodicAvailability;
	}

	public BigInteger getPeriod() {
		return tramWay.getHyperPeriod();
	}

	private double getAvailabilityStandard(int timeStep) {
		if (timeStep < 0)
			throw new IllegalArgumentException("Negative timeStep requested.");
		return this.availability[timeStep % availability.length];
	}

	private void setAvailability(double[] availability) {
		this.availability = availability.clone();
	}

	@Override
	public String toString() {
		return "TramCrossing [tramWay=" + tramWay + ", periodicAvailability=" + Arrays.toString(periodicAvailability)
				+ ", analyzed=" + analyzed + ", availability=" + Arrays.toString(availability) + "]";
	}

}
