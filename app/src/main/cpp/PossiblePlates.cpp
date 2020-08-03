//
// Created by Tzahi Witman on 23/05/2019.
//

#include <opencv2/imgproc/types_c.h>
#include "PossiblePlates.h"

Point2f rotatePointByPoint(Point2f &point, Point2f &center,float angle) {
/*
	These are the steps needed to rotate a point around another point by an angle alpha :
	Translate the point by the negative of the pivot point
		Rotate the point using the standard equation for 2 - d(or 3 - d) rotation
		Translate back
		The standard equation for rotation is :
	x' = xcos(alpha) - ysin(alpha)
	y' = xsin(alpha) + ycos(alpha)
	Let's take the example of Point(15,5) around Point(2,2) by 45 degrees.
	Firstly, translate:
	v = (15, 5) - (2, 2) = (13, 3)
	Now rotate by 45° :
		v = (13 * cos 45° - 3 * sin 45°, 13 * sin 45° + 3 * cos 45°) = (7.07.., 11.31..)
		And finally, translate back :
		v = v + (2, 2) = (9.07.., 13.31..)
		Note : Angles must be specified in radians, so multiply the number of degrees by Pi / 180
*/
    Point2f v = Point2f(point.x - center.x, point.y - center.y);
    Point2f vv = Point2f(v.x * cos(angle) - v.y * sin(angle), v.x * sin(angle) + v.y * cos(angle));
    Point2f vvv = Point2f(vv.x + center.x, vv.y + center.y);
    return vvv;
}

vector<Mat> PossiblePlates::get(Mat &inImage, int rotationFactor) {
    output.clear();
    Size size = inImage.size();
    int half_width = size.width / 2.2;
    int start_size = size.height / 10;
    int fifth_width = size.width * 0.2;
    int seventy_width = size.width * 0.75;
    int fifth_height = size.height * 0.15;
    int seventy_height = size.height * 0.6;
    Mat gray, binary, image, img_edges, sub_binary, iamge_rotate, lambda;
    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    cvtColor(inImage, image, COLOR_BGR2RGB);
    cvtColor(image, gray, COLOR_BGR2HSV);
    vector<Mat> hsv_planes;
    split(gray, hsv_planes);
    gray = hsv_planes[2];
    adaptiveThreshold(gray, binary, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 17, 1);
    Mat element = getStructuringElement(MORPH_RECT, Size(3, 3));
    erode(binary, binary, element);
    dilate(binary, binary, element);
    findContours(binary.clone(), contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));
    for (size_t i = 0; i < contours.size(); ++i) {
        Rect r = boundingRect(contours.at(i));
        if (true || r.x < half_width) {
            if (true || r.y >= start_size) {
                if (r.width > 80 && r.height > 20) {
                    if (true || (r.width > fifth_width && r.width < seventy_width)) {
                        if (true || (r.height > fifth_height && r.height < seventy_height)) {
                            if (true || abs(((float)r.width / (float)r.height) - 4.7272) < 4) {
                                output.push_back(image(r));
                                Canny(binary(r), img_edges, 100, 100, 3, true);
                                vector<Vec4i> lines;
                                for (int pass = 0; pass < 2; pass++) {
                                    float angles = 0;
                                    int count = 0;
                                    HoughLinesP(img_edges, lines, 1, CV_PI / 180, CV_THRESH_OTSU | CV_THRESH_BINARY, r.width / 5.f, 2);
                                    for (size_t ii = 0; ii < lines.size(); ++ii) {
                                        float angle = (atan2((float)lines[ii][3] - lines[ii][1], (float)lines[ii][2] - lines[ii][0]));
//                                      if (angle != 0 && abs(angle) < 0.3 * CV_PI) {
                                        if (angle != 0) {
                                            angles += angle;
                                            count++;
                                        }
                                    }
                                    if (count > 0) {
                                        if (angles != 0) {
                                            float median_angle = -(angles / count / rotationFactor);
                                            bitwise_not(binary(r), sub_binary);
                                            Point2f center = Point2f(sub_binary.cols / 2.0, sub_binary.rows / 2.0);
                                            Point2f p[4];
                                            p[0] = Point2f(0, 0);
                                            p[1] = Point2f(0, sub_binary.rows);
                                            p[2] = Point2f(sub_binary.cols,0);
                                            p[3] = Point2f(sub_binary.cols, sub_binary.rows);
                                            Point2f pe = Point2f(sub_binary.cols, sub_binary.rows);
                                            Point2f pointPlatSize = rotatePointByPoint(pe, center, median_angle);
                                            Point2f ps = Point2f(0, 0);
                                            Point2f pointPlatStart = rotatePointByPoint(ps, center, median_angle);
                                            Point2f pt[4];
                                            pt[0] = rotatePointByPoint(p[0], center, median_angle);
                                            pt[1] = rotatePointByPoint(p[1], center, median_angle);
                                            pt[2] = rotatePointByPoint(p[2], center, median_angle);
                                            pt[3] = rotatePointByPoint(p[3], center, median_angle);
                                            lambda = getPerspectiveTransform(p, pt);
                                            Size platSize = Size(max(int(pointPlatSize.x + 0.99), sub_binary.cols), max(int(pointPlatSize.y + 0.99), sub_binary.rows));
//                                          warpPerspective(image(r), iamge_rotate, lambda, platSize, INTER_CUBIC, BORDER_CONSTANT, Scalar(200, 200, 200));
//                                          warpPerspective(image(r), iamge_rotate, lambda, platSize, INTER_LINEAR, BORDER_CONSTANT, Scalar(200, 200, 200));
//                                          warpPerspective(image(r), iamge_rotate, lambda, platSize, INTER_LINEAR, BORDER_CONSTANT, Scalar(200, 200, 200));
                                            warpPerspective(image(r), iamge_rotate, lambda, platSize, INTER_LANCZOS4, BORDER_CONSTANT, Scalar(200, 200, 200));
                                            Rect rr;
                                            if (pointPlatStart.y >= 0) {
                                                int y = (int)((pointPlatStart.y + 0.99));
                                                rr = Rect(0, y, iamge_rotate.cols, iamge_rotate.rows - y * 2);
                                            }
                                            else {
                                                int y = -((int)((pointPlatStart.y - 0.99)));
                                                rr = Rect(0, y, iamge_rotate.cols, iamge_rotate.rows - y * 3);
                                            }
                                            if (rr.height > 0) {
                                                output.push_back(iamge_rotate);
                                                output.push_back(iamge_rotate(rr));
                                            }
                                            else {
                                                output.push_back(image(r));
                                                output.push_back(image(r));
                                            }
                                        }
                                        else {
                                            output.push_back(image(r));
                                            output.push_back(image(r));
                                        }
                                        break;
                                    }
                                    else {
                                        if (pass == 1) {
                                            output.push_back(image(r));
                                            output.push_back(image(r));
                                            break;
                                        }
                                        img_edges = binary(r);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return output;
}
