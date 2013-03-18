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
		translatorMap.put("à", "a");
		translatorMap.put("á", "b");
		translatorMap.put("â", "v");
		translatorMap.put("ã", "g");
		translatorMap.put("ä", "d");
		translatorMap.put("å", "e");
		translatorMap.put("æ", "zh");
		translatorMap.put("ç", "z");
		translatorMap.put("è", "i");
		translatorMap.put("é", "j");
		translatorMap.put("ê", "k");
		translatorMap.put("ë", "l");
		translatorMap.put("ì", "m");
		translatorMap.put("í", "n");
		translatorMap.put("î", "o");
		translatorMap.put("ï", "p");
		translatorMap.put("ð", "r");
		translatorMap.put("ñ", "s");
		translatorMap.put("ò", "t");
		translatorMap.put("ó", "u");
		translatorMap.put("ô", "f");
		translatorMap.put("õ", "h");
		translatorMap.put("ö", "ts");
		translatorMap.put("÷", "ch");
		translatorMap.put("ø", "sh");
		translatorMap.put("ù", "sht");
		translatorMap.put("ú", "y");
		translatorMap.put("ü", "j");
		translatorMap.put("þ", "yu");
		translatorMap.put("ÿ", "ya");
		translatorMap.put("À", "A");
		translatorMap.put("Á", "B");
		translatorMap.put("Â", "V");
		translatorMap.put("Ã", "G");
		translatorMap.put("Ä", "D");
		translatorMap.put("Å", "E");
		translatorMap.put("Æ", "Zh");
		translatorMap.put("Ç", "Z");
		translatorMap.put("È", "I");
		translatorMap.put("É", "J");
		translatorMap.put("Ê", "K");
		translatorMap.put("Ë", "L");
		translatorMap.put("Ì", "M");
		translatorMap.put("Í", "N");
		translatorMap.put("Î", "O");
		translatorMap.put("Ï", "P");
		translatorMap.put("Ð", "R");
		translatorMap.put("Ñ", "S");
		translatorMap.put("Ò", "T");
		translatorMap.put("Ó", "U");
		translatorMap.put("Ô", "F");
		translatorMap.put("Õ", "H");
		translatorMap.put("Ö", "Ts");
		translatorMap.put("×", "Ch");
		translatorMap.put("Ø", "Sh");
		translatorMap.put("Ù", "Sht");
		translatorMap.put("Ú", "Y");
		translatorMap.put("Ü", "J");
		translatorMap.put("Þ", "Yu");
		translatorMap.put("ß", "Ya");
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
