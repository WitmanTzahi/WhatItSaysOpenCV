#include <jni.h>
#include <string>
#include <iostream>

#include <opencv2/imgproc/types_c.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/dnn.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#define TAG "jni_part.c"

#include "PossiblePlates.h"
#include "PlateProcessing.h"

using namespace cv;
using namespace std;

int results5[5];

int* process(Mat &srcImg, Mat &grayResultBest, int rotationFactor) {
    PossiblePlates possiblePlates;
    vector<Mat> candidatePlates = possiblePlates.get(srcImg, rotationFactor);
    int result_best_img_threshold = 0;
    bool result_best_checkCounters = false;
    bool result_best_clearLine = false;
    int result_best_counter = 0;
    int result_best_number_of_rects = 0;
    int* results;
    bool callRotationHalf = false;
    for (vector<Mat>::iterator it = candidatePlates.begin();it != candidatePlates.end(); ++it) {
        PlateProcessing plateProcessing = PlateProcessing(*it);
        results = plateProcessing.process(true, false);
        int best_number_of_rects = results[0];
        int best_counter = results[1];
        int best_img_threshold = results[2];
        bool best_checkCounters = true;
        bool best_clearLine = false;
        results = plateProcessing.process(true, true);
        if (results[0] > best_number_of_rects || (results[0] == best_number_of_rects && results[1] > best_counter)) {
            best_number_of_rects = results[0];
            best_counter = results[1];
            best_img_threshold = results[2];
            best_checkCounters = true;
            best_clearLine = true;
        }
        results = plateProcessing.process(false, false);
        if (results[0] > best_number_of_rects || (results[0] == best_number_of_rects && results[1] > best_counter)) {
            best_number_of_rects = results[0];
            best_counter = results[1];
            best_img_threshold = results[2];
            best_checkCounters = false;
            best_clearLine = false;
        }
        if (best_number_of_rects > result_best_number_of_rects || (best_number_of_rects == result_best_number_of_rects && result_best_counter > best_counter)) {
            result_best_number_of_rects = best_number_of_rects;
            result_best_counter = best_counter;
            result_best_img_threshold = best_img_threshold;
            result_best_checkCounters = best_checkCounters;
            result_best_clearLine = best_clearLine;
            grayResultBest = plateProcessing.get();
        }
    }
    if (result_best_img_threshold > 0) {
        PlateProcessing plateProcessing = PlateProcessing(&grayResultBest);
        results = plateProcessing.characterSegmentation(result_best_img_threshold, result_best_checkCounters, result_best_clearLine, false);
        int fixes = results[3];
        int save_result_best_img_threshold = result_best_img_threshold;
        for (int i = save_result_best_img_threshold - 9; i <= save_result_best_img_threshold + 9; i += 3) {
            if (i != result_best_img_threshold) {
                results = plateProcessing.characterSegmentation(i, result_best_checkCounters, result_best_clearLine, false);
            }
            if (results[0] > result_best_number_of_rects || (results[0] == result_best_number_of_rects && (results[1] > result_best_counter || results[3] < fixes || (i > result_best_img_threshold && i <= result_best_img_threshold+6)))) {
                result_best_number_of_rects = results[0];
                result_best_counter = results[1];
                result_best_img_threshold = i;
                fixes = results[3];
            }
            callRotationHalf = results[2] == 1;
        }
    }
    if (result_best_number_of_rects < 8 && callRotationHalf)
        rotationFactor = 2;
    results5[0] = result_best_img_threshold;
    results5[1] = result_best_checkCounters;
    results5[2] = result_best_clearLine;
    results5[3] = rotationFactor;
    results5[4] = result_best_number_of_rects;
    return results5;
}

