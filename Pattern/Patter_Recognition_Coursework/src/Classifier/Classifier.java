package Classifier;

import java.io.FileNotFoundException;

public interface Classifier {
	public void trainClassifier(double [][] input, String [] output);
	public void writeClassifier(String filename) throws FileNotFoundException;
	public void readClassifier(String filename) throws FileNotFoundException;
	public String classify(double [] input);
}
