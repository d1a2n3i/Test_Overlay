import csv
from os import listdir
import os
from array import *

#This file is used to rename the fragments based on the activity they belong to

path = "/Users/dani.a/Desktop/Android_End_Term_Proj/ReformatDataFrame/reformatDataFrames/data_frames_new"#make this a relative path
files = listdir(path)
line_count = []
file1 = open("myfile.txt", "w")
index = 0
for i in range(len(files)):

    with open(path + "/" + files[i], 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        data = list(csv.reader(csv_file))
        print(data[1][7])

        print(files[i])

        line_count.append(data[1][7] + files[i])




            # to change file access modes
print(len(line_count))
# file1.write(''.join(line_count))
# file1.close()
for i in range(len(files)):
     print(files[i])
     os.rename(path + "/" + files[i],line_count[i])

