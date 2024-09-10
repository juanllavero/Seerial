# <img src="https://github.com/juanllavero/Seerial/assets/33575454/fe9ef4f9-4fe5-4df3-a358-eefc389f4932" align="top_left" height="40" width="40"> Seerial
A video library manager with an embedded video player. It features a desktop main application that is designed as a content manager that lets you add, edit and remove video content, analyzing your files automatically and downloading the content metadata; and a fullscreen mode controlled by keyboard that acts as the typical TV application to watch the content previoulsy set in the desktop mode.

## Demos from the latest release
This section features a couple of demo videos showing some of the functionalities of the application.
### Desktop Mode Demo 1
[![Desktop Mode Demo 1](https://img.youtube.com/vi/9x7BMvfJzlc/1.jpg)](https://www.youtube.com/watch?v=9x7BMvfJzlc)
### Desktop Mode Demo 2
[![Desktop Mode Demo 2](https://img.youtube.com/vi/bTsA_tDtOLs/1.jpg)](https://www.youtube.com/watch?v=bTsA_tDtOLs)
### Fullscreen Mode Demo
[![Fullscreen Mode Demo](https://img.youtube.com/vi/CpIguBXi0P4/1.jpg)](https://www.youtube.com/watch?v=CpIguBXi0P4)

## Folder structure and naming
### Shows
Folder structure:
```
libraryFolder  
└───show1
│   └───S01
│   │   episode 1.ext
│   │   episode 2.ext
│   │   ...
│   ...
└───show2
    │   episode 1.ext
    │   episode 2.ext
```
`*ext = file extension`

Shows are detected by its root folder, searching for its name and analyzing all of the video files inside that folder. Because of that, the name of the folder has to be the name of the series; and it is best if the name has the `first air date year` in it, though the year is optional and does not need to be between parenthesis.

Example of a show folder:
> **Game of Thrones (2011)**

The video files inside are processed equally and the folders inside are not taken into account, but the name of the video files is key for the algorithm to find the exact match. The video file name needs to have one of this formats:

> **S02E12.mkv**
> 
> **s02e12.mkv**
> 
> **2x12.mkv**
> 
> **2-12.mkv**
> 
> **2 - 12.mkv**
> 
> **II - 12.mkv**      //Yes, seasons can be in roman numbers (up to season X)
> 
> **24.mkv**

Everything extra is optional, like the name of the show or the name of the episode. It only needs to have the season/episode or the absolute episode format. For the first one, the `S/E` can be lowercase `s/e` and the numbers can be in the format `01` or `1`.

### Movies/Concerts
Movies and concerts are processed equally. It is done so in order to add concerts with multiple video files, or movies with extra videos.

Folder structure:
```
libraryFolder
└───movie1.ext
|
└───movie2
|	└───movie1.ext
|
└───collection1
│   └───movie3
│   │   movie3.ext
│   │   extras.ext
│   │   ...
│   └───movie4
│   │   movie4.ext
```
Movies/Concerts can be organized alone or by collections. Collections are created automatically, based on the folder structure, so the collection name will be the root folder name of the collection.

Collections are only created when there are folders inside the root folder (collection folder) and there are video files inside them.

Movies/Concerts are detected by their folder name, so it is recommended to always include the year alongside the title.

Movie examples:
```
The Dark Knight (2008)					//Only file

The Dark Knight (2008)					
└───The Dark Knight (2008).mkv			        //Folder + file

The Dark Knight Collection				
└───The Dark Knight (2008)				//Collection + folder + file
|	└───The Dark Knight (2008).mkv		
```

If a folder contains various video files, it will be cataloged as a movie with extras, which will be the same as a concert with various video files.

## Valid video extensions
- mkv
- mp4
- mpeg
- avi
- mov
- wmv
- m2ts

## Installation
Portable versions exist for Windows and Linux.

### Requirements
This software is required in order for the application to run.

- Java JDK 21 or newer (only for Linux, or if using the `.jar` file directly)
- VLC (only for Linux)
- Python 3
- Python Pytube (`pip install pytube`)
- FFMPEG

## Branches
-   `master`  - default branch representing the state of the currently released build.
-   `develop`  - development branch containing the latest changes.
-   `v1.0`  - old branch with the previous version of the application. It lacks automated file search, music search, the video player and a lot of fullscreen functionality.

## Localization
Localization is done with the help of Java's **ResourceBundle**, having two groups of `.properties` files in the folder `resources/`.  These groups are for **buttons** and **text**, and additional languages can be added just by creating another file in each group.

