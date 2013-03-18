package bg.znestorov.sofbus24.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bg.znestorov.sofbus24.schedule_stations.Direction;
import bg.znestorov.sofbus24.station_database.GPSStation;

public class TranslatorCyrillicToLatin {

	private static final Map<String, String> translatorMap;
	static {
		translatorMap = new HashMap<String, String>();
		translatorMap.put("�", "a");
		translatorMap.put("�", "b");
		translatorMap.put("�", "v");
		translatorMap.put("�", "g");
		translatorMap.put("�", "d");
		translatorMap.put("�", "e");
		translatorMap.put("�", "zh");
		translatorMap.put("�", "z");
		translatorMap.put("�", "i");
		translatorMap.put("�", "j");
		translatorMap.put("�", "k");
		translatorMap.put("�", "l");
		translatorMap.put("�", "m");
		translatorMap.put("�", "n");
		translatorMap.put("�", "o");
		translatorMap.put("�", "p");
		translatorMap.put("�", "r");
		translatorMap.put("�", "s");
		translatorMap.put("�", "t");
		translatorMap.put("�", "u");
		translatorMap.put("�", "f");
		translatorMap.put("�", "h");
		translatorMap.put("�", "ts");
		translatorMap.put("�", "ch");
		translatorMap.put("�", "sh");
		translatorMap.put("�", "sht");
		translatorMap.put("�", "y");
		translatorMap.put("�", "j");
		translatorMap.put("�", "yu");
		translatorMap.put("�", "ya");
		translatorMap.put("�", "A");
		translatorMap.put("�", "B");
		translatorMap.put("�", "V");
		translatorMap.put("�", "G");
		translatorMap.put("�", "D");
		translatorMap.put("�", "E");
		translatorMap.put("�", "Zh");
		translatorMap.put("�", "Z");
		translatorMap.put("�", "I");
		translatorMap.put("�", "J");
		translatorMap.put("�", "K");
		translatorMap.put("�", "L");
		translatorMap.put("�", "M");
		translatorMap.put("�", "N");
		translatorMap.put("�", "O");
		translatorMap.put("�", "P");
		translatorMap.put("�", "R");
		translatorMap.put("�", "S");
		translatorMap.put("�", "T");
		translatorMap.put("�", "U");
		translatorMap.put("�", "F");
		translatorMap.put("�", "H");
		translatorMap.put("�", "Ts");
		translatorMap.put("�", "Ch");
		translatorMap.put("�", "Sh");
		translatorMap.put("�", "Sht");
		translatorMap.put("�", "Y");
		translatorMap.put("�", "J");
		translatorMap.put("�", "Yu");
		translatorMap.put("�", "Ya");
	}

	private TranslatorCyrillicToLatin() {
	}

	public static String translate(String input) {
		StringBuilder output = new StringBuilder("");

		if (input != null && !"".equals(input)) {
			boolean capitalFlag = false;

			for (int i = 0; i < input.length(); i++) {
				// Check if the first letter (but not last) is a Capital one
				if (i == 0 && i < input.length() - 1) {
					if (Character.isUpperCase(input.charAt(i + 1))) {
						capitalFlag = true;
					} else {
						capitalFlag = false;
					}
				}

				// Check if a letter between the first and last is a Capital one
				if (i > 0 && i < input.length() - 1) {
					if ((Character.isUpperCase(input.charAt(i - 1)) || input
							.charAt(i - 1) == ' ')
							&& (Character.isUpperCase(input.charAt(i + 1)) || input
									.charAt(i + 1) == ' ')
							&& !(input.charAt(i - 1) == ' ' && input
									.charAt(i + 1) == ' ')) {
						capitalFlag = true;
					} else {
						capitalFlag = false;
					}
				}

				// Check if the last letter (but not first) is a Capital one
				if (i > 0 && i == input.length() - 1) {
					if (Character.isUpperCase(input.charAt(i - 1))) {
						capitalFlag = true;
					} else {
						capitalFlag = false;
					}
				}

				String cyrillicSymbol = input.charAt(i) + "";
				String latinSymbol = translatorMap.get(cyrillicSymbol);

				if (latinSymbol == null) {
					output.append(cyrillicSymbol);
				} else {
					if (capitalFlag) {
						latinSymbol = latinSymbol.toUpperCase();
					}
					output.append(latinSymbol);
				}
			}
		}

		return output.toString();
	}

	public static String[] translate(String[] input) {
		String[] output = null;

		if (input != null && input.length != 0) {
			output = new String[input.length];

			for (int i = 0; i < input.length; i++) {
				output[i] = translate(input[i]);
			}
		}

		return output;
	}

	public static ArrayList<String> translate(ArrayList<String> input) {
		ArrayList<String> output = null;

		if (input != null && input.size() != 0) {
			output = new ArrayList<String>();

			for (int i = 0; i < input.size(); i++) {
				output.add(translate(input.get(i)));
			}
		}

		return output;
	}

	public static ArrayList<GPSStation> translateGPSStation(
			ArrayList<GPSStation> input) {
		ArrayList<GPSStation> output = null;

		if (input != null && input.size() != 0) {
			output = new ArrayList<GPSStation>();

			for (int i = 0; i < input.size(); i++) {
				GPSStation gpsStation = input.get(i);
				gpsStation.setName(translate(gpsStation.getName()));
				gpsStation.setType(translate(gpsStation.getType()));
				gpsStation.setDirection(translate(gpsStation.getDirection()));

				output.add(gpsStation);
			}
		}

		return output;
	}

	public static ArrayList<Direction> translateDirection(
			ArrayList<Direction> input) {
		ArrayList<Direction> output = null;

		if (input != null && input.size() != 0) {
			output = new ArrayList<Direction>();

			for (int i = 0; i < input.size(); i++) {
				Direction direction = input.get(i);
				direction.setVehicleType(translate(direction.getVehicleType()));
				direction.setDirection(translate(direction.getDirection()));
				direction.setStop(translate(direction.getStop()));
				direction.setStations(translate(direction.getStations()));

				output.add(direction);
			}
		}

		return output;
	}
}
