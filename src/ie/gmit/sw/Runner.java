package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Runner {
//	public static int ngramValue = 4;
//	public static int vectorSize = 20500;
	public static int ngramValue = 2;
	public static int vectorSize = 700;
	public static List<Integer> hiddenLayerSizeNeuronsList = new ArrayList<Integer>();
	public static int hiddenLayerSize = 1;
	public static double errorRate = 0.0015;

	public static String path = "wili-2018-Small-11750-Edited.txt";

	public static void main(String[] args) {

//		InputStream is = Runner.class.getResourceAsStream("wili-2018-Small-11750-Edited_.txt");
		new Runner().mainMenu();

		/*
		 * Each of the languages in the enum Language can be represented as a number
		 * between 0 and 234. You can map the output of the neural network and the
		 * training data label to / from the language using the following. Eg. index 0
		 * maps to Achinese, i.e. langs[0].
		 */

	}

	// Main menu list....
	public void mainMenu() {

		System.out.println("Enter your choice\n\n" + "1. To see the languages in our machine.\n\n"
				+ "2. To enter the size of ngrams \n\n" + "3. To enter the size of vector to store data,  default is "
				+ Runner.vectorSize + " \n\n"
				+ "4. To modify the number of hidden layers(By deafult 1 hidden  layer.).\n\n"
				+ "5. To modify the error rate(by default, it is " + errorRate + ")\n\n"
				+ "6. Continue to train the neural network with default values\n\n"
				+ "7. Write default training file location\n\n" + "8. To enter the text to determine its language.\n\n"
				+ "9. To exit.\n\n");

		try {
			// Read data from console.
			Scanner in = new Scanner(System.in);
			String input = in.next();

			// process based on input
			switch (input) {
			case "1":
				Language[] langs = Language.values();
				for (int i = 0; i < langs.length; i++) {
					System.out.println(i + "-->" + langs[i]);
				}
				System.out.println(" ");
				System.out.println(" ");
				System.out.println("Do u wish to continue or exit? (y/n)");
				String response = in.next();
				if (response.equals("y"))
					mainMenu();
				else if (response.equals("n"))
					System.exit(1);

				break;

			case "2":
				System.out.println("Enter the size of each nGram");
				ngramValue = in.nextInt();
				new Runner().start();
				break;

			case "3":
				System.out.println("Enter the size of each Vector");
				vectorSize = in.nextInt();
				new Runner().start();
				break;

			case "4":
				System.out.println("Enter the number of hidden layers");
				hiddenLayerSize = in.nextInt();
				for (int i = 1; i <= hiddenLayerSize; i++) {
					System.out.println("Press 1 to Enter the number of neurons in layer" + i
							+ "\n or Press 2 to continue with default 121");
					int in_ = in.nextInt();
					switch (in_) {
					case 1:
						System.out.println("Enter the size of neurons in Hidden layer" + i);
						hiddenLayerSizeNeuronsList.add(in.nextInt());
						break;

					case 2:
						hiddenLayerSizeNeuronsList.add(121);
						break;

					default:
						System.out.println("Wrong choice , Going back to main menu");
						Thread.sleep(5000);
						mainMenu();
					}
				}
				new Runner().start();
				break;

			case "5":
				errorRate = in.nextDouble();
				new Runner().start();
				break;

			case "6":
				System.out.println("");
				new Runner().start();
				break;

			case "7":
				System.out.println("Enter file path:");
				path = in.next();
				new Runner().start();
				break;

			case "8":
				Boolean isTrue = true;
				while (isTrue) {
					System.out.println("Press F to upload a file or Press S to input String");
					Scanner sc_ = new Scanner(System.in);
					String input_ = sc_.nextLine();
					File fileName = new File("test.txt");
					VectorProcessor vp = new VectorProcessor();
					if (input_.equals("F")) {
						isTrue = false;
						System.out.println("Enter full path");
						Scanner scan = new Scanner(System.in);
						String loc = scan.nextLine();
						String textData = "";
						BufferedReader br = null;
						try {
							br = new BufferedReader(new InputStreamReader(Runner.class.getResourceAsStream(loc)));
						} catch (Exception e) {try {
							br = new BufferedReader(new FileReader(loc.toString()));
						}
						catch(Exception e_) {
							System.out.println(e.getMessage());
							System.out.println("Exception occurred : Specify correct path. Going back to main menu");
							Thread.sleep(5000);
							
							mainMenu();
						}
						}
						while ((textData = br.readLine()) != null) {
							vp.saveTestFileData(textData, fileName);
							double[] vec = vp.process_();
							vp.test(vec);

						}
						isTrue = false;
						goToMainMenu();

					} else if (input_.equals("S")) {
						System.out.println("Enter String");
						Scanner sc = new Scanner(System.in);
						String testData = sc.nextLine();

						vp.saveTestData(testData);
						double[] vec = vp.process_();
						vp.test(vec);
						isTrue = false;
						goToMainMenu();
					} else {
						System.out.println("You entered wrong choice");
					}
				}
				break;

			case "9":
				System.exit(1);
				break;

			default:
				System.out.println("Please select the correct choice. Going back to main menu");
				Thread.sleep(5000);
				mainMenu();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Method to start neural networks
	public void start() {
		try {
			System.out.println("Processing.........");
			if (hiddenLayerSizeNeuronsList.size() == 0)
				hiddenLayerSizeNeuronsList.add(0, 374);
			new VectorProcessor().go();

//			for (int i = 250; i < 600; i++) {
			new NeuralNetwork(0);
			goToMainMenu();

//				i=i+10;
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void goToMainMenu() {

		Boolean isTrue = true;
		while (isTrue) {
			System.out.println("Do u want to go to main Menu, press  Y or N");
			Scanner sc = new Scanner(System.in);
			String response = sc.next();
			if (response.toUpperCase().equals("Y")) {
				isTrue = false;
				mainMenu();
			} else if (response.toUpperCase().equals("N")) {
				isTrue = false;
				System.out.println();
			} else {
				System.out.println("You entered wrong choice");
			}
		}
	}

}