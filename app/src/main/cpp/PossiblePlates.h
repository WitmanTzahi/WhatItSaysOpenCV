//
// Created by Tzahi Witman on 23/05/2019.
//

#ifndef HANITA_POSSIBLEPLATES_H
#define HANITA_POSSIBLEPLATES_H

#include <opencv2/imgproc.hpp>

using namespace cv;
using namespace std;

class PossiblePlates {
private:
    vector<Mat> output;
public:
    vector<Mat> get(Mat &image, int rotationFactor);
};


#endif //HANITA_POSSIBLEPLATES_H
