# Loading Status

Loading Status is the third project of the Android Kotlin Developer nanodegree provided by Udacity. It is a test project for the second chapter of the course, Advanced Android Apps with Kotlin. The project is based on downloading a file, getting a notification when the download terminates, and handling such notification. Moreover, to simulate the loading process when performing the file download, a custom view has been created from scratch by extending the general Android View class.
The project demonstrates the ability to exploit the components already seen in the first chapter, Developing Android Apps with Kotlin (see projects [Shoe Store](https://github.com/PaoloCaldera/shoeStore) and [Asteroid Radar](https://github.com/PaoloCaldera/asteroidRadar)), but it also demonstrates the knowledge of:

* [Notifications](https://developer.android.com/develop/ui/views/notifications), appearing in the status bar
* [View](https://developer.android.com/reference/android/view/View) class, to create custom views
* [ObjectAnimator](https://developer.android.com/reference/android/animation/ObjectAnimator) to create simple animations
* [MotionLayout](https://developer.android.com/develop/ui/views/animations/motionlayout) ViewGroup, to create animations as complex as you like

The project consists of one main screen, the one displaying the downloading process, and a second screen that is opened when clicking the notification.

Visit the [Wiki]() to see the application screens.


## Getting Started
To clone the repository, use the command
```
$ git clone https://github.com/PaoloCaldera/loadingStatus.git
```
or the `Get from VCS` option inside Android Studio by copying the link above.

Then, run the application on an Android device or emulator. The application is compiled with API 33, thus use a device or emulator supporting such API version.
For complete usage of the application, be sure that the device or emulator is connected to a Wi-Fi network.
