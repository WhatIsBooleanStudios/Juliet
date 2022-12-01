from PIL import Image
import numpy as np
import sys

img = Image.open(sys.argv[1])
width = img.size[0]
height = img.size[1]

for i in range(0,width):
    for j in range(0,height):
         data = img.getpixel((i,j))
         if data[0] == 0 and data[1] == 0 and data[2] == 0 and data[3] != 0:
             img.putpixel((i,j),(255, 255, 255))

img.save(sys.argv[1])
