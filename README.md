# GalleryDemo

This is a demo project that shows all the media (image/video) folders from the external storage and the media inside each folder in its detail screen. I've used Kotlin, MVVM architecture, LiveData, ViewModel, Navigation, Hilt, Databinding, Splash and Glide in it. This project targets android api 33. So, it is compatible with all latest devices till date. This demo is supporting Day/Night modes, English/Arabic languages and Landscape mode as well.
Screens are as follows:

- Permissions:
    
    In this screen we request for storage permission, then after getting approval we navigate to folders screen.

- Gallery Folders:    
    
    In this screen we load all media from external storage and show them in Grid form by default, but user can toggle between grid and linear layout. When any folder is clicked then its all media is shown in Media List screen.
    
- Media List:
    
    In this screen we show images and videos of the selected folder. User can see information of any media by clicking on it.
    
    
    
