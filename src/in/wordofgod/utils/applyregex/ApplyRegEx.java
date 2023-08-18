/**
 * 
 */
package in.wordofgod.utils.applyregex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ApplyRegEx {

	private static String sourceDirectory;
	private static String regExFilePath;
	private static String outputDirectory;
	private static Map<String, String> regExMap = new HashMap<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (!validateInput(args)) {
			return;
		}
		applyRegEx();
	}

	private static void applyRegEx() {
		System.out.println("Started applying regex...");

		BufferedReader reader = null;
		File directory = new File(sourceDirectory);

		for (File file : directory.listFiles()) {
			System.out.println("Reading the file: " + file.getName());

			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				reader = new BufferedReader(isr);
				String line = reader.readLine();

				while (line != null) {
					for(String key: regExMap.keySet()) {
						line = line.replaceAll(key, regExMap.get(key));
						//System.out.println(line);
					}
					line = reader.readLine();
				}

				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Applying regex is completed...");
	}

	private static void loadRegExList() {
		System.out.println("Loading list of RegEx to be applied...");
		try {
			List<String> list = Files.readAllLines(new File(regExFilePath).toPath(), Charset.defaultCharset());
			System.out.println("RegEx Obtained: " + list);
			for(String str: list) {
				String temp[] = str.split(";replaceWith=");
				if(temp.length>1) {
					regExMap.put(temp[0].replace("regEx=", ""), temp[1]);
				}else {
					regExMap.put(temp[0].replace("regEx=", ""), "");
				}
			}			
			System.out.println("RegEx Formatted: " + regExMap);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean validateInput(String[] args) {
		if (args.length == 0) {
			printErrorMessage();
			return false;
		} else {

			if (args[0].contains("sourceDir")) {
				sourceDirectory = args[0].split("=")[1];
			} else if (args[1].contains("sourceDir")) {
				sourceDirectory = args[1].split("=")[1];
			} else {
				printErrorMessage();
				return false;
			}

			if (sourceDirectory == null || sourceDirectory.isBlank()) {
				printErrorMessage();
				return false;
			}

			if (args[0].contains("regexFile")) {
				regExFilePath = args[0].split("=")[1];
			} else if (args[1].contains("regexFile")) {
				regExFilePath = args[1].split("=")[1];
			} else {
				printErrorMessage();
				return false;
			}

			if (regExFilePath == null || regExFilePath.isBlank()) {
				printErrorMessage();
				return false;
			}

			File folder = new File(sourceDirectory);
			if (!folder.exists()) {
				System.out.println("sourceDir " + sourceDirectory + " does not exists");
				return false;
			}
			if (!folder.isDirectory()) {
				System.out.println("sourceDir " + sourceDirectory + " should be a directory not a file");
				return false;
			}

			File regExFile = new File(regExFilePath);
			if (!regExFile.exists()) {
				System.out.println("regexFile " + regExFilePath + " does not exists");
				return false;
			}
			if (!regExFile.isFile()) {
				System.out.println("regexFile " + regExFilePath + " should be a file not a directory");
				return false;
			}

			if (folder.listFiles().length == 0) {
				System.out.println("Directory " + sourceDirectory + " does not have any files.");
				return false;
			}

			loadRegExList();
			if (regExMap == null || regExMap.size() == 0) {
				System.out.println("No RegEx found in the file " + regExFilePath);
				return false;
			}

			if (sourceDirectory.contains("\\")) {
				outputDirectory = sourceDirectory.substring(sourceDirectory.lastIndexOf("\\"),
						sourceDirectory.length());
			} else {
				outputDirectory = sourceDirectory.substring(0, sourceDirectory.length());
			}
			outputDirectory = outputDirectory + "-results";
		}
		return true;
	}

	private static void printErrorMessage() {
		System.out.println("Please input source folder name/path & regex file name/path....");
		System.out.println(
				"\nPlease note, if you have more than 1 RegEx to be applied, then keep only one RegEx at each line");
		System.out.println(
				"RegEx file should contain the values this way: regEx=<your regular expression>;replaceWith=<matching data to be replaced with>");
		System.out.println(
				"Example 1 (to replace all 'dummytext' with the string 'actualtext'): regEx=dummytext;replaceWith=actualtext");
		System.out.println(
				"Example 2 (to replace all numbers with the character 'N'): regEx=[0-9]*;replaceWith=N");
		System.out.println(
				"\nSyntax to run this progam:\njava -jar apply-regex-to-files.jar sourceDir=<Source Folder Name or Path> regexFile=<RegEx file name or file path>");
		System.out.println("\nExample 1: java -jar apply-regex-to-files.jar sourceDir=directory1 regexFile=regex.txt");
		System.out.println(
				"Example 2: java -jar apply-regex-to-files.jar sourceDir=\"C:/somedirectory/directory1\" regexFile=\"C:/somedirectory/regex-config.ini\"");
	}

}
