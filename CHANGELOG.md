# Back log of changes in NavigineSDK library

All notable changes to this project will be documented in this file. NavigineSDK
uses calendar versioning in the format `YYYYMMDD`.

## Version 20201103

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/8ed48a645236658e6260c416caca86ac92b551ac/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms improved to version 2.5

## Version 20201007

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/d85e04eb08571d241ec76ff0c0d501b1974d0b00/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms improved

## Version 20200929

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/83112d8e675064db7f08a9f018162cd687051b03/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 2.4


## Version 20200924

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/a3629c616ab58a3d6271a27bbc882888ae7a1cae/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms improved

## Version 20200617

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/0d081bc251cad0a63a9a19a253339872ce8fd198/libs/NavigineSDK.jar?raw=true)

* Added public function for providing custom wifi rtt signals.
```java
  public void provideWifiRttMeasurement(String BSSID, float distance, int level, float stddev)
```
* Navigation using WiFi RTT improved

## Version 20200601

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/a58687bf3a364e53193c9e9f32344be0255101d8/libs/NavigineSDK.jar?raw=true)

* Added public function for uploading user files.
```java
  public static boolean uploadFile(String filePath, UploadFileListener uploadListener);
```
* Added UploadFileListener class.
```java
  public class UploadFileListener
  {
    public UploadFileListener( ) { }
    public void onFinished ( ) { }
    public void onFailed   ( int error ) { }
    public void onUpdate   ( int state ) { }
  };
```

## Version 20200515

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/7da35fdd9f7b22a0d5426a10ca53c29f57b5a77a/libs/NavigineSDK.jar?raw=true)

* Added public function for removing locations list file.
```java
  public boolean removeLocationsList();
```
* Heatmaps and Zones storage size increased

## Version 20200509

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/5f330d1f56079c9daa8c9c08042f2e00d13a181f/libs/NavigineSDK.jar?raw=true)

* Added public function for publishing user events.
```java
  public String publishUserEvent(String content);
```

## Version 20200423

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/3cb56f53f65909ed77f7ecfffd36f6a1a6845bfb/libs/NavigineSDK.jar?raw=true)

* Added public function for providing WiFi and Bluetooth measurements on NavigationThread class.
```java
  public void provideBluetoothMeasurement(String name, 
                                          String address, 
                                          int rssi, 
                                          byte[] scanRecord);
  public void provideWifiMeasurement(List<android.net.wifi.ScanResult> scanResults);
```
* Added fling and zoom animations to location view.

## Version 20200228

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/2dac5d71ece61878c8a7c73c868f74d1038916ab/libs/NavigineSDK.jar?raw=true)

* Added Eddystone scanning for Android devices with version >= 21

## Version 20200212

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/f9fc5594d2321c4a28fd051c8c9b0f7eb639a325/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 2.3
* Added background scanning for Android versions >= 26
* Improved background scanning for Android versions < 26

## Version 20191220

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/21a23858abd688639674054f9be7b67301a0c28a/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 2.2
* Added support for graph smoothing in navigation.
* Removed support of track file recording. Following methods are removed from [NavigationThread](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread) class:

```java
  public void setTrackFile(String filename)
  public String getTrackFile()
```

