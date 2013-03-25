package bg.znestorov.sofbus24.utils;

import java.util.HashMap;
import java.util.Map;

public class TranslatorLatinToCyrillic {

	private static final Map<String, String> translatorMap;
	static {
		translatorMap = new HashMap<String, String>();
		translatorMap.put("a", "à");
		translatorMap.put("b", "á");
		translatorMap.put("v", "â");
		translatorMap.put("g", "ã");
		translatorMap.put("d", "ä");
		translatorMap.put("e", "å");
		translatorMap.put("zh", "æ");
		translatorMap.put("z", "ç");
		translatorMap.put("i", "è");
		translatorMap.put("j", "é");
		translatorMap.put("k", "ê");
		translatorMap.put("l", "ë");
		translatorMap.put("m", "ì");
		translatorMap.put("n", "í");
		translatorMap.put("o", "î");
		translatorMap.put("p", "ï");
		translatorMap.put("r", "ð");
		translatorMap.put("s", "ñ");
		translatorMap.put("t", "ò");
		translatorMap.put("u", "ó");
		translatorMap.put("f", "ô");
		translatorMap.put("h", "õ");
		translatorMap.put("c", "ö");
		translatorMap.put("ch", "÷");
		translatorMap.put("sh", "ø");
		translatorMap.put("sht", "ù");
		translatorMap.put("y", "ú");
		translatorMap.put("yu", "þ");
		translatorMap.put("ya", "ÿ");
		translatorMap.put("A", "À");
		translatorMap.put("B", "Á");
		translatorMap.put("V", "Â");
		translatorMap.put("G", "Ã");
		translatorMap.put("D", "Ä");
		translatorMap.put("E", "Å");
		translatorMap.put("ZH", "Æ");
		translatorMap.put("Z", "Ç");
		translatorMap.put("I", "È");
		translatorMap.put("J", "É");
		translatorMap.put("K", "Ê");
		translatorMap.put("L", "Ë");
		translatorMap.put("M", "Ì");
		translatorMap.put("N", "Í");
		translatorMap.put("O", "Î");
		translatorMap.put("P", "Ï");
		translatorMap.put("R", "Ð");
		translatorMap.put("S", "Ñ");
		translatorMap.put("T", "Ò");
		translatorMap.put("U", "Ó");
		translatorMap.put("F", "Ô");
		translatorMap.put("H", "Õ");
		translatorMap.put("C", "Ö");
		translatorMap.put("CH", "×");
		translatorMap.put("SH", "Ø");
		translatorMap.put("SHT", "Ù");
		translatorMap.put("Y", "Ú");
		translatorMap.put("YU", "Þ");
		translatorMap.put("YA", "ß");
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
