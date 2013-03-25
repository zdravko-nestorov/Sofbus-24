package bg.znestorov.sofbus24.utils;

import java.util.HashMap;
import java.util.Map;

public class TranslatorLatinToCyrillic {

	private static final Map<String, String> translatorMap;
	static {
		translatorMap = new HashMap<String, String>();
		translatorMap.put("a", "�");
		translatorMap.put("b", "�");
		translatorMap.put("v", "�");
		translatorMap.put("g", "�");
		translatorMap.put("d", "�");
		translatorMap.put("e", "�");
		translatorMap.put("zh", "�");
		translatorMap.put("z", "�");
		translatorMap.put("i", "�");
		translatorMap.put("j", "�");
		translatorMap.put("k", "�");
		translatorMap.put("l", "�");
		translatorMap.put("m", "�");
		translatorMap.put("n", "�");
		translatorMap.put("o", "�");
		translatorMap.put("p", "�");
		translatorMap.put("r", "�");
		translatorMap.put("s", "�");
		translatorMap.put("t", "�");
		translatorMap.put("u", "�");
		translatorMap.put("f", "�");
		translatorMap.put("h", "�");
		translatorMap.put("c", "�");
		translatorMap.put("ch", "�");
		translatorMap.put("sh", "�");
		translatorMap.put("sht", "�");
		translatorMap.put("y", "�");
		translatorMap.put("yu", "�");
		translatorMap.put("ya", "�");
		translatorMap.put("A", "�");
		translatorMap.put("B", "�");
		translatorMap.put("V", "�");
		translatorMap.put("G", "�");
		translatorMap.put("D", "�");
		translatorMap.put("E", "�");
		translatorMap.put("ZH", "�");
		translatorMap.put("Z", "�");
		translatorMap.put("I", "�");
		translatorMap.put("J", "�");
		translatorMap.put("K", "�");
		translatorMap.put("L", "�");
		translatorMap.put("M", "�");
		translatorMap.put("N", "�");
		translatorMap.put("O", "�");
		translatorMap.put("P", "�");
		translatorMap.put("R", "�");
		translatorMap.put("S", "�");
		translatorMap.put("T", "�");
		translatorMap.put("U", "�");
		translatorMap.put("F", "�");
		translatorMap.put("H", "�");
		translatorMap.put("C", "�");
		translatorMap.put("CH", "�");
		translatorMap.put("SH", "�");
		translatorMap.put("SHT", "�");
		translatorMap.put("Y", "�");
		translatorMap.put("YU", "�");
		translatorMap.put("YA", "�");
	}

	private TranslatorLatinToCyrillic() {
	}

	public static String translate(String input) {
		StringBuilder output = new StringBuilder("");

		if (input != null && !"".equals(input)) {
			int j = 1;
			boolean capitalFlag = false;

			for (int i = 0; i < input.length(); i = i + j) {

				// Case there are at least 3 more symbols
				if (input.substring(i).length() > 2) {
					if (Character.isUpperCase(input.charAt(i))
							|| Character.isUpperCase(input.charAt(i + 1))
							|| Character.isUpperCase(input.charAt(i + 2))) {
						capitalFlag = true;
					} else {
						capitalFlag = false;
					}

					String latinSymbol = "" + input.charAt(i)
							+ input.charAt(i + 1) + input.charAt(i + 2);
					latinSymbol = latinSymbol.toLowerCase();
					String cyrillicSymbol = translatorMap.get(latinSymbol);

					if (cyrillicSymbol == null) {
						if (Character.isUpperCase(input.charAt(i))
								|| Character.isUpperCase(input.charAt(i + 1))) {
							capitalFlag = true;
						} else {
							capitalFlag = false;
						}

						latinSymbol = "" + input.charAt(i)
								+ input.charAt(i + 1);
						latinSymbol = latinSymbol.toLowerCase();
						cyrillicSymbol = translatorMap.get(latinSymbol);

						if (cyrillicSymbol == null) {
							latinSymbol = "" + input.charAt(i);
							cyrillicSymbol = translatorMap.get(latinSymbol);

							if (cyrillicSymbol == null) {
								output.append(latinSymbol);
								j = 1;
								continue;
							} else {
								output.append(cyrillicSymbol);
								j = 1;
								continue;
							}
						} else {
							if (capitalFlag) {
								output.append(cyrillicSymbol.toUpperCase());
							} else {
								output.append(cyrillicSymbol);
							}
							j = 2;
							continue;
						}
					} else {
						if (capitalFlag) {
							output.append(cyrillicSymbol.toUpperCase());
						} else {
							output.append(cyrillicSymbol);
						}
						j = 3;
						continue;
					}
					// Case there are 2 more symbols
				} else if (input.substring(i).length() == 2) {
					if (Character.isUpperCase(input.charAt(i))
							|| Character.isUpperCase(input.charAt(i + 1))) {
						capitalFlag = true;
					} else {
						capitalFlag = false;
					}

					String latinSymbol = "" + input.charAt(i)
							+ input.charAt(i + 1);
					latinSymbol = latinSymbol.toLowerCase();
					String cyrillicSymbol = translatorMap.get(latinSymbol);

					if (cyrillicSymbol == null) {
						latinSymbol = "" + input.charAt(i);
						cyrillicSymbol = translatorMap.get(latinSymbol);

						if (cyrillicSymbol == null) {
							output.append(latinSymbol);
							j = 1;
							continue;
						} else {
							output.append(cyrillicSymbol);
							j = 1;
							continue;
						}
					} else {
						if (capitalFlag) {
							output.append(cyrillicSymbol.toUpperCase());
						} else {
							output.append(cyrillicSymbol);
						}
						j = 2;
						continue;
					}
					// Case there is 1 more symbol
				} else if (input.substring(i).length() == 1) {
					String latinSymbol = "" + input.charAt(i);
					String cyrillicSymbol = translatorMap.get(latinSymbol);

					if (cyrillicSymbol == null) {
						output.append(latinSymbol);
						j = 1;
						continue;
					} else {
						output.append(cyrillicSymbol);
						j = 1;
						continue;
					}
				}
			}
		}

		return output.toString();
	}

}
