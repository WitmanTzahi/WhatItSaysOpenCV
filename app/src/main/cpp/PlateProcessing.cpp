//
// Created by Tzahi Witman on 24/05/2019.
//

#include "PlateProcessing.h"
#include <opencv2/imgproc/types_c.h>

#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#define TAG "PlateProcessing"

static Mat resultResized;

void PlateProcessing::setMemory() {
    resultResized.create(66, 288, CV_8UC3);
}

Mat PlateProcessing::histeq(Mat &in) {
    Mat out(in.size(), in.type());
    if (in.channels() == 3) {
        Mat hsv;
        vector<Mat> hsvSplit;
        cvtColor(in, hsv, CV_BGR2HSV);
        split(hsv, hsvSplit);
        equalizeHist(hsvSplit[2], hsvSplit[2]);
        merge(hsvSplit, hsv);
        cvtColor(hsv, out, CV_HSV2BGR);
    }
    else if (in.channels() == 1) {
        equalizeHist(in, out);
    }
    return out;
}

int PlateProcessing::verifySizes(Mat &r, Rect mr) {
    //Char sizes 45x77
    float aspect = 45.0f / 77.0f;
    float charAspect = (float)r.cols / (float)r.rows;
    float error = 0.6;
    float minHeight = 30;
    float maxHeight = 60;
    //We have a different aspect ratio for number 1, and it can be ~0.2
    float minAspect = 0.2;
    float maxAspect = aspect + aspect * error;
    //area of pixels
    float area = countNonZero(r);
    //bb area
    float bbArea = r.cols*r.rows;
    //% of pixel in area
    float percPixels = area / bbArea;
    if (percPixels < 0.8 && charAspect > minAspect && charAspect < maxAspect && r.rows >= minHeight && r.rows <= maxHeight) {
        return 0;
    }
    else {
        if(r.rows >= minHeight && mr.x < 8)
            return 1;
        else
            return 2;
    }
}

Mat PlateProcessing::preprocessChar(Mat &in) {
    //Remap image
    int h = in.rows;
    int w = in.cols;
    Mat transformMat = Mat::eye(2, 3, CV_32F);
    int m = max(w, h);
    transformMat.at<float>(0, 2) = m / 2 - w / 2;
    transformMat.at<float>(1, 2) = m / 2 - h / 2;

    Mat warpImage(m, m, in.type());
    warpAffine(in, warpImage, transformMat, warpImage.size(), INTER_LINEAR, BORDER_CONSTANT, Scalar(0));

    Mat out;
    resize(warpImage, out, Size(30, 30));

    return out;
}

bool compareRect(Rect r1, Rect r2) {
    return (r1.x < r2.x);
}

void PlateProcessing::prepairPlate(Mat &image) {
    resize(image, resultResized, resultResized.size(), 0, 0, INTER_CUBIC);
//Equalize croped image
    cvtColor(histeq(resultResized), grayResult, CV_BGR2GRAY);
    blur(grayResult, grayResult, Size(3, 3));
    grayResult = histeq(grayResult);
}

PlateProcessing::PlateProcessing(Mat &image) {
    prepairPlate(image);
}

PlateProcessing::PlateProcessing(Mat* image) {
    grayResult = *image;
}

PlateProcessing::PlateProcessing() {
}

