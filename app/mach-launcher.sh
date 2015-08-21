#!/bin/sh

WORTE="Tick"

WORTE=$1
version=$2
code=$3

VERSION="$version\n$code"

size="512x512"
Pointsize=176
pointsize=80

convert -size $size xc:black -fill white -draw 'translate 256,256 circle 0,0 240,0'  -gaussian 1x1 +matte a-01.png
#convert a-01.png -fill yellow -draw 'color 0,0 reset' a-01.png +matte -compose CopyOpacity -composite -font DejaVu-Sans-Bold -pointsize $pointsize -fill blue -gravity center -annotate 0x0 "$WORTE" a-02.png 
convert a-01.png -fill yellow -draw 'color 0,0 reset' a-01.png +matte -compose CopyOpacity -composite a-04.png 

convert a-01.png -blur 0x4 -blur 0x4 -blur 0x4 -blur 0x4 +matte x-03.png
convert x-03.png -shade 90x0 -normalize x-04.png
convert x-03.png -shade 90x180 -normalize x-05.png
convert \( x-04.png \( +clone -fx 'rand()' -threshold -1 \) +swap +matte -compose CopyOpacity -composite \) \( x-05.png \( +clone -threshold 100% \)  +swap +matte -compose CopyOpacity -composite \) -compose Over -composite x-06.png 
convert a-04.png x-06.png -compose ATop -composite x-07.png
#onvert x-07.png \( +clone -fx 'rand()' +matte -shade 120x45 -fill gray -fill beige -tint 120 \)  -insert 0 -flatten x-08.png

convert -font DejaVu-Sans-Bold       -fill blue  -pointsize $Pointsize -stroke black -strokewidth 6 -gravity center -annotate +0-32 "$WORTE" x-07.png x-08.png
convert -font DejaVu-Sans-ExtraLight -fill black -pointsize $pointsize -gravity center -annotate +0+128 "$VERSION" x-08.png x-09.png

mkdir -p src/main/res/mipmap-ldpi     #  36x36   0.75
mkdir -p src/main/res/mipmap-mdpi     #  48x48   1
mkdir -p src/main/res/mipmap-tvdpi    #  64x64   1.33
mkdir -p src/main/res/mipmap-hdpi     #  72x72   1.5
mkdir -p src/main/res/mipmap-xhdpi    #  96x96   2
mkdir -p src/main/res/mipmap-xxhdpi   # 144x144  3
mkdir -p src/main/res/mipmap-xxxhdpi  # 192x192  4
mkdir -p src/main/res/mipmap-web      # 512x512  10.67

convert -resize  36x36  x-09.png src/main/res/mipmap-ldpi/ic_launcher.png
convert -resize  48x48  x-09.png src/main/res/mipmap-mdpi/ic_launcher.png
convert -resize  64x64  x-09.png src/main/res/mipmap-tvdpi/ic_launcher.png
convert -resize  72x72  x-09.png src/main/res/mipmap-hdpi/ic_launcher.png
convert -resize  96x96  x-09.png src/main/res/mipmap-xhdpi/ic_launcher.png
convert -resize 144x144 x-09.png src/main/res/mipmap-xxhdpi/ic_launcher.png
convert -resize 192x192 x-09.png src/main/res/mipmap-xxxhdpi/ic_launcher.png
convert -resize 512x512 x-09.png src/main/res/mipmap-web/ic_launcher.png

exit 0

# Mondsichel
convert a-03.png -alpha on \( +clone -alpha Transparent -fill skyblue -draw 'circle 35,35 35,5' \( +clone -alpha Transparent -fill black -draw 'circle 28,30 35,5' \) -compose Dst_Out -composite \) -compose Over -composite show:

