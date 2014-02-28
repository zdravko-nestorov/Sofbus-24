package bg.znestorov.sofbus24.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;

/**
 * Class which is used to transcode the <b>Cyrillic</b> text to a <b>Latin</b>
 * one using a table with the symbol translations
 * 
 * @author Zdravko Nestorov
 * @version 1.0
 * 
 */
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
		translatorMap.put("�", "c");
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
		translatorMap.put("�", "C");
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

	/**
	 * Translate the input Cyrillic text to a Latin one using a table with the
	 * translation
	 * 
	 * @param context
	 *            the current activity context
	 * @param input
	 *            the input text in Latin
	 * @return the transcoded Cyrillic text in a Latin format
	 */
	public static String translate(Context context, String input) {
		StringBuilder output = new StringBuilder("");
		Locale currentLocale = new Locale(LanguageChange.getUserLocale(context));

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
						latinSymbol = latinSymbol.toUpperCase(currentLocale);
					}
					output.append(latinSymbol);
				}
			}
		}

		return output.toString();
	}

	/**
	 * Translate an array of String from Cyrillic to Latin
	 * 
	 * @param arrayToTranslate
	 *            the input array
	 * @return the translated array
	 */
	public static String[] translate(Context context, String[] arrayToTranslate) {
		String[] output = null;

		if (arrayToTranslate != null && arrayToTranslate.length != 0) {
			output = new String[arrayToTranslate.length];

			for (int i = 0; i < arrayToTranslate.length; i++) {
				output[i] = translate(context, arrayToTranslate[i]);
			}
		}

		return output;
	}

	/**
	 * Translate a list of String from Cyrillic to Latin
	 * 
	 * @param listToTranslate
	 *            the input list
	 * @return the translated list
	 */
	public static ArrayList<String> translate(Context context,
			ArrayList<String> listToTranslate) {
		ArrayList<String> output = null;

		if (listToTranslate != null && listToTranslate.size() != 0) {
			output = new ArrayList<String>();

			for (int i = 0; i < listToTranslate.size(); i++) {
				output.add(translate(context, listToTranslate.get(i)));
			}
		}

		return output;
	}
}
