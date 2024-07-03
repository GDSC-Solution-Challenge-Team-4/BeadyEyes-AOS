## GDSC Google Solution Challenge - BeadyEyes
### Front repo
---
### How to run my code on local environment by using Android Studio emulator

##### Get started with the emulator
The Android Emulator lets you test your app on many different devices virtually. The emulator comes with Android Studio, so you don't need to install it separately. To use the emulator, follow these basic steps, which are described in more detail in the sections that follow:

1. Verify that you have the system requirements.
2. Create an Android Virtual Device (AVD).
3. Run your app on the emulator.
4. Navigate the emulator.

Emulator system requirements
For the best experience, you should use the emulator in Android Studio on a computer with at least the following specs:

- 16 GB RAM
- 64-bit Windows, macOS, Linux, or ChromeOS operating system
- 16 GB disk space

#### Create an Android Virtual Device
Each instance of the Android Emulator uses an Android virtual device (AVD) to specify the Android version and hardware characteristics of the simulated device. To effectively test your app, create an AVD that models each device your app is designed to run on. To create an AVD, see Create and manage virtual devices.

Each AVD functions as an independent device with its own private storage for user data, SD card, and so on. By default, the emulator stores the user data, SD card data, and cache in a directory specific to that AVD. When you launch the emulator, it loads the user data and SD card data from the AVD directory.

#### Run your app on the emulator
After you have created an AVD, you can start the Android Emulator and run an app in your project:

1. In the toolbar, select the AVD that you want to run your app on from the target device menu.
<p align="center"><img src="https://github.com/GDSC-Solution-Challenge-Team-4/BeadyEyes-AOS/assets/127933902/8e331e2e-09db-4589-8d3b-041bec802db0"></p>

2. Click Run. The emulator might take a minute or so to launch for the first time, but subsequent launches use a snapshot and should launch faster. If you experience issues, see the troubleshooting guide.

Once your app is installed on your AVD, you can run it from the device as you would run any app on a device. Any time you want to deploy new changes, you need to click Run or Apply Changes again.

#### Wear OS pairing assistant
If you want to test your app with Wear OS devices, the Wear OS pairing assistant guides you step-by-step through pairing Wear OS emulators with physical or virtual phones directly in Android Studio. To learn more, see Use the Wear OS emulator pairing assistant.

#### Navigate the emulator
While the emulator is running, you can use your computer mouse pointer to mimic your finger on the touch screen and use the emulator panel to perform common actions.

#### Navigate the emulator screen
Use your computer mouse pointer to mimic your finger on the touchscreen, select menu items and input fields, and click buttons and controls. Use your computer keyboard to type characters and enter emulator shortcuts.

---
If you want more detailed information, please visit the following link: https://developer.android.com/studio/run/emulator#requirements

---

### How to run my code on local environment by using hardware device 

#### Set up a device for development
Before you can start debugging on your device, decide whether you want to connect to the device using a USB cable or Wi-Fi. Then do the following:

On the device, open the Settings app, select Developer options, and then enable USB debugging (if applicable).

Note: If you don't see Developer options, follow the instructions to [enable developer options](https://developer.android.com/studio/run/device?_gl=1*1brd377*_up*MQ..*_ga*MTgyOTIyNjQxNS4xNzA4Nzk0MDQx*_ga_6HH9YJMN9M*MTcwODc5NDA0MS4xLjAuMTcwODc5NDA0MS4wLjAuMA..).

####Set up your system to detect your device.

- ChromeOS: No additional configuration required.
- macOS: No additional configuration required.
- Windows: Install a USB driver for ADB (if applicable). For an installation guide and links to OEM drivers, see Install OEM USB drivers.

#### Connect to your device using USB
When you're set up and plugged in over USB, click Run  in Android Studio to build and run your app on the device.

You can also use adb to issue commands, as follows:

- Verify that your device is connected by running the adb devices command from your android_sdk/platform-tools/ directory. If connected, you'll see the device listed.
- Issue any adb command with the -d flag to target your device

---
If you want more detailed information, please visit the following link: 
https://developer.android.com/studio/run/device?_gl=1*1brd377*_up*MQ..*_ga*MTgyOTIyNjQxNS4xNzA4Nzk0MDQx*_ga_6HH9YJMN9M*MTcwODc5NDA0MS4xLjAuMTcwODc5NDA0MS4wLjAuMA..

---

#### The application requires Android API level 34 or higher. 
#### It also requires permissions for audio recording and camera usage to be granted while the application is in use
