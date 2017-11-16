package bg.znestorov.sofbus24.db.utils;

import java.util.Comparator;

import bg.znestorov.sofbus24.db.entity.Vehicle;

public class VehicleAlphanumComparator implements Comparator<Vehicle> {

	private final boolean isDigit(char ch) {
		return ch >= 48 && ch <= 57;
	}

	/**
	 * Length of string is passed in for improved efficiency (only need to
	 * calculate it once)
	 **/
	private final String getChunk(String s, int slength, int marker) {

		StringBuilder chunk = new StringBuilder();
		char c = s.charAt(marker);
		chunk.append(c);
		marker++;

		if (isDigit(c)) {
			while (marker < slength) {
				c = s.charAt(marker);
				if (!isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		} else {
			while (marker < slength) {
				c = s.charAt(marker);
				if (isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		}

		return chunk.toString();
	}

	public int compare(Vehicle vehicle1, Vehicle vehicle2) {

		if (!(vehicle1 instanceof Vehicle) || !(vehicle2 instanceof Vehicle)) {
			return 0;
		}

		// First sort is by type
		String vehicle1Type = vehicle1.getType().toString();
		String vehicle2Type = vehicle2.getType().toString();
		int vehicleTypeCompare = vehicle1Type.compareToIgnoreCase(vehicle2Type);

		if (vehicleTypeCompare != 0) {
			return vehicleTypeCompare;
		} else {
			String vehicle1Number = (String) vehicle1.getNumber();
			String vehicle2Number = (String) vehicle2.getNumber();

			int thisMarker = 0;
			int thatMarker = 0;
			int s1Length = vehicle1Number.length();
			int s2Length = vehicle2Number.length();

			while (thisMarker < s1Length && thatMarker < s2Length) {
				String thisChunk = getChunk(vehicle1Number, s1Length,
						thisMarker);
				thisMarker += thisChunk.length();

				String thatChunk = getChunk(vehicle2Number, s2Length,
						thatMarker);
				thatMarker += thatChunk.length();

				// If both chunks contain numeric characters, sort them
				// numerically
				int result = 0;
				if (isDigit(thisChunk.charAt(0))
						&& isDigit(thatChunk.charAt(0))) {

					// Simple chunk comparison by length.
					int thisChunkLength = thisChunk.length();
					result = thisChunkLength - thatChunk.length();

					// If equal, the first different number counts
					if (result == 0) {
						for (int i = 0; i < thisChunkLength; i++) {
							result = thisChunk.charAt(i) - thatChunk.charAt(i);
							if (result != 0) {
								return result;
							}
						}
					}
				} else {
					result = thisChunk.compareTo(thatChunk);
				}

				if (result != 0)
					return result;
			}

			return s1Length - s2Length;
		}
	}

}