* Navigation frequency changed to 1 per second
* Class Location.Info changed to [LocationInfo](https://github.com/Navigine/Android-SDK/wiki/Class-LocationInfo)
* Class [Beacon](https://github.com/Navigine/Android-SDK/wiki/Class-Beacon): public fields were replaced by corresponding getters, constructors will be removed in further versions
```java
  @Deprecated public int id                   // replaced by getId()
  @Deprecated public int location             // replaced by getLocationId()
  @Deprecated public int subLocation          // replaced by getSubLocationId()
  @Deprecated public int major                // replaced by getMajor()
  @Deprecated public int minor                // replaced by getMinor()
  @Deprecated public String uuid              // replaced by getUuid()
  @Deprecated public String name              // replaced by getName()
  @Deprecated public int power                // replaced by getPower()
  @Deprecated public float x                  // replaced by getX()
  @Deprecated public float y                  // replaced by getY()
  @Deprecated public Beacon()
  @Deprecated public Beacon(Beacon B)
```

* Class [DeviceInfo](https://github.com/Navigine/Android-SDK/wiki/Class-DeviceInfo): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int id                   // replaced by getId()
  @Deprecated public int location             // replaced by getLocationId()
  @Deprecated public int subLocation          // replaced by getSubLocationId()
  @Deprecated public long time                // replaced by getTime()
  @Deprecated public float x                  // replaced by getX()
  @Deprecated public float y                  // replaced by getY()
  @Deprecated public float r                  // replaced by getR()
  @Deprecated public float azimuth            // replaced by getAzimuth()
  @Deprecated public float stepLength         // replaced by getStepLength()
  @Deprecated public int stepCount            // replaced by getStepCount()
  @Deprecated public int errorCode            // replaced by getErrorCode()
  @Deprecated public List<RoutePath> paths    // replaced by getPaths()
  @Deprecated public List<Zone> zones         // replaced by getZones()
  @Deprecated public DeviceInfo()
  @Deprecated public DeviceInfo(DeviceInfo info)
```

* Class [Location](https://github.com/Navigine/Android-SDK/wiki/Class-Location): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int id                           // replaced by getId()
  @Deprecated public int version                      // replaced by getVersion()
  @Deprecated public String name                      // replaced by getName()
  @Deprecated public String description               // replaced by getDescription()
  @Deprecated public List<SubLocation> subLocations   // replaced by getSubLocations()
  @Deprecated public List<Venue.Category> categories  // replaced by getCategories()
  @Deprecated public Location()
  @Deprecated public Location(Location loc)
```

* Class [RouteEvent](https://github.com/Navigine/Android-SDK/wiki/class-RouteEvent): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int type                     // replaced by getType()
  @Deprecated public int value                    // replaced by getValue()
  @Deprecated public float distance               // replaced by getDistance()
  @Deprecated public RouteEvent()
  @Deprecated public RouteEvent(RouteEvent event)
```

* Class [RoutePath](https://github.com/Navigine/Android-SDK/wiki/Class-RoutePath): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public float length                 // replaced by getLength()
  @Deprecated public List<RouteEvent> events      // replaced by getEvents()
  @Deprecated public List<LocationPoint> points   // replaced by getPoints()
  @Deprecated public RoutePath()
  @Deprecated public RoutePath(RoutePath path)
```

* Class [SubLocation](https://github.com/Navigine/Android-SDK/wiki/Class-SubLocation): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int id                     // replaced by getId()
  @Deprecated public int location               // replaced by getLocationId()
  @Deprecated public String name                // replaced by getName()
  @Deprecated public float width                // replaced by getWidth()
  @Deprecated public float height               // replaced by getHeight()
  @Deprecated public float azimuth              // replaced by getAzimuth()
  @Deprecated public double latitude            // replaced by getLatitude()
  @Deprecated public double longitude           // replaced by getLongitude()
  @Deprecated public List<Beacon> beacons       // replaced by getBeacons()
  @Deprecated public List<Venue> venues         // replaced by getVenues()
  @Deprecated public List<Zone> zones           // replaced by getZones()
  @Deprecated public SubLocation()
  @Deprecated public SubLocation(SubLocation subLoc)
```

* Class [Venue](https://github.com/Navigine/Android-SDK/wiki/Class-Venue): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int id                     // replaced by getId()
  @Deprecated public int location               // replaced by getLocationId()
  @Deprecated public int subLocation            // replaced by getSubLocationId()
  @Deprecated public float x                    // replaced by getX()
  @Deprecated public float y                    // replaced by getY()
  @Deprecated public String name                // replaced by getName()
  @Deprecated public String alias               // replaced by getAlias()
  @Deprecated public String phone               // replaced by getPhone()
  @Deprecated public String image               // replaced by getImage()
  @Deprecated public String description         // replaced by getDescription()
  @Deprecated public Category category          // replaced by getCategory()
  @Deprecated public Venue()
  @Deprecated public Venue(Venue v)
```

* Class [Venue.Category](https://github.com/Navigine/Android-SDK/wiki/Class-Venue.Category): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int id                     // replaced by getId()
  @Deprecated public String name                // replaced by getName()
  @Deprecated public Venue.Category()
  @Deprecated public Venue.Category(Venue.Category c)
```

* Class [Zone](https://github.com/Navigine/Android-SDK/wiki/Class-Zone): public fields were replaced by corresponding getters, constructors will be removed in further versions:
```java
  @Deprecated public int id                       // replaced by getId()
  @Deprecated public int location                 // replaced by getLocationId()
  @Deprecated public int subLocation              // replaced by getSubLocationId()
  @Deprecated public String name                  // replaced by getName()
  @Deprecated public String alias                 // replaced by getAlias()
  @Deprecated public String color                 // replaced by getColor()
  @Deprecated public List<LocationPoint> points   // replaced by getPoints()
  @Deprecated public Zone()
  @Deprecated public Zone(Zone Z)
```

## Version 20191014

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/a6e3148d50ed0629acc6eecd892b9479e358d84b/libs/NavigineSDK.jar?raw=true)

* Improved LocationView

## Version 20190723

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/606bff20da6037818aad147dc2f477279a79ddc1/libs/NavigineSDK.jar?raw=true)

* Added RTT measurements support
* Navigation algorithms updated to version 2.0

## Version 20190626

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/4317b949ebf89c4aa803c3e2286472f72c26be9d/libs/NavigineSDK.jar?raw=true)

* Bugfixes in class `LocationView`

## Version 20190603

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/18d3b9f4e25e819b57a83ea94b7dc18f8d5a8496/libs/NavigineSDK.jar?raw=true)

* Minor bugfixes and improvements

## Version 20190429

Download link [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/3e713e7a376031531b8f6b5a96c9957dac06952b/libs/NavigineSDK.jar?raw=true)

* Added class [SubLocationImage](https://github.com/Navigine/Android-SDK/wiki/Class-SubLocationImage)
* Following fields and methods are marked as `@Deprecated` in class `SubLocation`:
  * `svgFile`
  * `pngFile`
  * `jpgFile`
  * `getSvgImage()`
  * `getPngImage()`
  * `getJpgImage()`
  * `getPicture()`
  * `getBitmap()`
  * `getBitmap(int maxsize)`
* Added following method in class `SubLocation`:

```java
SubLocationImage getImage()
```

* Navigation algorithms updated to version 1.17

## Version 20190228

Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/f65ffca0030d58c9df591084b55246d410728ddf/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 1.14

## Version 20190215

Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/98ff65bbe54e0820ea78eec003f23e2221274b53/libs/NavigineSDK.jar?raw=true)

* Minor bugfixes

## Version 20190208

Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/369a6ba6c2bc2989568addadb587b5896c2aed54/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 1.13

## Version 20190114

Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/88322912705082e41d8637cdaf4d9b5c8479d432/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 1.12

## Version 20181207

Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/9f908e86d0b3869256d028d79f27a5d453cdb8c9/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 1.10

## Version 20181109
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/1f1c82ff1ec591cc0804899997ecec21acac1534/libs/NavigineSDK.jar?raw=true)

* Reworked notification handling (More information [here](https://github.com/Navigine/Android-SDK/wiki/Push-notifications))
* Added methods to LocationView.Listener
```java
  void onLoadFinished()
  void onLoadFailed()
```
* Navigation algorithms updated to version 1.8

## Version 20181009
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/d9466f503ac33dfd383dae03f3774fb7a92e417e/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 1.7

## Version 20180907
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/47688f140521e483fbdd342a9291062a10fa23b4/libs/NavigineSDK.jar?raw=true)

* Fixed memory leak in LocationView
* Navigation algorithms updated to version 1.5

## Version 20180720
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/de4e177cf41bbc2f71b6564710abf46b8763efbc/libs/NavigineSDK.jar?raw=true)

The following functions were made public:
```java
public static boolean NavigineSDK.loadLocation(String location)
public static boolean NavigineSDK.loadLocation(String location, int timeout)
```

## Version 20180712
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/d233ffbeb0874ba8c05bbece389f71f63166b433/libs/NavigineSDK.jar?raw=true)

* Fixed bug in NavigineSDK.loadLocationInBackgroundCancel
* Navigation algorithms updated to version 1.3

## Version 20180620
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/4e129d99487168b105e51981deaa2aa2dc3f2837/libs/NavigineSDK.jar?raw=true)

* [Location.LoadListener](https://github.com/Navigine/Android-SDK/wiki/Class-Location.LoadListener):
functions
```java
Location.LoadListener.onFinished ( String location )
Location.LoadListener.onFailed   ( String location, int error )
Location.LoadListener.onUpdate   ( String location, int error )

Location.LoadListener.onFinished ( int locationId)
Location.LoadListener.onFailed   ( int locationId, int error )
Location.LoadListener.onUpdate   ( int locationId, int error )
```
are replaced by:
```java
Location.LoadListener.onFinished ( )
Location.LoadListener.onFailed   ( int error )
Location.LoadListener.onUpdate   ( int error )
```

## Version 20180523
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/2f46c63ff66b60d34de2be3490a2ab1820d83d91/libs/NavigineSDK.jar?raw=true)

* Navigation algorithms updated to version 1.1.0

* Added functions:
```java
DeviceInfo.inZone(int id)
DeviceInfo.inZone(String alias)
SubLocation.getZone(String alias)
Location.getZone(String alias)
Location.getZones(LocationPoint P)
```

## Version 20180513
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/f1c725d4b138351002001fed7e48d428d765466b/libs/NavigineSDK.jar?raw=true)

* Fixed synchronization bugs (deadlocks, ANRs)

## Version 20180503
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/5a74248a388685f5532976482162b1c5124dc253/NavigineSDK/NavigineSDK.jar?raw=true)

* Improved navigation algorithms

* Fixed validation regex for `server_url` parameter

* Added validation regex for `user_hash` parameter

* Added class [Location.LoadListener](https://github.com/Navigine/Android-SDK/wiki/Class-Location.LoadListener)

* Added asynchronous functions for loading locations for `NavigineSDK`:
  * [loadLocationInBackground](https://github.com/Navigine/Android-SDK/wiki/Class-NavigineSDK#function-loadlocationinbackground)
  * [loadLocationInBackgroundCancel](https://github.com/Navigine/Android-SDK/wiki/Class-NavigineSDK#function-loadlocationinbackgroundCancel)

* Added beacon emulation functions for `NavigationThread`:
  * [addBeaconGenerator](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread#function-addbeacongenerator),
  * [removeBeaconGenerator](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread#function-removebeacongenerator),
  * [removeBeaconGenerators](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread#function-removebeacongenerators)

## Version 20180423
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/blob/b9eb40e39166ab873490ef09a8a602863559d466/NavigineSDK/NavigineSDK.jar?raw=true)

* Fixed bug in `DeviceInfo` radius (member field `DeviceInfo.r`)

* Fixed bug in `DeviceInfo` conversion to `GlobalPoint` (function `DeviceInfo.getGlobalPoint`)

* Added periodic BLE scan restart (to fix the problem of stopping BLE measurements after 30 minutes of continuous scanning on Android 7 or higher)

* Added extra validation of the location archives after downloading

## Version 20180411
Download link: [NavigineSDK.jar](https://github.com/Navigine/Android-SDK/raw/d4c0e75ed5b40c266da4561f91a9f1070fd3196c/NavigineSDK/NavigineSDK.jar)

* Improved navigation algorithms

* Fixed bugs in network API

* Added validation of parameter `server_url` according to the pattern:
`http[s]://hostname[:port]`

* DeviceInfo: added function `getGlobalPoint` for converting sublocation
coordinates of the device into the global geographic coordinates:
[wiki](https://github.com/Navigine/Android-SDK/wiki/Class-DeviceInfo#function-getglobalpoint)

* Added abstract interface `DeviceInfo.Listener`:
[wiki](https://github.com/Navigine/Android-SDK/wiki/Class-DeviceInfo.Listener)

* NavigationThread: added function `setDeviceListener`. Function is called every time when NavigationThread updates the device position: [wiki](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread#function-setdevicelistener)

* LocationView.Listener: functions
```java
void onScroll (float x, float y)
void onZoom   (float ratio)
```
are replaced by:
```java
void onScroll (float x, float y, boolean isTouchEvent)
void onZoom   (float ratio,      boolean isTouchEvent)
```

[wiki](https://github.com/Navigine/Android-SDK/wiki/Class-LocationView.Listener)

## Version 20180302

* NavigineSDK: function `getLocationFile` is marked as **deprecated**. In future it
will be removed.

* NavigationThread: function `loadLocation`, taking the location archive file
path as argument, is replaced by:
```java
public boolean loadLocation(int locationId)
public boolean loadLocation(String location)
```

[wiki](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread#function-loadlocation)

## Version 20180221

* Improved navigation algorithms

* NavigationThread: added function `setTrackFile` for track recording:
[wiki](https://github.com/Navigine/Android-SDK/wiki/Class-NavigationThread#function-settrackfile)

## Version 20170601

* Support of PNG maps with large dimensions added.

* SVG maps support improved.

* Minor bugs were fixed.

## Version 20170427

* Fixed crush in location downloading.

* Navigation accuracy improved.

## Version 20170314

* Background service stability and battery usage improved.

* Navigation accuracy improved.

## Version 20170220

* Major updates in internal SDK storage - to support locations with local symbols in name.

* Navigation accuracy improved.

* Fixed bug with iBeacon battery level transmition to Navigine CMS.
