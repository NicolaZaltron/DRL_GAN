package reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import static basicMap.Settings.printErrorMsg;
import static basicMap.Settings.printWarnMsg;

public class ZeldaReader {

	static Map<Character, Integer> tiles = new HashMap();

	static {
		tiles.put('w', 0);
		tiles.put('A', 7);
		tiles.put('+', 2);
		tiles.put('g', 3);
		tiles.put('1', 4); // Enemy
		tiles.put('2', 5); // Enemy
		tiles.put('3', 6); // Enemy
		tiles.put('.', 1); // Default
	}

	// Is this width value may be too small for the GAN to learn well?
	static int targetWidth = 13;

	public static void main(String[] args) throws Exception {
		String dir = System.getProperty("user.dir");
		System.out.println("Working Directory = " + dir);
		dir += "/marioaiDagstuhl/";
		// String inputFile = "data/zelda/example.txt";

		String inputDirectory = dir + "data/zelda/levels/";

		String outputFile = dir + "data/zelda/exampleZelda.json";
		String outputdir = dir + "data/zelda/";

		// need to iterate over all the files in a directory

		ArrayList<int[][]> examples = new ArrayList<>();

		File file = new File(inputDirectory);
		String[] fileList = file.list();

		// System.out.println(fileList);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		for (String inputFile : fileList) {
			try {
				System.out.println("Reading: " + inputFile);
				int[][] level = readLevel(new Scanner(new FileInputStream(inputDirectory + inputFile)));
				addData(examples, level);
				System.out.println(level);
				System.out.println("Read: " + inputFile);

				ArrayList<int[][]> examplesTmp = new ArrayList<>();
				addData(examplesTmp, level);
				String outTmp = gson.toJson(examplesTmp);
				System.out.println("Created JSON String");

				String withoutTxt = inputFile.substring(0, inputFile.indexOf(".txt"));

				PrintWriter writerTmp = new PrintWriter(outputdir + "example" + withoutTxt + ".json");
				writerTmp.print(outTmp);
				writerTmp.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// System.out.println(examples);

		System.out.println("Processed examples");

		String out = gson.toJson(examples);
		System.out.println("Created JSON String");

		// System.out.println(out);

		PrintWriter writer = new PrintWriter(outputFile);

		writer.print(out);
		writer.close();

		System.out.println("Wrote file with " + examples.size() + " examples");

	}

	static void addData(ArrayList<int[][]> examples, int[][] level) {
		int h = level.length;

		int[][] example = new int[h][targetWidth];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < targetWidth; x++) {
				example[y][x] = level[y][x];
			}
		}
		examples.add(example);
	}

	// static int[][][][] makeExamples(int[][] level) {
	//
	// }

	static int[] oneHot(int x) {
		// System.out.println("Tiles size: " + tiles.size());
		int[] vec = new int[tiles.size()];
		// System.out.println("Index = " + x);
		vec[x] = 1;
		return vec;
	}

	static int[][] readLevel(Scanner scanner) throws Exception {
		String line;
		ArrayList<String> lines = new ArrayList<>();
		int width = 0;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			width = line.length();
			lines.add(line);
			// System.out.println(line);
		}

		int[][] a = new int[lines.size()][width];
		System.out.println("Arrays length: " + a.length);
		for (int y = 0; y < lines.size(); y++) {
			System.out.println("Processing line: " + lines.get(y));
			for (int x = 0; x < width; x++) {
				try { // Added error checking to deal with unrecognized tile types
					a[y][x] = tiles.get(lines.get(y).charAt(x));
				} catch (Exception e) {
					System.out.println("Problem on ");
					System.out.println("\ty = " + y);
					System.out.println("\tx = " + x);
					System.out.println("\tlines.get(y).charAt(x) = " + lines.get(y).charAt(x));
					System.exit(1);
				}
			}
		}

		return a;
	}

	static String arrayToString(int[][] inputArray) {
		String outputStr = "";

		// Null array
		if (inputArray == null) {
			printWarnMsg("arrayToString: null argument passed.");
			return outputStr;
		}

		// Empty array
		int nbRows = inputArray.length;
		if (nbRows == 0) {
			printWarnMsg("arrayToString: input array is empty.");
			outputStr += "[]";
			return outputStr;
		}
		// Empty array
		int nbCols = inputArray[0].length;
		if (nbCols == 0) {
			printWarnMsg("arrayToString: input array is empty.");
			outputStr += "[";
			outputStr += "[]";
			for (int i = 1; i < nbRows; i++) { // TODO: 24/11/2017 will this happen?
				outputStr += ", []";
			}
			outputStr += "]";
			return outputStr;
		}

		// Non-empty array case
		outputStr += "["; // matrix starter
		int i = 0;
		outputStr += "["; // row starter
		outputStr += inputArray[i][0];
		for (int j = 1; j < nbCols - 1; j++) { // column
			outputStr += "," + inputArray[i][j];
		}
		outputStr += "]";
		for (i = 1; i < nbRows - 1; i++) { // loop rows
			outputStr += ", ["; // row starter
			outputStr += inputArray[i][0];
			for (int j = 1; j < nbCols - 1; j++) { // column
				outputStr += "," + inputArray[i][j];
			}
			outputStr += "]";
		}
		outputStr += "]";
		return outputStr;
	}

}
