package com.spilab.monact.tensorflow;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TensorFlowLiteModel {

    private Interpreter interpreter;

    final private String TAG = "ContigoTF";

    private Context context;

    private String logLosses;

    public TensorFlowLiteModel(Context context, String modelFileName) {
        this.context=context;
        this.logLosses="";
        try {
            interpreter = new Interpreter(loadModelFile(context, modelFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        return FileUtil.loadMappedFile(context, modelFileName);
    }

    public void readModel(String filename) {
        File modelBuffer = new File("");
        try {
            // Load file
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, filename);
            interpreter = new Interpreter(tfliteModel);
            Log.d(TAG, "File OK");
        } catch (Exception e) {
            Log.d(TAG, "File/Restore ERROR");
            throw new RuntimeException(e);
        }
    }

    public float[][] runInference(float[][] inputData) {

        Log.e("SIZE INPUT", String.valueOf(inputData.length));

        float[][] output = new float[inputData.length][2];  // Ajusta el primer parámetro del output segun los datos a predecir

        interpreter.run(inputData, output);

        return output;
    }

    public void saveModel(){

            // Conduct the training jobs.

            // Export the trained weights as a checkpoint file.
            File outputFile = new File(context.getFilesDir(), "checkpoint.ckpt");
            //Delete if there is other
            outputFile.delete();


            Map<String, Object> inputs = new HashMap<>();
            inputs.put("checkpoint_path", outputFile.getAbsolutePath());
            Map<String, Object> outputs = new HashMap<>();
            interpreter.runSignature(inputs, outputs, "save");

            Log.i("SAVE MODEL", "saved...");

    }


    public void restoreModel() {
        File outputFile = new File(context.getFilesDir(), "checkpoint.ckpt");
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("checkpoint_path", outputFile.getAbsolutePath());
        Map<String, Object> outputs = new HashMap<>();
        interpreter.runSignature(inputs, outputs, "restore");
    }

    private File copyFileFromAssets(Context context, String assetFileName) throws IOException {
        File file = new File(context.getFilesDir(), assetFileName);
        if (!file.exists()) {
            InputStream inputStream = context.getAssets().open(assetFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        }
        return file;
    }

    public void restoreModelV2(String checkpointFileName) {
        File outputFile = null;
        try {
            outputFile = copyFileFromAssets(context, checkpointFileName);
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("checkpoint_path", outputFile.getAbsolutePath());
            Map<String, Object> outputs = new HashMap<>();
            interpreter.runSignature(inputs, outputs, "restore");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public float[][] makeInfer(float[][] inputData){

        // Run the inference.
        Map<String, Object> inputs = new HashMap<>();

        Map<String, Object> outputs = new HashMap<>();

        float[][] output = new float[inputData.length][2];

        inputs.put("x", inputData);

        outputs.put("output", output);


        interpreter.runSignature(inputs, outputs, "infer");

        return (float[][]) outputs.get("output");
    }


    public String getLogLosses() {
        return logLosses;
    }

    /**
     * POST: Retrain model with the new predicted image

     */
    public void retrainModel(float[][] trainX,float[] trainY) {


        logLosses="";


        int NUM_EPOCHS = 300;
        int BATCH_SIZE = 5;
        int NUM_TRAININGS = trainX.length;
        int NUM_BATCHES = NUM_TRAININGS / BATCH_SIZE; // Because the training have got 1 element to train

        int NUM_FEATURES = trainX[0].length;/* Número de características en tus datos */;
        int NUM_CLASSES = 2; // Esto depende del número de clases en tus datos.

        //Creación de lotes de tamaño num_batches para después pasarselo al reentramiento


        float[][] trainLabels = new float[0][0];

        List<float[][]> trainFeatureBatches = new ArrayList<>(NUM_BATCHES);
        List<float[][]> trainLabelBatches = new ArrayList<>(NUM_BATCHES);

        // Prepare training batches.
        for (int i = 0; i < NUM_BATCHES; ++i) {
            float[][] trainFeatures = new float[BATCH_SIZE][NUM_FEATURES];
            trainLabels = new float[BATCH_SIZE][NUM_CLASSES];

            int startIdx = i * BATCH_SIZE;
            int endIdx = Math.min((i + 1) * BATCH_SIZE, NUM_TRAININGS);

            for (int j = startIdx; j < endIdx; ++j) {
                System.arraycopy(trainX[j], 0, trainFeatures[j - startIdx], 0, NUM_FEATURES);
                int classIndex = (int) trainY[j];
                trainLabels[j - startIdx][0] = classIndex;  // 1-D array with class indices
            }

            trainFeatureBatches.add(trainFeatures);
            trainLabelBatches.add(trainLabels);
        }




        // Run training for a few steps.
        float[] losses = new float[NUM_EPOCHS];
        for (int epoch = 0; epoch < NUM_EPOCHS; ++epoch) {
            for (int batchIdx = 0; batchIdx < NUM_BATCHES; ++batchIdx) {

                Map<String, Object> inputs = new HashMap<>();
                //Obtener los lotes para pasarselos al reentrenamiento
                inputs.put("x", trainFeatureBatches.get(batchIdx)); //train_X

                float[] trainLabelsArray1D = new float[BATCH_SIZE];
                for (int i = 0; i < BATCH_SIZE; i++) {
                    trainLabelsArray1D[i] = trainLabels[i][0];
                }

                inputs.put("y", trainLabelsArray1D); // train_y


                Map<String, Object> outputs = new HashMap<>();
                FloatBuffer loss = FloatBuffer.allocate(1);
                outputs.put("loss", loss);
                // IF YOU WANT TO RUN WITH SIGNATURE, THE MODEL DONT MUST BE SEQUENTIAL


                interpreter.runSignature(inputs, outputs,"train");
                // Record the last loss.
                if (batchIdx == NUM_BATCHES - 1)
                    losses[epoch] = loss.get(0);
            }

            // Print the loss output for every 10 epochs.
            if ((epoch + 1) % 10 == 0) {
                System.out.println("Finished " + (epoch + 1) + " epochs, current loss: " + losses[epoch]);
                logLosses= logLosses+("Finished " + (epoch + 1) + " epochs, current loss: " + losses[epoch])+'\n';
            }
        }


        System.out.println("*** FINISHED ***");




    }



}