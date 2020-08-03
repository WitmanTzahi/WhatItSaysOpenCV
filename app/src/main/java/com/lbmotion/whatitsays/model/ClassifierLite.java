package com.lbmotion.whatitsays.model;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import android.os.Trace;

import com.lbmotion.whatitsays.env.Logger;

import org.opencv.core.Mat;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

//import org.tensorflow.lite.Interpreter;
//import org.tensorflow.lite.gpu.GpuDelegate;

public class ClassifierLite {

    /** Options for configuring the Interpreter. */
//    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
//    protected Interpreter tflite;

    /** Optional GPU delegate for accleration. */
//    private GpuDelegate gpuDelegate = null;

    private static final Logger LOGGER = new Logger();

    private String[] labels = {"0","1","2","3","4","5","6","7","8","9"};
    private float[][] labelProbArray = new float[1][labels.length];
    private ByteBuffer imgData = null;
    /** Dimensions of inputs. */
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 1;

    private Float confidence;

    public ClassifierLite() {
        imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * 30 * 30 * DIM_PIXEL_SIZE * getNumBytesPerChannel());
        imgData.order(ByteOrder.nativeOrder());
    }

    public void create(Activity activity, Device device, int numThreads) throws IOException {
        tfliteModel = loadModelFile(activity);
        switch (device) {
            case NNAPI:
//                tfliteOptions.setUseNNAPI(true);
                break;
            case GPU:
//                gpuDelegate = new GpuDelegate();
//                tfliteOptions.addDelegate(gpuDelegate);
                break;
            case CPU:
                break;
        }
//        tfliteOptions.setNumThreads(numThreads);
//        tflite = new Interpreter(tfliteModel, tfliteOptions);
        LOGGER.d("Created a Tensorflow Lite Image ClassifierLite.");
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("license_plate.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /** The runtime device type used for executing classification. */
    public enum Device {
        CPU,
        NNAPI,
        GPU
    }

    private int getNumBytesPerChannel() {
        return 4; // Float.SIZE / Byte.SIZE;
    }

    /** Runs inference and returns the classification results. */
    public List<Classification> recognize(final Mat mat) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");
        List<Classification> digits = new ArrayList<>();
        // Run the inference call.
        Trace.beginSection("runInference");
        long startTime = SystemClock.uptimeMillis();
        imgData.rewind();
        float[][][][] temp = new float[1][30][30][1];
        for (int x = 0; x < 30; ++x) {
            for (int y = 0; y < 30; ++y) {
                imgData.putFloat((float)mat.get(x,y)[0]);
                temp[0][x][y][0] = (float)mat.get(x,y)[0];
            }
        }
//        tflite.run(imgData, labelProbArray);
//        tflite.run(temp, labelProbArray);
        long endTime = SystemClock.uptimeMillis();
        Trace.endSection();
        LOGGER.v("Timecost to run model inference: " + (endTime - startTime));

        Trace.endSection();
        return digits;
    }

}

