//
// Created by Tzahi Witman on 24/05/2019.
//

#ifndef HANITA_PLATEPROCESSING_H
#define HANITA_PLATEPROCESSING_H

#include <opencv2/imgproc.hpp>

using namespace cv;
using namespace std;

class PlateProcessing {
private:
    int results[4];
    Mat grayResult;
    void prepairPlate(Mat &image);
    int verifySizes(Mat &r, Rect mr);
    Mat histeq(Mat &in);
    Mat preprocessChar(Mat &in);
public:
    Mat digits[10];
    Mat img_threshold_result;
    void setMemory();
    PlateProcessing();
    PlateProcessing(Mat &image);
    PlateProcessing(Mat *image);
    int* process(bool checkCounters, bool clearLine);
    int* characterSegmentation(int img_threshold_value,bool checkCounters, bool clearLine, bool doOutput);
    Mat get();
};


#endif //HANITA_PLATEPROCESSING_H