Mat PlateProcessing::get() {
    return grayResult;
}
int* PlateProcessing::process(bool checkCounters, bool clearLine) {
    string str = "1";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
    int best_counter = 0;
    int best_number_of_rects = 0;
    int best_img_threshold = 10;
    int limit = 150;
    int* characterSegmentationResults = characterSegmentation(best_img_threshold, checkCounters, clearLine, false);
    str = "2";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
    best_number_of_rects = characterSegmentationResults[0];
    best_counter = characterSegmentationResults[1];
    int img_threshold = best_img_threshold;
    str = "3";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
    while (img_threshold <= limit) {
        img_threshold += 10;
        str = "4";
        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
        characterSegmentationResults = characterSegmentation(img_threshold, checkCounters, clearLine, false);
        int number_of_rects = characterSegmentationResults[0];
        int counter = characterSegmentationResults[1];
        str = "5";
        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
        string str = "counter:"+to_string(counter)+" best_counter:"+to_string(counter)+" best_counter:"+to_string(counter)+" number_of_rects:"+to_string(number_of_rects)+" best_number_of_rects:"+to_string(best_number_of_rects);
        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
        if (counter > best_counter || (counter == best_counter && number_of_rects > best_number_of_rects)) {
            best_img_threshold = img_threshold;
            best_counter = counter;
            best_number_of_rects = number_of_rects;
        }
        str = "6";
        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
        if (counter + 2 < best_counter) {
            break;
        }
        str = "6.1";
        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
    }
    results[0] = best_number_of_rects;
    results[1] = best_counter;
    results[2] = best_img_threshold;
    results[3] = img_threshold;
    str = "7";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::process:%s", str.c_str());
    return results;
}

int* PlateProcessing::characterSegmentation(int img_threshold_value,bool checkCounters, bool clearLine, bool doOutput) {
    string str = "1";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    int fixes = 0;
//Threshold input image
    threshold(grayResult, img_threshold_result, img_threshold_value, 255, CV_THRESH_BINARY_INV);
    str = "2";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    if (clearLine) {
        line(img_threshold_result, Point(0, 0), Point(img_threshold_result.cols, 0), Scalar(0), 3);
    }
    str = "3";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    Mat img_contours = img_threshold_result.clone();
    //Find contours of possibles characters
    vector< vector< Point> > contours;
    vector<Vec4i> hierarchy;
    str = "4";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    findContours(img_contours.clone(), contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0));
    str = "5";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    try {
        for (size_t c = 0; c < contours.size(); ++c) {
            str = "6";
            __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s",str.c_str());
            Rect mr = boundingRect(contours.at(c));
            str = "6.1";
            __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s",str.c_str());
            str = "mr X:" + to_string(mr.x) + " Y:" + to_string(mr.y) + " W:" + to_string(mr.width) + " H:" + to_string(mr.height);
            __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s",str.c_str());
            str = "img_contours C:" + to_string(img_contours.cols) + " R:" + to_string(img_contours.rows);
            __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s",str.c_str());
            if (mr.area() < 220) {
                if(mr.x + mr.width >= img_contours.cols) {
                    mr.width = img_contours.cols - mr.x - 1;
                }
                if(mr.y + mr.height >= img_contours.rows) {
                    mr.height = img_contours.rows - mr.y - 1;
                }
                img_contours(mr).setTo(0);
            }
            str = "7";
            __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s",str.c_str());
        }
    }
    catch (...) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s",str.c_str());
    }
    str = "8";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    contours.clear();
    hierarchy.clear();
    findContours(img_contours, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, Point(0, 0));
    str = "9";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
// Draw blue contours on a white image
//  cvtColor(img_threshold, result, CV_GRAY2RGB);
//  drawContours(result, contours,
//               -1, // draw all contours
//               Scalar(255, 0, 0), // in blue
//               1); // with a thickness of 1
    //Start to iterate to each contour founded
    if (contours.size() < 7) {
        results[0] = 0;
        results[1] = 0;
        results[2] = 0;
        results[3] = 0;
        return results;
    }
    str = "10";
    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    vector< Rect > rects;
    try {
        for (size_t c = 0; c < contours.size() && checkCounters; ++c) {
            str = "11";
            __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
            bool overlapping = false;
            Rect mr = boundingRect(contours.at(c));
            if (mr.width > 90) {
                contours.erase(contours.begin() + c);
                c--;
            } else {
                for (size_t r = 0; r < rects.size(); ++r) {
                    if (mr.width < 60 && rects.at(r).width < 60 && (mr & rects.at(r)).area() > 0) {
                        contours.at(c).insert(contours.at(c).end(), contours.at(r).begin(),
                                              contours.at(r).end());
                        contours.erase(contours.begin() + c);
                        c--;
                        overlapping = true;
                        break;
                    }
                }
                if (!overlapping) {
                    rects.push_back(mr);
                }
            }
//^^        str = "12";
//^^        __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
        }
    }
    catch (...) {
//^^    str = "12.1";
//^^    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    }
//^^str = "13";
//^^__android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    if (contours.size() < 7) { //^^^^5
        results[0] = 0;
        results[1] = 0;
        results[2] = 0;
        results[3] = 0;
        return results;
    }
