package ie.gmit.sw;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.MLRegression;
import org.encog.ml.MethodFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

public class NeuralNetwork {

	/*
	 * *****************************************************************************
	 * ******** NB: READ THE FOLLOWING CAREFULLY AFTER COMPLETING THE TWO LABS ON
	 * ENCOG AND REVIEWING THE LECTURES ON BACKPROPAGATION AND MULTI-LAYER NEURAL
	 * NETWORKS! YOUR SHOULD ALSO RESTRUCTURE THIS CLASS AS IT IS ONLY INTENDED TO
	 * DEMO THE ESSENTIALS TO YOU.
	 * *****************************************************************************
	 * ********
	 * 
	 * The following demonstrates how to configure an Encog Neural Network and train
	 * it using backpropagation from data read from a CSV file. The CSV file should
	 * be structured like a 2D array of doubles with input + output number of
	 * columns. Assuming that the NN has two input neurons and two output neurons,
	 * then the CSV file should be structured like the following:
	 *
	 * -0.385,-0.231,0.0,1.0 -0.538,-0.538,1.0,0.0 -0.63,-0.259,1.0,0.0
	 * -0.091,-0.636,0.0,1.0
	 * 
	 * The each row consists of four columns. The first two columns will map to the
	 * input neurons and the last two columns to the output neurons. In the above
	 * example, rows 1 an 4 train the network with features to identify a category
	 * 2. Rows 2 and 3 contain features relating to category 1.
	 * 
	 * You can normalize the data using the Utils class either before or after
	 * writing to or reading from the CSV file.
	 */
	long finishTime = 0;
	long startTime = 0;

	public NeuralNetwork(int out) {

		int inputs = Runner.vectorSize; // Change this to the number of input neurons
		int outputs = 235; // Change this to the number of output neurons

		// Configure the neural network topology.
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(inputs)); /*
																				 */
//		int numberOfHiddenLayers = (int) Math.sqrt(inputs * outputs);
		for (int i = 0; i < Runner.hiddenLayerSize; i++) {
			network.addLayer(
					new BasicLayer(new ActivationReLU(), true, (int) Runner.hiddenLayerSizeNeuronsList.get(i)));
//					new BasicLayer(new ActivationReLU(), true, 400));

		}

		network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));

		network.getStructure().finalizeStructure();
		network.reset();

		// Read the CSV file "data.csv" into memory. Encog expects your CSV file to have
		// input + output number of columns.
		DataSetCODEC dsc = new CSVDataCODEC(new File("java.csv"), CSVFormat.ENGLISH, false, inputs, outputs, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();

//		Backpropagation trainer = new Backpropagation(network, trainingSet);

		ResilientPropagation trainFolded = new ResilientPropagation(network, trainingSet);
		trainFolded.addStrategy(new RequiredImprovementStrategy(5));

//		System.out.println("Training started at......");
		startTime = System.currentTimeMillis() / 1000;

		System.out.println("Starting training.......\n");

		// Train the neural network
		int epoch = 1; // Use this to track the number of epochs
		do {

			trainFolded.iteration();
			System.out.println(trainFolded.getError());
			epoch++;
		} while (trainFolded.getError() > Runner.errorRate);
		finishTime = System.currentTimeMillis() / 1000;

		System.out.println("Training finished .....\n");

//		System.out.println("Training Finished in......"+(finishTime-startTime)+"   seconds");

		trainFolded.finishTraining();
		System.out.println("Saving the network\n");
		Utilities.saveNeuralNetwork(network, "network.nn");
		System.out.println("Network Saved....\n");
//		network.
//		double correct = 0;
//		double total = 0;

		System.out.println("Starting network testing.....");

		getNetworkAccuracy(network, trainingSet);
//		getNetworkAccuracy_2(network, trainingSet);

//

//        
		double TP = 0;
		double FN = 0;
		double TN = 0;
		double FP = 0;

		double sensitivity = TP / (TP + FN);
		double specificity = TN / (TN + FP);
		System.out.println("sensitivity = " + sensitivity);
		System.out.println("specificity = " + specificity);

	}

	public void getNetworkAccuracy(BasicNetwork network, MLDataSet trainingSet) {
		double correct = 0;
		double total = 0;
		int number_of_languages = 0;
		int Index = 0;

		double TP = 0;
		double FN = 0;
		double TN = 0;
		double FP = 0;

		for (MLDataPair pair : trainingSet) {
			total++;
			MLData output = network.compute(pair.getInput());
			MLData ideal = pair.getIdeal();
			double resultArray[] = output.getData();
			int finalIndex = 0;

			for (int i = 1; i < resultArray.length; i++) {

				if ((int) Math.round(resultArray[i]) == 1) {
					FP++;
				} else {
					TN++;
				}
				if (resultArray[i] > 0 && (resultArray[i] > resultArray[finalIndex])) {
					finalIndex = i;
				}
			}

			for (int i = 0; i < ideal.size(); i++) {
				if (ideal.getData(i) == 1.0) {
					Index = i;
					if (Index == finalIndex) {
						correct++;
						TP++;
						FP--;
					}
					else {
						FN++;
					}
				}
			}

			number_of_languages++;

		}
		System.out.println("Testing completed with accuracy  " + (correct / total) * 100 + " %" + " in "
				+ (finishTime - startTime) + "   seconds");

		double sensitivity = TP / (TP + FN);
		double specificity = TN / (TN + FP);
		System.out.println("sensitivity = " + sensitivity);
		System.out.println("specificity = " + specificity);
	}

	public void getNetworkAccuracy_2(BasicNetwork network, MLDataSet trainingSet) {

		double TP = 0;
		double FN = 0;
		double TN = 0;
		double FP = 0;
		double correct = 0;
		double total = 0;

		// Testing data.......
		for (MLDataPair pair : trainingSet) {
			total++;
			MLData output = network.compute(pair.getInput());
			int count = 0;

//			System.out.println("--------------------"+output.size());
			for (int i = 0; i < output.size(); i++) {
				if ((int) Math.round(output.getData(i)) == 1) {
					FP++;
				} else {
					TN++;
				}

//				System.out.println((int) Math.round(output.getData(i))+"=---------------="+output.size()+"=---------------="+(int)pair.getIdeal().getData(i));
				if ((int) pair.getIdeal().getData(i) == 1)
					if ((int) Math.round(output.getData(i)) == (int) pair.getIdeal().getData(i)) {
						correct++;
						TP++;
						FP--;

					} else {
						FN++;
					}

			}
//			int y = (int) Math.round(output.getData(0));
//			int yd = (int) pair.getIdeal().getData(0);
		}

		System.out.println("-----Testing completed    " + (correct / total) * 100 + "     in    "
				+ (finishTime - startTime) + "   seconds");

		double sensitivity = TP / (TP + FN);
		double specificity = TN / (TN + FP);
		System.out.println("sensitivity = " + sensitivity);
		System.out.println("specificity = " + specificity);
	}

}