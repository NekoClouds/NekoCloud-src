#!/bin/sh

g++ -std=c++11 -shared -fPIC -O3 -Wall -Werror src\\c\\NativeCompressImpl.cpp -o src\\main\\resources\\native-compress.so -lz
