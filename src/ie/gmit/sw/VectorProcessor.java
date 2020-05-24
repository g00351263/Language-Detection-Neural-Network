package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;

public class VectorProcessor {

	private double[] vector = new double[Runner.vectorSize];
	private DecimalFormat df = new DecimalFormat("###.###");
	private DecimalFormat df_ = new DecimalFormat("###.########################");
	private int n = Runner.ngramValue;
	int m = 1;
	Language[] langs = Language.values();
	public static String loc = "java.csv";

	public void go() throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Runner.class.getResourceAsStream(Runner.path)));
		} catch (Exception e_) {
			try {
				br = new BufferedReader(new FileReader(Runner.path));
			} catch (Exception e__) {
				System.out.println(e__.getMessage());
				System.out.println("\n\n <<<<<<>>>>>>>>>   Going Back to Main menu      <<<<<<>>>>>>>>>");
				try {
					Thread.sleep(5000);
				} catch (Exception excep) {
					excep.printStackTrace();
				}

				new Runner().mainMenu();
			}
		}
		try {
			File file = new File(loc);
			FileWriter fileWriter = new FileWriter(file.toString());
			String line;
			while ((line = br.readLine()) != null) {
//				line = br.readLine();
				System.out.println(m++);
//				if (line != null && line.length() > 0)
				process(line, fileWriter);
//				System.out.println("-----------------------------------------------"+m++);
			}
			fileWriter.flush();
			fileWriter.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("\n\n <<<<<<>>>>>>>>>   Going Back to Main menu      <<<<<<>>>>>>>>>");
			try {
				Thread.sleep(5000);
			} catch (Exception e_) {
				e_.printStackTrace();
			}

			new Runner().mainMenu();
		}
	}

	public double[] process_() {
		double[] vec = new double[Runner.vectorSize];
		double[] vector_ = new double[Runner.vectorSize];
		List<Double> dataList = new ArrayList<>();
		try {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("test.txt")));

			String line;
			File file = new File("test.csv");
			FileWriter fileWriter = new FileWriter(file.toString());
			while ((line = br.readLine()) != null) {
				for (int i = 0; i < vector_.length; i++)
					vector_[i] = 0;
				List<String> ngramList = new ArrayList<>();
				ngramList = ngrams(n, line);
				for (String string_ : ngramList) {
					String string = string_.toUpperCase();
					int index = string.hashCode() % vector_.length;
					try {
						vector_[index] = vector_[index] + 1;
					} catch (Exception e) {
//						System.out.println(e.getMessage());

					}
				}
				vec = Utilities.normalize(vector_, 0, 1);

				for (double d : vec) {
					dataList.add(Double.valueOf(df_.format(d)));
				}

				for (Double double_ : dataList) {
					fileWriter.append(String.valueOf(double_));
					fileWriter.append(COMMA_DELIMITER);

				}
				fileWriter.append(NEW_LINE_SEPARATOR);
				fileWriter.flush();
				fileWriter.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vec;

	}

	public void process(String line, FileWriter fileWriter) {
		try {
//			System.out.println(line+"mm------------------- "+m++);
			List<Double> dataList = new ArrayList<>();

			String[] record = line.trim().split("@");
			if (record.length > 2)
				return;
			String text = record[0].toUpperCase();
			String lang = record[1];

			for (int i = 0; i < vector.length; i++)
				vector[i] = 0;
			List<String> ngramList = new ArrayList<>();
			ngramList = ngrams(n, text);
			for (String string : ngramList) {
				int index = string.hashCode() % vector.length;
				try {
					vector[index] = vector[index] + 1;
				} catch (Exception e) {

				}
			}

			double[] vec = Utilities.normalize(vector, 0, 1);
			for (double d : vec) {
				dataList.add(Double.valueOf(df.format(d)));
			}

			int vectorLength = vec.length;
			int loc = getLangNo(dataList, lang, vectorLength);
			dataList.add(vec.length + loc, 1.0);

//		FileWriter fileWriter = null;

			try {

//			Path path = Paths.get("src\\data\\java.csv");
//			File file = new File("src\\data\\java.csv");
//			fileWriter = new FileWriter(file.toString(), true);

				// Write a new student object list to the CSV file
				for (Double double_ : dataList) {
					fileWriter.append(String.valueOf(double_));
					fileWriter.append(COMMA_DELIMITER);

				}
				fileWriter.append(NEW_LINE_SEPARATOR);

			} catch (Exception e) {
				System.out.println(e);

			}
		} catch (Exception e) {
			m++;
			e.printStackTrace();
		}

	}

	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	int iterrator;

	public int getLangNo(List<Double> dataList, String lang, int vecLength) {

		iterrator = 1;
		for (int i = 0; i < 236; i++)
			dataList.add(0.0);
		try {
			Stream.of(langs).forEach(i -> {
				if (i.toString().equals(lang)) {
					throw new ArithmeticException();
				} else {
					iterrator++;
				}

			});
		}

		catch (ArithmeticException e) {

		}
		return iterrator;
	}

	// Produce nGrams

	public List<String> ngrams(int n, String str) {
		List<String> ngramList = new ArrayList<String>();
		try {
			for (int i = 0; i < str.length() - n + 1; i++)
				// Add the substring or size n
				ngramList.add(str.substring(i, i + n));
			// In each iteration, the window moves one step forward
			// Hence, each n-gram is added to the list

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ngramList;
	}

	public void saveTestData(String data) {
		try {
			File fileName = new File("test.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveTestFileData(String data, File fileName) {
		try {
//			File fileName = new File("test.txt");
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test(double[] vector) throws Exception {
		DataSetCODEC dsc = new CSVDataCODEC(new File("test.csv"), CSVFormat.ENGLISH, false, Runner.vectorSize, 0,
				false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();
//		
//		
		BasicNetwork network = Utilities.loadNeuralNetwork("network.nn");
//
		MLData output = network.compute(trainingSet.get(0).getInput());
//		MLData mlData = new BasicMLData(vector);

//		MLData output = network.compute(new BasicMLData(vector));
		double resultArray[] = output.getData();
		int finalIndex = 0;

		for (int i = 1; i < resultArray.length; i++) {
			if (resultArray[i] > 0 && (resultArray[i] > resultArray[finalIndex])) {
				finalIndex = i;
			}
		}
		Language[] languages = Language.values();
		System.out.println("Figured language from network is " + languages[finalIndex]);
		System.out.println();
	}
}