//^^str = "14";
//^^__android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
//  vector<vector<Point> >::iterator itc = contours.begin();
//  while (itc != contours.end()) {
//  ++itc;
//  Rect mr = boundingRect(Mat(*itc));
//Remove patch that are no inside limits of aspect ratio and area.
    vector< Rect > good_rects;
    int counter = 0;
    for (size_t i = 0; i < contours.size(); ++i) {
//^^    str = "15";
//^^    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
        //Create bounding rect of object
        Rect mr = boundingRect(contours.at(i));
        if(mr.x + mr.width >= img_threshold_result.cols) {
            mr.width = img_threshold_result.cols - mr.x - 1;
        }
        if(mr.y + mr.height >= img_threshold_result.rows) {
            mr.height = img_threshold_result.rows - mr.y - 1;
        }
//      rectangle(result, mr, Scalar(0, 255, 0));
        //Crop image
        Mat auxRoi(img_threshold_result, mr);
        int verifyResult = verifySizes(auxRoi, mr);
        if (verifyResult == 0) {
//          rectangle(result, mr, Scalar(0, 125, 255));
            counter++;
            good_rects.push_back(mr);
        }
        else if (verifyResult == 1) {
            counter++;
        }
//^^    str = "16";
//^^    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    }
//^^str = "17";
//^^__android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    int halfRotation = 0;
    bool reduceCounter = false;
    if (good_rects.size() > 6) {
        sort(good_rects.begin(), good_rects.end(), compareRect);
        if (abs(good_rects[0].y - good_rects[good_rects.size() - 1].y) > 5) {
            halfRotation = 1;
        }
        else if (abs((good_rects[0].y + good_rects[0].height) - (good_rects[good_rects.size() - 1].y + good_rects[good_rects.size() - 1].height)) > 5) {
            halfRotation = 1;
        }
        int diff = 10;
        while (diff >= 5 && good_rects.size() > 6) {
            int lowerPoints = 0;
            int minLowerPoints = 999;
            for (size_t i = 0; i < good_rects.size(); ++i) {
                lowerPoints += good_rects.at(i).y + good_rects.at(i).height;
                minLowerPoints = min(minLowerPoints, good_rects.at(i).y + good_rects.at(i).height);
            }
            lowerPoints = (lowerPoints-minLowerPoints)/(good_rects.size()-1.0f)+0.5;
            diff = abs(good_rects.at(0).y + good_rects.at(0).height - lowerPoints);
            int diffInx = 0;
            for (size_t i = 1; i < good_rects.size(); ++i) {
                int d = abs(good_rects.at(i).y + good_rects.at(i).height - lowerPoints);
                if (d > diff) {
                    diff = d;
                    diffInx = i;
                }
            }
            if (diff >= 5) {
//              rectangle(result, good_rects.at(diffInx), Scalar(0, 255, 255));
                good_rects.erase(good_rects.begin() + diffInx);
                counter--;
            }
        }
//^^    str = "18";
//^^    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
        diff = 10;
        while (diff >= 5 && good_rects.size() > 6) {
            int lowerPoints = 0;
            int minLowerPoints = 999;
            for (size_t i = 0; i < good_rects.size(); ++i) {
                lowerPoints += good_rects.at(i).y;
                minLowerPoints = min(minLowerPoints, good_rects.at(i).y);
            }
            lowerPoints = (lowerPoints- minLowerPoints)/(good_rects.size()-1.0f) + 0.5;
            diff = abs(good_rects.at(0).y - lowerPoints);
            int diffInx = 0;
            for (size_t i = 1; i < good_rects.size(); ++i) {
                int d = abs(good_rects.at(i).y - lowerPoints);
                if (d > diff) {
                    diff = d;
                    diffInx = i;
                }
            }
            if (diff >= 5) {
//              rectangle(result, good_rects.at(diffInx), Scalar(0, 255, 255));
                good_rects.erase(good_rects.begin() + diffInx);
                counter--;
            }
        }
        if (counter >= 7) {
            diff = good_rects.at(0).x;
            for (size_t i = 1; i < good_rects.size(); ++i) {
                if (good_rects.at(i).x - good_rects.at(i - 1).x > diff) {
                    diff = good_rects.at(i).x - good_rects.at(i - 1).x;
                }
            }
            if (diff < 8) {
                counter = 8;
            }
            else {
                if (good_rects.at(0).x < 20) {
                    counter = 8;
                    reduceCounter = true;
                }
            }
        }
//^^    str = "19";
//^^    __android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
        if (good_rects.size() >= 7) {
            if (good_rects.at(0).x < 8) {
                int aveY = 0;
                int aveYH = 0;
                int aveW = 0;
                for (size_t i = 1; i < good_rects.size(); ++i) {
                    aveYH += (good_rects.at(i).y + good_rects.at(i).height);
                    aveY += good_rects.at(i).y;
                    aveW += good_rects.at(i).width;
                }
                if (good_rects.at(0).width >= aveW / (good_rects.size() - 1) + 5) {
//                  rectangle(result, good_rects.at(0), Scalar(0, 255, 255));
                    good_rects.erase(good_rects.begin());
                    if (reduceCounter) {
                        counter--;
                    }
                }
                else if (good_rects.at(0).y + good_rects.at(0).height > aveYH / (good_rects.size() - 1) + 4) {
//                  rectangle(result, good_rects.at(0), Scalar(0, 255, 255));
                    good_rects.erase(good_rects.begin());
                    if (reduceCounter) {
                        counter--;
                    }
                }
                else if (good_rects.at(0).y + 4 < aveY / (good_rects.size() - 1)) {
//                  rectangle(result, good_rects.at(0), Scalar(0, 255, 255));
                    good_rects.erase(good_rects.begin());
                    if (reduceCounter) {
                        counter--;
                    }
                }
                if (good_rects.at(1).x > 90) {
//                  rectangle(result, good_rects.at(0), Scalar(0, 255, 255));
                    good_rects.erase(good_rects.begin());
                    if (reduceCounter) {
                        counter--;
                    }
                }
            }
        }
    }
//^^str = "20";
//^^__android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    if (good_rects.size() > 5 && doOutput) {
        int maxY = 0;
        int minY = 0;
        for (size_t i = 0; i < good_rects.size(); ++i) {
            maxY += good_rects.at(i).y + good_rects.at(i).height;
            minY += good_rects.at(i).y;
        }
        maxY /= good_rects.size();
        minY /= good_rects.size();
        for (size_t i = 0; i < good_rects.size(); ++i) {
            if (abs(good_rects.at(i).y - minY) > 3) {
                good_rects.at(i).y = minY;
                fixes++;
            }
            int diff = abs(good_rects.at(i).y + good_rects.at(i).height - maxY);
            if (diff > 3) {
                good_rects.at(i).height = min(good_rects.at(i).height + diff, grayResult.rows - good_rects.at(i).y);
                fixes++;
            }
//          rectangle(result, good_rects.at(i), Scalar(255, 0, 0));
            if (i < (sizeof(digits)/sizeof(digits[0]))) {
                Mat auxRoi(img_threshold_result, good_rects.at(i));
                digits[i]  = preprocessChar(auxRoi);
            }
        }
    }
//^^str = "21";
//^^__android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    results[0] = good_rects.size();
    results[1] = counter;
    results[2] = halfRotation;
    results[3] = fixes;
//^^str = "22";
//^^__android_log_print(ANDROID_LOG_ERROR, TAG, "PlateProcessing::characterSegmentation:%s", str.c_str());
    return results;
}