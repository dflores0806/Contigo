package com.spilab.monact.tensorflow;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataProcessor {

    private static final String TAG = "DataProcessor";

    private List<List<Double>> trainData;
    private List<List<Double>> testData;
    private List<List<Double>> valData;


    private List<Double> trainLabels;
    private List<Double> testLabels;
    private List<Double> valLabels;

    ////////
    private List<List<Double>> scaledTrainData ;
    private List<List<Double>> scaledTestData ;
    private List<List<Double>> scaledValData ;

    private List<Double> means ;
    private List<Double> stds ;

    private Context context;

    private String dataFileName;

    public DataProcessor(Context context, String dataFileName) {
        // Constructor vacío
        this.dataFileName=dataFileName;
        this.context=context;

        trainData = new ArrayList<>();
       testData = new ArrayList<>();
        valData = new ArrayList<>();

        trainLabels= new ArrayList<>();
        testLabels= new ArrayList<>();
        valLabels= new ArrayList<>();

        scaledTrainData=new ArrayList<>();
        scaledTestData= new ArrayList<>();
        scaledValData= new ArrayList<>();

        means= new ArrayList<>();
        stds=new ArrayList<>();

    }

    public void processData(){
        List<List<Double>> data= loadCSVData(context,dataFileName);

        Collections.shuffle(data);

        splitData(data,1,0);

        getOutputs();

        scalingData();

    }

    public List<List<Double>> loadCSVData(Context context, String dataFileName) {
        List<List<Double>> data = new ArrayList<>();

        try (InputStream inputStream = context.getAssets().open(dataFileName);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {


            csvReader.readNext();

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                List<Double> row = new ArrayList<>();

                for (String value : nextLine) {
                    row.add(Double.parseDouble(value));
                }

                data.add(row);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading CSV data", e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return data;
    }


    public List<List<Double>> splitData(List<List<Double>> data, double trainPercentage, double testPercentage) {
        int dataSize = data.size();
        int trainSize = (int) (trainPercentage * dataSize);
        int testSize = (int) (testPercentage * dataSize);




        if (trainSize + testSize <= dataSize) {

            this.trainData = new ArrayList<>(data.subList(0, trainSize));
            this.testData = new ArrayList<>(data.subList(trainSize, trainSize + testSize));
            this.valData = new ArrayList<>(data.subList(trainSize + testSize, dataSize));
        } else {

            throw new IllegalArgumentException("Percentages given result in invalid data set sizes.");
        }


        return null;
    }


    public List<Double> getOutputLabels(List<List<Double>> data) {
        List<Double> outputLabels = new ArrayList<>();

        for (List<Double> row : data) {
            outputLabels.add(row.remove(row.size() - 1)); // Remove and add to outputLabels
        }

        return outputLabels;
    }

    public List<List<Double>> getOutputs() {
        this.trainLabels = getOutputLabels(this.trainData);
        this.testLabels = getOutputLabels(this.testData);
        this.valLabels = getOutputLabels(this.valData);

        //return List.of(trainLabels, testLabels, valLabels);
        return null;
    }

    public void getTrainStats(List<List<Double>> trainData) {
        int numFeatures = trainData.get(0).size();
        means = new ArrayList<>(numFeatures);
        stds = new ArrayList<>(numFeatures);

        for (int i = 0; i < numFeatures; i++) {
            double mean = calculateMean(trainData, i);
            double std = calculateStd(trainData, i, mean);

            means.add(mean);
            stds.add(std);
        }


    }

    public List<List<Double>> normalizeData(List<List<Double>> data) {
        List<List<Double>> normalizedData = new ArrayList<>();

        for (List<Double> row : data) {
            List<Double> normalizedRow = new ArrayList<>();

            for (int i = 0; i < row.size(); i++) {
                double normalizedValue = (row.get(i) - means.get(i)) / stds.get(i);
                normalizedRow.add(normalizedValue);
            }

            normalizedData.add(normalizedRow);
        }

        return normalizedData;
    }

    public List<List<Double>> scalingData() {
        getTrainStats(trainData);

        scaledTrainData = normalizeData(this.trainData);
        scaledTestData = normalizeData(this.testData);
        scaledValData = normalizeData(this.valData);

//        scaledTrainData = (this.trainData);
//        scaledTestData = (this.testData);
//        scaledValData = (this.valData);

        //return List.of(scaledTrainData, scaledTestData, scaledValData);
        return null;
    }

    private double calculateMean(List<List<Double>> data, int columnIndex) {
        double sum = 0.0;

        for (List<Double> row : data) {
            sum += row.get(columnIndex);
        }

        return sum / data.size();
    }

    private double calculateStd(List<List<Double>> data, int columnIndex, double mean) {
        double sumSquaredDiff = 0.0;

        for (List<Double> row : data) {
            double diff = row.get(columnIndex) - mean;
            sumSquaredDiff += diff * diff;
        }

        return Math.sqrt(sumSquaredDiff / data.size());
    }

    public float[] convertToFloatArray(List<Double> doubleList) {
        int numRows = doubleList.size();
        float[] floatArray = new float[numRows];
        for (int i = 0; i < numRows; i++) {
            floatArray[i] = doubleList.get(i).floatValue();
        }
        return floatArray;
    }


    public float[][] convertToFloatFloatArray(List<List<Double>> doubleList) {
        int numRows = doubleList.size();
        int numCols = doubleList.get(0).size();

        float[][] floatArray = new float[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                floatArray[i][j] = doubleList.get(i).get(j).floatValue();
            }
        }

        return floatArray;
    }

    public List<List<Double>> getTrainData() {
        return trainData;
    }

    public List<List<Double>> getTestData() {
        return testData;
    }

    public List<List<Double>> getValData() {
        return valData;
    }

    public List<Double> getTrainLabels() {
        return trainLabels;
    }

    public List<Double> getTestLabels() {
        return testLabels;
    }

    public List<Double> getValLabels() {
        return valLabels;
    }

    public List<List<Double>> getScaledTrainData() {
        return scaledTrainData;
    }

    public List<List<Double>> getScaledTestData() {
        return scaledTestData;
    }

    public List<List<Double>> getScaledValData() {
        return scaledValData;
    }
}