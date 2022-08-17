# SegmentationTool

### Introduction
This App was made for the research over sky-cloud segmentation.


### Tech Stacks
- Control Flow
  * Kotlin
  * Java

- UI
  * XML
  * Android Navigation Components


### Features Implemented
- Load any image using url.
- Change width and height as required.
- Redo, undo, clear canvas, change the color and size of brush.
- Add, remove background color as required.
- Save or share the image through the app.



### Get the app
- [Download](https://github.com/aryanO-o/segmentationTool/raw/main/resources/segmentationTool.apk)

### How to use app
On opening the app we will see the home fragment which contains the instruction on how we can use the app for ground truth labeling. This will be helpful for the new users of the app.<br />
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/home.jpg" width = "250" height = "500" align="center">
</p>

When we click on the hamburger icon on the top left corner or swipe right from the left edge we will see the navigation drawer.
From this navigation drawer we can navigate from one fragment to other easily by swiping right and selecting the fragment to go.<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/navigationDrawer.jpg" width = "250" height = "500">
</p>

We can go to choose image fragment by click on the ImageUrl option in the navigation drawer where we can paste the image url we wan to work on.<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/chooseImage.jpg" width = "250" height = "500">
</p>

We can paste the URL of our image and click on the preview button, image will load in the image view and the height and width of the image will be loaded and displayed. Also if we want to change the height and width of the image while saving the image then we can change the width and height to required width and height.<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/gettingImageAndItsData.jpg" width = "250" height = "500">
</p>

After getting the image preview we can now click on the start button by which we will be directed to the segmentation fragment where the image will be loaded completely stretched in the background of the drawing area.
<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/loadedImage.jpg" width = "250" height = "500">
</p>

We can now use brush to color the cloud part of the image, we can use redo, undo buttons or change the size and color of the brush or we can clear the drawing area at once by clicking the delete button.<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/workingOnImage.jpg" width = "250" height = "500">
</p>


After coloring the cloud we can click on the second btn from the left in the bottom drawer to remove the background and getting actual labeled image.<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/afterRemovingBackground.jpg" width = "250" height = "500">
</p>

We can now share this image using the share button, any app that can share images will be displayed in the share image dialog box.
Avoid using whatsapp to share images as if the image size is very big than whatsapp will reduce the quality of the image for sharing purpose.
<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/shareImage.jpg" width = "250" height = "500">
</p>

We can click on the button on bottom right to save the image on the device, also when sharing the image one copy of the image is saved in the device. \\The image will be saved in the original size of the image which was displyed in the preview screen. \\Below is the saved image.
<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/savedImage.jpg">
</p>


We can see the details of the saved image that it has the size of the actual image.
<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/savedImageDetails.png">
</p>

Comparing Actual image vs ground truth image.
<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/actualImage.jpeg">
</p>
<br/>
<p align="center">
<img src = "https://github.com/aryanO-o/segmentationTool/blob/main/resources/Figures/savedImage.jpg">
</p>



