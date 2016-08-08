package bg.znestorov.sofbus24.metro.stations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Storing the results of both directions, so can be transfered from one
 * activity to another (this is why it is implementing Serializable interface)
 * 
 * @author zanio
 * 
 */
public class MetroDirectionTransfer implements Serializable {

	private static final long serialVersionUID = 1L;
	private int choice;
	private ArrayList<MetroDirection> directionsList = new ArrayList<MetroDirection>();

	public MetroDirectionTransfer() {
	}

	public MetroDirectionTransfer(MetroDirection... directions) {
		for (MetroDirection direction : directions) {
			this.directionsList.add(direction);
		}
	}

	public MetroDirectionTransfer(ArrayList<MetroDirection> directionsList) {
		this.directionsList = directionsList;
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}

	public ArrayList<MetroDirection> getDirectionsList() {
		return directionsList;
	}

	public void setDirectionsList(ArrayList<MetroDirection> directionsList) {
		this.directionsList = directionsList;
	}

	public int getDirectionsListSize() {
		return this.directionsList.size();
	}

	public List<String> getDirectionsListNames() {
		List<String> directionsListNames = new ArrayList<String>();

		for (int i = 0; i < directionsList.size(); i++) {
			directionsListNames.add(directionsList.get(i).getName());
		}

		return directionsListNames;
	}

	@Override
	public String toString() {
		return "MetroDirectionTransfer [choice=" + choice + ", directionsList=" + directionsList + "]";
	}

}