void cleanDigitImage(Mat &digit, Mat* mat) {
    if (digit.cols > 0 && digit.rows > 0) {
        vector<vector<Point> > contours;
        vector<Vec4i> hierarchy;
        findContours(digit.clone(), contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0));
        for (size_t c = 0; c < contours.size(); ++c) {
            Rect mr = boundingRect(contours.at(c));
            if (mr.area() < 20) {
                digit(mr).setTo(0);
            }
        }
    }
    *mat = digit;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_lbmotion_whatitsays_MainActivity_InitPlates(JNIEnv *env, jobject) {
    PlateProcessing plateProcessing;
    plateProcessing.setMemory();
    return 0;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_lbmotion_whatitsays_MainActivity_GetPlates(JNIEnv *env, jobject, jlong addrRgba, jlong plate, jlong digit1, jlong digit2, jlong digit3, jlong digit4, jlong digit5, jlong digit6, jlong digit7, jlong digit8, jlong digit9, jlong digit10) {
//Java_com_lbmotion_whatitsays_MainActivity_GetPlates(JNIEnv *env, jobject, jlong addrRgba, jlong digit1, jlong digit2, jlong digit3, jlong digit4, jlong digit5, jlong digit6, jlong digit7, jlong digit8, jlong digit9, jlong digit10) {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "GetPlates");
    Mat& mRgb = *(Mat*)addrRgba;
//
    string str = to_string(mRgb.rows) + "X" + to_string(mRgb.cols);
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Size:%s", str.c_str());
    Mat grayResultBest1, grayResultBest2;
    int* results = process(mRgb, grayResultBest1, 1);
    int result_best_img_threshold1 = results[0];
    bool result_best_checkCounters1 = results[1];
    bool result_best_clearLine1 = results[2];
    int best_number_of_rects1 = results[4];
    int halfRotation = results[3];
    str = to_string(result_best_checkCounters1) + " " + to_string(result_best_clearLine1) + " " + to_string(best_number_of_rects1) + " " + to_string(halfRotation);
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s", str.c_str());
    PlateProcessing plateProcessing;
    bool hasDigits = false;
    if (halfRotation == 2 || best_number_of_rects1 < 8) {
        results = process(mRgb, grayResultBest2, 2);
        if (results[4] > best_number_of_rects1) {
            plateProcessing = PlateProcessing(&grayResultBest2);
            results = plateProcessing.characterSegmentation(results[0],(bool)results[1], (bool)results[2],true);
            hasDigits = true;
        }
        else {
            if (result_best_img_threshold1 > 0) {
                plateProcessing = PlateProcessing(&grayResultBest1);
                results = plateProcessing.characterSegmentation(result_best_img_threshold1,
                                                                result_best_checkCounters1,
                                                                result_best_clearLine1, true);
                hasDigits = true;
            }
        }
    }
    else {
        if(result_best_img_threshold1 > 0) {
            plateProcessing = PlateProcessing(&grayResultBest1);
            results = plateProcessing.characterSegmentation(result_best_img_threshold1,
                                                            result_best_checkCounters1,
                                                            result_best_clearLine1,true);
            hasDigits = true;
        }
    }
    if(hasDigits) {
        if (results[0] > 5) {
            Mat *mat = (Mat *) plate;
            *mat = plateProcessing.img_threshold_result;
            cleanDigitImage(plateProcessing.digits[0], (Mat *) digit1);
            cleanDigitImage(plateProcessing.digits[1], (Mat *) digit2);
            cleanDigitImage(plateProcessing.digits[2], (Mat *) digit3);
            cleanDigitImage(plateProcessing.digits[3], (Mat *) digit4);
            cleanDigitImage(plateProcessing.digits[4], (Mat *) digit5);
            cleanDigitImage(plateProcessing.digits[5], (Mat *) digit6);
            cleanDigitImage(plateProcessing.digits[6], (Mat *) digit7);
            cleanDigitImage(plateProcessing.digits[7], (Mat *) digit8);
            cleanDigitImage(plateProcessing.digits[8], (Mat *) digit9);
            cleanDigitImage(plateProcessing.digits[9], (Mat *) digit10);
            return results[0];
        }
    }
    return -1;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_lbmotion_whatitsays_MainActivity_SetDigit(JNIEnv *env, jobject, jlong digit) {
    Mat *mat = (Mat *) digit;
    Mat matOut;
    mat->convertTo(*mat, CV_32FC3, 1 / 255.0);
    return 0;
}
