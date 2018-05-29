package nn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class NeuralNet {
	public static void run() throws Exception {

		HashMap<double[],double[]> realData = digitData();
		HashMap<double[],double[]> trainData = new HashMap<double[],double[]>();
		HashMap<double[],double[]> testData = new HashMap<double[],double[]>();
		
		int inputSize = realData.keySet().iterator().next().length;
		int hiddenSize = 1000;
		int outputSize =  realData.get(realData.keySet().iterator().next()).length;
		double learningRate = 0.01;
		double loss;
		double trainRatio = 0.7;
		
		
		// put data in train data
		int id = 0;
		for(double[] arr : realData.keySet()) {
			if(id < realData.size() * trainRatio) {
				trainData.put(arr,realData.get(arr));
			}else {
				testData.put(arr,realData.get(arr));
			}
			id++;
			
		}
		
		System.out.println("Training size : " + trainData.size());
		System.out.println("Testing size : " + testData.size());
		
		
		// actual rs -- t
		double[] actual = new double[outputSize];
		
		//input layer
		double[] input = new double[inputSize];
		//hidden layer -- y
		double[] hidden = new double[hiddenSize];
		//output layer -- z -> o
		double[] output = new double[outputSize];
		
		//weights between input and hidden
		double[][] ih = new double[inputSize][hiddenSize];
		
		//weights between hidden and output
		double[][] ho = new double[hiddenSize][outputSize];
		
		
		//random weight
		randomInitialze(ih);
		randomInitialze(ho);
		
		int ite = 0;
		double previousLoss = 0;
		
		while(ite < 1000) {
			loss = 0.0;
			for(double[] dArr : trainData.keySet()) {
				input = dArr;
				actual = trainData.get(dArr);
				
				// forward
				
				forward(input,ih,hidden);
				forward(hidden,ho,output);
//				output = smallen(output);
				output = softmax(output);
				loss += crossEntropy(output, actual);
//				System.out.println("loss : " + loss);
				
				
				// backward
				for(int i = 0 ; i < ih.length ; ++i) {
					for(int j = 0 ; j < ih[0].length ; ++j) {
						ih[i][j] -= learningRate * input[i] * idk(output,actual,ho,j);
					}
				}
				
				for(int i = 0 ; i < ho.length ; ++i) {
					for(int j = 0 ; j < ho[0].length ; ++j) {
						ho[i][j] -= learningRate * hidden[i] * (output[j] - actual[j]);
					}
				}
			}
			System.out.println("iteration : " + ite + "\t->\tloss:" + loss);
			if((loss > previousLoss || loss < 5) && previousLoss != 0) {
				break;
			}
			ite++;
			previousLoss = loss;
		}
		
		
		// predict test data
		predict(ih,ho,testData);
		
		
	}
	
	public static int maxIndex(double[] arr) {
		int rs = 0;
		double max = arr[0];
		for(int i = 0 ; i < arr.length ; ++i) {
			if(arr[i] > max) {
				max = arr[i];
				rs = i;
			}
		}
		return rs;
	}
	
	public static int maxIndex(int[] arr) {
		int rs = 0;
		int max = arr[0];
		for(int i = 0 ; i < arr.length ; ++i) {
			if(arr[i] > max) {
				max = arr[i];
				rs = i;
			}
		}
		return rs;
	}
	
	public static void predict(double[][] ih, double[][] ho, HashMap<double[],double[]> testData) {
		int right = 0;
		int wrong = 0;
		double[] hidden = new double[ih[0].length];
		double[] op = new double[ho[0].length];
		for(double[] input : testData.keySet()) {
			System.out.println(maxIndex(testData.get(input)));
			forward(input, ih, hidden);
			forward(hidden,ho,op);
			if(maxIndex(op) == maxIndex(testData.get(input))) {
				right++;
			}else {
				wrong++;
			}
		}
		System.out.println(right + "\t" + wrong + "\t" + ((double)right / (double)(wrong+right)));
	}
	
	public static void printArr(double[] arr) {
		StringBuilder sb = new StringBuilder();
		for(double d : arr) {
//			System.out.println(d);
			try {
				sb.append(String.valueOf(d).substring(0, 7)).append("   ");
			}catch(Exception e) {
				sb.append(d).append("   ");
			}
		}
		System.out.println(sb.toString());
	}
	
	public static double idk(double[] predicted , double[] actual , double[][] ho , int noInY) {
		double rs = 0.0;
		for(int i = 0 ; i < actual.length ; ++i) {
			rs += ho[noInY][i] * (predicted[i] - actual[i]);
		}
		return rs;
	}
		
	public static void forward(double[] ip, double[][] ws, double[] op) {
		for(int i = 0 ; i < op.length ; ++i) {
			op[i] = 0.0;
			for(int j = 0 ; j < ip.length ; ++j) {
				op[i] += ip[j] * ws[j][i];
			}
		}
	}
	
	public static double[] softmax(double[] input) {
		double[] rs = new double[input.length];
		double sum = 0.0;
		for(double d : input) {
			sum += Math.exp(d);
		}
		for(int i = 0 ; i < input.length ; ++i) {
			rs[i] = Math.exp(input[i])/sum;
		}
		return rs;
	}
	
	public static double[] smallen(double[] input) {
		double[] rs = new double[input.length];
		double maxAbs = Math.abs(input[0]);
		for(double d : input) {
			if(Math.abs(d) > maxAbs) {
				maxAbs = Math.abs(d);
			}
		}
		for(int i = 0 ; i < input.length ; ++i) {
			rs[i] = input[i] / maxAbs;
		}
		return rs;
	}
	
	public static double crossEntropy(double[] predicted , double[] actual) {
		double rs = 0.0;
		for(int i = 0 ; i < predicted.length ; ++i) {
			rs -= actual[i] * Math.log10(predicted[i]);
		}
		return rs;
	}
	
	public static void randomInitialze(double[][] matrix) {
		for(int i = 0 ; i < matrix.length ; ++i) {
			for(int j = 0 ; j < matrix[0].length ; ++j) {
				matrix[i][j] = Math.random()/(double)1000;
			}
		}
	}
	
	public static HashMap<double[],double[]> data() throws Exception { 
	 	HashMap<double[],double[]> data = new HashMap<double[], double[]>(); 
		BufferedReader br = new BufferedReader(new FileReader("iris.txt"));
		String line = br.readLine();
		while(line != null) {
			String[] elements = line.split(",");
			double[] ip = new double[4];
			for(int i = 0 ; i < 4 ; ++i) {
				ip[i] = Double.parseDouble(elements[i]);
			}
			if(elements[4].equals("Iris-virginica")) {
				data.put(ip, new double[] {1.0,0.0,0.0});
			}else if (elements[4].equals("Iris-setosa")) {
				data.put(ip, new double[] {0.0,1.0,0.0});
			}else {
				data.put(ip, new double[] {0.0,0.0,1.0});
			}
			line = br.readLine();
		}
		br.close();
		return data;
	}
	
	public static HashMap<double[],double[]> digitData() throws Exception { 
	 	HashMap<double[],double[]> data = new HashMap<double[], double[]>(); 
		BufferedReader br = new BufferedReader(new FileReader("E:\\study\\text\\digit_data.txt"));
		String line = br.readLine();
		while(line != null) {
			String[] elements = line.split(" ");
			double[] ip = new double[256];
			for(int i = 0 ; i < 256 ; ++i) {
				ip[i] = Double.parseDouble(elements[i]);
			}

			double[] op = new double[10];
			for(int i = 256 ; i < 266 ; ++i) {
				op[i-256] = Double.parseDouble(elements[i]);
			}
			data.put(ip, op);
			line = br.readLine();
		}
		br.close();
		return data;
	}

}
