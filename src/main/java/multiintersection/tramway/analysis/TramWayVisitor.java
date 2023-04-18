package multiintersection.tramway.analysis;

import multiintersection.tramway.TramLine;
import multiintersection.tramway.pn.PetriNetTramTrack;

/**
 * This is an interface for visitors that do analysis on tramways.
 */
public interface TramWayVisitor {

	/**
	 * The visit method for TramLine objects.
	 * 
	 * @param tramLine
	 */
	public void visit(TramLine tramLine);

	/**
	 * The visit method for PetriNetTramTrack objects.
	 * 
	 * @param petriNetTramWay
	 */
	public void visit(PetriNetTramTrack petriNetTramWay);

}