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
		translatorMap.put("ö", "c");
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
		translatorMap.put("Ö", "C");
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
