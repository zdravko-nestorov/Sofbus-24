package bg.znestorov.sofbus24.schedule_stations;

import java.io.Serializable;
import java.util.ArrayList;

// Storing the results of both directions, so can be transfered from one activity to another
public class DirectionTransfer implements Serializable {
	private static final long serialVersionUID = 1L;
	private int choice;
	private Direction direction1;
	private Direction direction2;

	public DirectionTransfer(ArrayList<Direction> list) {
		if (list.size() > 1) {
			this.direction1 = list.get(0);
			this.direction2 = list.get(1);
		} else {
			this.direction1 = null;
			this.direction2 = null;
		}
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}

	public Direction getDirection1() {
		return direction1;
	}

	public Direction getDirection2() {
		return direction2;
	}

	// Just for testing purposes
	public String toString() {
		return getChoice() + "\n" + getDirection1() + "\n" + getDirection2();
	}

}
