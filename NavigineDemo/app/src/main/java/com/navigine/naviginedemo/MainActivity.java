package com.navigine.naviginedemo;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.util.*;
import java.io.*;
import java.lang.*;
import java.util.*;

import com.navigine.naviginesdk.*;

import static android.view.View.GONE;

public class MainActivity extends Activity
{
  private static final String   TAG                     = "NAVIGINE.Demo";
  private static final String   NOTIFICATION_CHANNEL    = "NAVIGINE_DEMO_NOTIFICATION_CHANNEL";
  private static final int      UPDATE_TIMEOUT          = 100;  // milliseconds
  private static final int      ADJUST_TIMEOUT          = 5000; // milliseconds
  private static final int      ERROR_MESSAGE_TIMEOUT   = 5000; // milliseconds
  private static final boolean  ORIENTATION_ENABLED     = true; // Show device orientation?
  private static final boolean  NOTIFICATIONS_ENABLED   = true; // Show zone notifications?
  
  // NavigationThread instance
  private NavigationThread mNavigation            = null;

  // UI Parameters
  private LocationView  mLocationView             = null;
  private Button        mPrevFloorButton          = null;
  private Button        mNextFloorButton          = null;
  private View          mBackView                 = null;
  private View          mPrevFloorView            = null;
  private View          mNextFloorView            = null;
  private View          mZoomInView               = null;
  private View          mZoomOutView              = null;
  private View          mAdjustModeView           = null;
  private TextView      mCurrentFloorLabel        = null;
  private TextView      mErrorMessageLabel        = null;
  private Handler       mHandler                  = new Handler();
  private float         mDisplayDensity           = 0.0f;

  private boolean       mAdjustMode               = false;
  private long          mAdjustTime               = 0;

  // Location parameters
  private Location      mLocation                 = null;
  private int           mCurrentSubLocationIndex  = -1;

  // Device parameters
  private DeviceInfo    mDeviceInfo               = null; // Current device
  private LocationPoint mPinPoint                 = null; // Potential device target
  private LocationPoint mTargetPoint              = null; // Current device target
  private RectF         mPinPointRect             = null;

  private RelativeLayout mDirectionLayout         = null;
  private TextView       mDirectionTextView       = null;
  private ImageView      mDirectionImageView      = null;

  private Bitmap  mVenueBitmap    = null;
  private Venue   mTargetVenue    = null;
  private Venue   mSelectedVenue  = null;
  private RectF   mSelectedVenueRect = null;
  private Zone    mSelectedZone   = null;
  
  @Override protected void onCreate(Bundle savedInstanceState)
  {
    Log.d(TAG, "MainActivity started");
    
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                         WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    // Setting up GUI parameters
    mBackView = (View)findViewById(R.id.navigation__back_view);
    mPrevFloorButton = (Button)findViewById(R.id.navigation__prev_floor_button);
    mNextFloorButton = (Button)findViewById(R.id.navigation__next_floor_button);
    mPrevFloorView = (View)findViewById(R.id.navigation__prev_floor_view);
    mNextFloorView = (View)findViewById(R.id.navigation__next_floor_view);
    mCurrentFloorLabel = (TextView)findViewById(R.id.navigation__current_floor_label);
    mZoomInView  = (View)findViewById(R.id.navigation__zoom_in_view);
    mZoomOutView = (View)findViewById(R.id.navigation__zoom_out_view);
    mAdjustModeView = (View)findViewById(R.id.navigation__adjust_mode_view);
    mErrorMessageLabel = (TextView)findViewById(R.id.navigation__error_message_label);

    mDirectionTextView  = findViewById(R.id.navigation__direction_distance);
    mDirectionImageView = findViewById(R.id.navigation__direction_image);
    mDirectionLayout    = findViewById(R.id.navigation__direction_layout);

    mBackView.setVisibility(View.INVISIBLE);
    mPrevFloorView.setVisibility(View.INVISIBLE);
    mNextFloorView.setVisibility(View.INVISIBLE);
    mCurrentFloorLabel.setVisibility(View.INVISIBLE);
    mZoomInView.setVisibility(View.INVISIBLE);
    mZoomOutView.setVisibility(View.INVISIBLE);
    mAdjustModeView.setVisibility(View.INVISIBLE);
    mErrorMessageLabel.setVisibility(View.GONE);
    
    mVenueBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.elm_venue);
    
    // Initializing location view
    mLocationView = (LocationView)findViewById(R.id.navigation__location_view);
    mLocationView.setBackgroundColor(0xffebebeb);
    mLocationView.setListener
    (
      new LocationView.Listener()
      {
        @Override public void onClick     ( float x, float y ) { handleClick(x, y);     }
        @Override public void onLongClick ( float x, float y ) { handleLongClick(x, y); }
        @Override public void onScroll    ( float x, float y, boolean byTouchEvent ) { handleScroll ( x, y,  byTouchEvent ); }
        @Override public void onZoom      ( float ratio,      boolean byTouchEvent ) { handleZoom   ( ratio, byTouchEvent ); }
        
        @Override public void onDraw(Canvas canvas)
        {
          drawZones(canvas);
          drawPoints(canvas);
          drawVenues(canvas);
          drawDevice(canvas);
        }
      }
    );

    mNavigation = NavigineSDK.getNavigation();
    
    // Loading map only when location view size is known
    mLocationView.addOnLayoutChangeListener
    (
      new OnLayoutChangeListener()
      {
        @Override public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
        {
          int width  = right  - left;
          int height = bottom - top;
          if (width == 0 || height == 0)
            return;
          
          Log.d(TAG, "Layout changed: " + width + "x" + height);
          
          int oldWidth  = oldRight  - oldLeft;
          int oldHeight = oldBottom - oldTop;
          if (oldWidth != width || oldHeight != height)
            loadMap();
        }
      }
    );
    
    mDisplayDensity = getResources().getDisplayMetrics().density;
    mDirectionLayout.setVisibility(GONE);
    
    // Setting up device listener
    if (mNavigation != null)
    {
      mNavigation.setDeviceListener
      (
        new DeviceInfo.Listener()
        {
          @Override public void onUpdate(DeviceInfo info) { handleDeviceUpdate(info); }
        }
      );
    }
    
    // Setting up zone listener
    if (mNavigation != null)
    {
      mNavigation.setZoneListener
      (
        new Zone.Listener()
        {
          @Override public void onEnterZone(Zone z) { handleEnterZone(z); }
          @Override public void onLeaveZone(Zone z) { handleLeaveZone(z); }
        }
      );
    }
    
    if (NOTIFICATIONS_ENABLED)
    {
      NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      if (Build.VERSION.SDK_INT >= 26)
        notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL, "default",
                                                                              NotificationManager.IMPORTANCE_LOW));
    }
  }
  
  @Override public void onDestroy()
  {
    if (mNavigation != null)
    {
      NavigineSDK.finish();
      mNavigation = null;
    }
    
    super.onDestroy();
  }

  @Override public void onBackPressed()
  {
    moveTaskToBack(true);
  }
  
  public void toggleAdjustMode(View v)
  {
    mAdjustMode = !mAdjustMode;
    mAdjustTime = 0;
    Button adjustModeButton = (Button)findViewById(R.id.navigation__adjust_mode_button);
    adjustModeButton.setBackgroundResource(mAdjustMode ?
                                           R.drawable.btn_adjust_mode_on :
                                           R.drawable.btn_adjust_mode_off);
    mLocationView.redraw();
  }

  public void onNextFloor(View v)
  {
    if (loadNextSubLocation())
      mAdjustTime = System.currentTimeMillis() + ADJUST_TIMEOUT;
  }

  public void onPrevFloor(View v)
  {
    if (loadPrevSubLocation())
      mAdjustTime = System.currentTimeMillis() + ADJUST_TIMEOUT;
  }

  public void onZoomIn(View v)
  {
    mLocationView.zoomBy(1.25f);
  }

  public void onZoomOut(View v)
  {
    mLocationView.zoomBy(0.8f);
  }

  public void onMakeRoute(View v)
  {
    if (mNavigation == null)
      return;

    if (mPinPoint == null)
      return;

    mTargetPoint  = mPinPoint;
    mTargetVenue  = null;
    mPinPoint     = null;
    mPinPointRect = null;

    mNavigation.setTarget(mTargetPoint);
    mBackView.setVisibility(View.VISIBLE);
    mLocationView.redraw();
  }

  public void onCancelRoute(View v)
  {
    if (mNavigation == null)
      return;

    mTargetPoint  = null;
    mTargetVenue  = null;
    mPinPoint     = null;
    mPinPointRect = null;

    mNavigation.cancelTargets();
    mBackView.setVisibility(View.GONE);
    mDirectionLayout.setVisibility(GONE);
    mLocationView.redraw();
  }

  private void handleClick(float x, float y)
  {
    Log.d(TAG, String.format(Locale.ENGLISH, "Click at (%.2f, %.2f)", x, y));
    
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;
    
    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    if (subLoc == null)
      return;
    
    if (mPinPoint != null)
    {
      if (mPinPointRect != null && mPinPointRect.contains(x, y))
      {
        mTargetPoint  = mPinPoint;
        mTargetVenue  = null;
        mPinPoint     = null;
        mPinPointRect = null;
        mNavigation.setTarget(mTargetPoint);
        mBackView.setVisibility(View.VISIBLE);
        return;
      }
      cancelPin();
      return;
    }
    
    if (mSelectedVenue != null)
    {
      if (mSelectedVenueRect != null && mSelectedVenueRect.contains(x, y))
      {
        mTargetVenue = mSelectedVenue;
        mTargetPoint = null;
        mNavigation.setTarget(new LocationPoint(mLocation.getId(), subLoc.getId(), mTargetVenue.getX(), mTargetVenue.getY()));
        mBackView.setVisibility(View.VISIBLE);
      }
      cancelVenue();
      return;
    }
    
    // Check if we touched venue
    mSelectedVenue = getVenueAt(x, y);
    mSelectedVenueRect = new RectF();
    
    // Check if we touched zone
    if (mSelectedVenue == null)
    {
      Zone Z = getZoneAt(x, y);
      if (Z != null)
        mSelectedZone = (mSelectedZone == Z) ? null : Z;
    }
    
    mLocationView.redraw();
  }
  
  private void handleLongClick(float x, float y)
  {
    Log.d(TAG, String.format(Locale.ENGLISH, "Long click at (%.2f, %.2f)", x, y));
    makePin(mLocationView.getAbsCoordinates(x, y));
    cancelVenue();
  }
  
  private void handleScroll(float x, float y, boolean byTouchEvent)
  {
    if (byTouchEvent)
      mAdjustTime = NavigineSDK.currentTimeMillis() + ADJUST_TIMEOUT;
  }
  
  private void handleZoom(float ratio, boolean byTouchEvent)
  {
    if (byTouchEvent)
      mAdjustTime = NavigineSDK.currentTimeMillis() + ADJUST_TIMEOUT;
  }
  
  private void handleEnterZone(Zone z)
  {
    Log.d(TAG, "Enter zone " + z.getName());
    if (NOTIFICATIONS_ENABLED)
    {
      Intent notificationIntent = new Intent(this, NotificationActivity.class);
      notificationIntent.putExtra("zone_id",    z.getId());
      notificationIntent.putExtra("zone_name",  z.getName());
      notificationIntent.putExtra("zone_color", z.getColor());
      notificationIntent.putExtra("zone_alias", z.getAlias());
      
      // Setting up a notification
      Notification.Builder notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL);
      notificationBuilder.setContentIntent(PendingIntent.getActivity(this, z.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
      notificationBuilder.setContentTitle("New zone");
      notificationBuilder.setContentText("You have entered zone '" + z.getName() + "'");
      notificationBuilder.setSmallIcon(R.drawable.elm_logo);
      notificationBuilder.setAutoCancel(true);
      
      // Posting a notification
      NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(z.getId(), notificationBuilder.build());
    }
  }
  
  private void handleLeaveZone(Zone z)
  {
    Log.d(TAG, "Leave zone " + z.getName());
    if (NOTIFICATIONS_ENABLED)
    {
      NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.cancel(z.getId());
    }
  }
  
  private void handleDeviceUpdate(DeviceInfo deviceInfo)
  {
    mDeviceInfo = deviceInfo;
    if (mDeviceInfo == null)
      return;
    
    // Check if location is loaded
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;

    if (mDeviceInfo.isValid() && mDeviceInfo.getPaths().size() != 0) {
      if (mDeviceInfo.getPaths().get(0).getEvents().size() >= 1)
        showDirections(mDeviceInfo.getPaths().get(0));
    }
    else
      mDirectionLayout.setVisibility(GONE);
    
    if (mDeviceInfo.isValid())
    {
      cancelErrorMessage();
      mBackView.setVisibility(mTargetPoint != null || mTargetVenue != null ?
                              View.VISIBLE : View.GONE);
      if (mAdjustMode)
        adjustDevice();
    }
    else
    {
      mBackView.setVisibility(View.GONE);
      switch (mDeviceInfo.getErrorCode())
      {
        case 4:
          setErrorMessage("You are out of navigation zone! Please, check that your bluetooth is enabled!");
          break;
        
        case 8:
        case 30:
          setErrorMessage("Not enough beacons on the location! Please, add more beacons!");
          break;
        
        default:
          setErrorMessage(String.format(Locale.ENGLISH,
                          "Something is wrong with location '%s' (error code %d)! " +
                          "Please, contact technical support!",
                          mLocation.getName(), mDeviceInfo.getErrorCode()));
          break;
      }
    }
    
    // This causes map redrawing
    mLocationView.redraw();
  }
  
  private void setErrorMessage(String message)
  {
    mErrorMessageLabel.setText(message);
    mErrorMessageLabel.setVisibility(View.VISIBLE);
  }
  
  private void cancelErrorMessage()
  {
    mErrorMessageLabel.setVisibility(View.GONE);
  }
  
  private boolean loadMap()
  {
    if (mNavigation == null)
    {
      Log.e(TAG, "Can't load map! Navigine SDK is not available!");
      return false;
    }
    
    mLocation = mNavigation.getLocation();
    mCurrentSubLocationIndex = -1;
    
    if (mLocation == null)
    {
      Log.e(TAG, "Loading map failed: no location");
      return false;
    }
    
    if (mLocation.getSubLocations().size() == 0)
    {
      Log.e(TAG, "Loading map failed: no sublocations");
      mLocation = null;
      return false;
    }
    
    if (!loadSubLocation(0))
    {
      Log.e(TAG, "Loading map failed: unable to load default sublocation");
      mLocation = null;
      return false;
    }
    
    if (mLocation.getSubLocations().size() >= 2)
    {
      mPrevFloorView.setVisibility(View.VISIBLE);
      mNextFloorView.setVisibility(View.VISIBLE);
      mCurrentFloorLabel.setVisibility(View.VISIBLE);
    }
    mZoomInView.setVisibility(View.VISIBLE);
    mZoomOutView.setVisibility(View.VISIBLE);
    mAdjustModeView.setVisibility(View.VISIBLE);    
    
    mNavigation.setMode(NavigationThread.MODE_NORMAL);
    
    if (D.WRITE_LOGS)
      mNavigation.setLogFile(getLogFile("log"));
    
    mLocationView.redraw();
    return true;
  }
  
  private boolean loadSubLocation(int index)
  {
    if (mNavigation == null)
      return false;
    
    if (mLocation == null || index < 0 || index >= mLocation.getSubLocations().size())
      return false;
    
    SubLocation subLoc = mLocation.getSubLocations().get(index);
    Log.d(TAG, String.format(Locale.ENGLISH, "Loading sublocation %s (%.2f x %.2f)", subLoc.getName(), subLoc.getWidth(), subLoc.getHeight()));
    
    if (subLoc.getWidth() < 1.0f || subLoc.getHeight() < 1.0f)
    {
      Log.e(TAG, String.format(Locale.ENGLISH, "Loading sublocation failed: invalid size: %.2f x %.2f", subLoc.getWidth(), subLoc.getHeight()));
      return false;
    }
    
    if (!mLocationView.loadSubLocation(subLoc))
    {
      Log.e(TAG, "Loading sublocation failed: invalid image");
      return false;
    }
    
    float viewWidth  = mLocationView.getWidth();
    float viewHeight = mLocationView.getHeight();
    float minZoomFactor = Math.min(viewWidth / subLoc.getWidth(), viewHeight / subLoc.getHeight());
    float maxZoomFactor = LocationView.ZOOM_FACTOR_MAX;
    mLocationView.setZoomRange(minZoomFactor, maxZoomFactor);
    mLocationView.setZoomFactor(minZoomFactor);
    Log.d(TAG, String.format(Locale.ENGLISH, "View size: %.1f x %.1f", viewWidth, viewHeight));
    
    mAdjustTime = 0;
    mCurrentSubLocationIndex = index;
    mCurrentFloorLabel.setText(String.format(Locale.ENGLISH, "%d", mCurrentSubLocationIndex));
    
    if (mCurrentSubLocationIndex > 0)
    {
      mPrevFloorButton.setEnabled(true);
      mPrevFloorView.setBackgroundColor(Color.parseColor("#90aaaaaa"));
    }
    else
    {
      mPrevFloorButton.setEnabled(false);
      mPrevFloorView.setBackgroundColor(Color.parseColor("#90dddddd"));
    }
    
    if (mCurrentSubLocationIndex + 1 < mLocation.getSubLocations().size())
    {
      mNextFloorButton.setEnabled(true);
      mNextFloorView.setBackgroundColor(Color.parseColor("#90aaaaaa"));
    }
    else
    {
      mNextFloorButton.setEnabled(false);
      mNextFloorView.setBackgroundColor(Color.parseColor("#90dddddd"));
    }
    
    cancelVenue();
    mLocationView.redraw();
    return true;
  }
  
  private boolean loadNextSubLocation()
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return false;
    return loadSubLocation(mCurrentSubLocationIndex + 1);
  }
  
  private boolean loadPrevSubLocation()
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return false;
    return loadSubLocation(mCurrentSubLocationIndex - 1);
  }
  
  private void makePin(PointF P)
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;

    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    if (subLoc == null)
      return;

    if (P.x < 0.0f || P.x > subLoc.getWidth() ||
        P.y < 0.0f || P.y > subLoc.getHeight())
    {
      // Missing the map
      return;
    }

    if (mTargetPoint != null || mTargetVenue != null)
      return;
    
    if (mDeviceInfo == null || !mDeviceInfo.isValid())
      return;

    mPinPoint = new LocationPoint(mLocation.getId(), subLoc.getId(), P.x, P.y);
    mPinPointRect = new RectF();
    mLocationView.redraw();
  }

  private void cancelPin()
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;

    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    if (subLoc == null)
      return;

    if (mTargetPoint != null || mTargetVenue != null || mPinPoint == null)
      return;

    mPinPoint = null;
    mPinPointRect = null;
    mLocationView.redraw();
  }
  
  private void cancelVenue()
  {
    mSelectedVenue = null;
    mLocationView.redraw();
  }
  
  private Venue getVenueAt(float x, float y)
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return null;

    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    if (subLoc == null)
      return null;

    Venue v0 = null;
    float d0 = 1000.0f;
        
    for(int i = 0; i < subLoc.getVenues().size(); ++i)
    {
      Venue v = subLoc.getVenues().get(i);
      PointF P = mLocationView.getScreenCoordinates(v.getX(), v.getY());
      float d = Math.abs(x - P.x) + Math.abs(y - P.y);
      if (d < 30.0f * mDisplayDensity && d < d0)
      {
        v0 = v;
        d0 = d;
      }
    }
    
    return v0;
  }
  
  private Zone getZoneAt(float x, float y)
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return null;

    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    if (subLoc == null)
      return null;
    
    PointF P = mLocationView.getAbsCoordinates(x, y);
    LocationPoint LP = new LocationPoint(mLocation.getId(), subLoc.getId(), P.x, P.y);
    
    for(int i = 0; i < subLoc.getZones().size(); ++i)
    {
      Zone Z = subLoc.getZones().get(i);
      if (Z.contains(LP))
        return Z;
    }
    return null;
  }
  
  private void drawPoints(Canvas canvas)
  {
    // Check if location is loaded
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;

    // Get current sublocation displayed
    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);

    if (subLoc == null)
      return;

    final int solidColor  = Color.argb(255, 64, 163, 205);  // Light-blue color
    final int circleColor = Color.argb(127, 64, 163, 205);  // Semi-transparent light-blue color
    final int arrowColor  = Color.argb(255, 255, 255, 255); // White color
    final float dp        = mDisplayDensity;
    final float textSize  = 16 * dp;
    
    // Preparing paints
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    paint.setTextSize(textSize);
    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    // Drawing pin point (if it exists and belongs to the current sublocation)
    if (mPinPoint != null && mPinPoint.subLocation == subLoc.getId())
    {
      final PointF T = mLocationView.getScreenCoordinates(mPinPoint);
      final float tRadius = 10 * dp;

      paint.setARGB(255, 0, 0, 0);
      paint.setStrokeWidth(4 * dp);
      canvas.drawLine(T.x, T.y, T.x, T.y - 3 * tRadius, paint);

      paint.setColor(solidColor);
      paint.setStrokeWidth(0);
      canvas.drawCircle(T.x, T.y - 3 * tRadius, tRadius, paint);
      
      final String text = "Make route";
      final float textWidth = paint.measureText(text);
      final float h  = 50 * dp;
      final float w  = Math.max(120 * dp, textWidth + h/2);
      final float x0 = T.x;
      final float y0 = T.y - 75 * dp;
      
      mPinPointRect.set(x0 - w/2, y0 - h/2, x0 + w/2, y0 + h/2);
      
      paint.setColor(solidColor);
      canvas.drawRoundRect(mPinPointRect, h/2, h/2, paint);
      
      paint.setARGB(255, 255, 255, 255);
      canvas.drawText(text, x0 - textWidth/2, y0 + textSize/4, paint);
    }
    
    // Drawing target point (if it exists and belongs to the current sublocation)
    if (mTargetPoint != null && mTargetPoint.subLocation == subLoc.getId())
    {
      final PointF T = mLocationView.getScreenCoordinates(mTargetPoint);
      final float tRadius = 10 * dp;

      paint.setARGB(255, 0, 0, 0);
      paint.setStrokeWidth(4 * dp);
      canvas.drawLine(T.x, T.y, T.x, T.y - 3 * tRadius, paint);

      paint.setColor(solidColor);
      canvas.drawCircle(T.x, T.y - 3 * tRadius, tRadius, paint);
    }
  }

  private void drawVenues(Canvas canvas)
  {
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;
    
    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    
    final float dp = mDisplayDensity;
    final float textSize  = 16 * dp;
    final float venueSize = 30 * dp;
    final int   venueColor = Color.argb(255, 0xCD, 0x88, 0x50); // Venue color
    
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    paint.setStrokeWidth(0);
    paint.setColor(venueColor);
    paint.setTextSize(textSize);
    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    
    for(int i = 0; i < subLoc.getVenues().size(); ++i)
    {
      Venue v = subLoc.getVenues().get(i);
      if (v.getSubLocationId() != subLoc.getId())
        continue;
      
      final PointF P = mLocationView.getScreenCoordinates(v.getX(), v.getY());
      final float x0 = P.x - venueSize/2;
      final float y0 = P.y - venueSize/2;
      final float x1 = P.x + venueSize/2;
      final float y1 = P.y + venueSize/2;
      canvas.drawBitmap(mVenueBitmap, null, new RectF(x0, y0, x1, y1), paint);
    }
    
    if (mSelectedVenue != null)
    {
      final PointF T = mLocationView.getScreenCoordinates(mSelectedVenue.getX(), mSelectedVenue.getY());
      final float textWidth = paint.measureText(mSelectedVenue.getName());
      
      final float h  = 50 * dp;
      final float w  = Math.max(120 * dp, textWidth + h/2);
      final float x0 = T.x;
      final float y0 = T.y - 50 * dp;
      mSelectedVenueRect.set(x0 - w/2, y0 - h/2, x0 + w/2, y0 + h/2);
      
      paint.setColor(venueColor);
      canvas.drawRoundRect(mSelectedVenueRect, h/2, h/2, paint);
      
      paint.setARGB(255, 255, 255, 255);
      canvas.drawText(mSelectedVenue.getName(), x0 - textWidth/2, y0 + textSize/4, paint);
    }
  }
  
  private void drawZones(Canvas canvas)
  {
    // Check if location is loaded
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;
    
    // Get current sublocation displayed
    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
    if (subLoc == null)
      return;
    
    // Preparing paints
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    
    for(int i = 0; i < subLoc.getZones().size(); ++i)
    {
      Zone Z = subLoc.getZones().get(i);
      if (Z.getPoints().size() < 3)
        continue;
      
      boolean selected = (Z == mSelectedZone);
      
      Path path = new Path();
      final LocationPoint P0 = Z.getPoints().get(0);
      final PointF        Q0 = mLocationView.getScreenCoordinates(P0);
      path.moveTo(Q0.x, Q0.y);
      
      for(int j = 0; j < Z.getPoints().size(); ++j)
      {
        final LocationPoint P = Z.getPoints().get((j + 1) % Z.getPoints().size());
        final PointF        Q = mLocationView.getScreenCoordinates(P);
        path.lineTo(Q.x, Q.y);
      }
      
      int zoneColor = Color.parseColor(Z.getColor());
      int red       = (zoneColor >> 16) & 0xff;
      int green     = (zoneColor >> 8 ) & 0xff;
      int blue      = (zoneColor >> 0 ) & 0xff;
      paint.setColor(Color.argb(selected ? 200 : 100, red, green, blue));
      canvas.drawPath(path, paint);
    }
  }
  
  private void drawDevice(Canvas canvas)
  {
    // Check if location is loaded
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;
    
    // Check if navigation is available
    if (mDeviceInfo == null || !mDeviceInfo.isValid())
      return;

    // Get current sublocation displayed
    SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);

    if (subLoc == null)
      return;

    final int solidColor  = Color.argb(255, 64,  163, 205); // Light-blue color
    final int circleColor = Color.argb(127, 64,  163, 205); // Semi-transparent light-blue color
    final int arrowColor  = Color.argb(255, 255, 255, 255); // White color
    final float dp = mDisplayDensity;
    
    // Preparing paints
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);
    paint.setStrokeCap(Paint.Cap.ROUND);

    /// Drawing device path (if it exists)
    if (mDeviceInfo.getPaths() != null && mDeviceInfo.getPaths().size() > 0)
    {
      RoutePath path = mDeviceInfo.getPaths().get(0);
      if (path.getPoints().size() >= 2)
      {
        paint.setColor(solidColor);

        for(int j = 1; j < path.getPoints().size(); ++j)
        {
          LocationPoint P = path.getPoints().get(j-1);
          LocationPoint Q = path.getPoints().get(j);
          if (P.subLocation == subLoc.getId() && Q.subLocation == subLoc.getId())
          {
            paint.setStrokeWidth(3 * dp);
            PointF P1 = mLocationView.getScreenCoordinates(P);
            PointF Q1 = mLocationView.getScreenCoordinates(Q);
            canvas.drawLine(P1.x, P1.y, Q1.x, Q1.y, paint);
          }
        }
      }
    }
    
    paint.setStrokeCap(Paint.Cap.BUTT);

    // Check if device belongs to the current sublocation
    if (mDeviceInfo.getSubLocationId() != subLoc.getId())
      return;

    final float x  = mDeviceInfo.getX();
    final float y  = mDeviceInfo.getY();
    final float r  = mDeviceInfo.getR();
    final float angle = mDeviceInfo.getAzimuth();
    final float sinA = (float)Math.sin(angle);
    final float cosA = (float)Math.cos(angle);
    final float radius  = mLocationView.getScreenLengthX(r);  // External radius: navigation-determined, transparent
    final float radius1 = 25 * dp;                            // Internal radius: fixed, solid

    PointF O = mLocationView.getScreenCoordinates(x, y);
    PointF P = new PointF(O.x - radius1 * sinA * 0.22f, O.y + radius1 * cosA * 0.22f);
    PointF Q = new PointF(O.x + radius1 * sinA * 0.55f, O.y - radius1 * cosA * 0.55f);
    PointF R = new PointF(O.x + radius1 * cosA * 0.44f - radius1 * sinA * 0.55f, O.y + radius1 * sinA * 0.44f + radius1 * cosA * 0.55f);
    PointF S = new PointF(O.x - radius1 * cosA * 0.44f - radius1 * sinA * 0.55f, O.y - radius1 * sinA * 0.44f + radius1 * cosA * 0.55f);

    // Drawing transparent circle
    paint.setStrokeWidth(0);
    paint.setColor(circleColor);
    canvas.drawCircle(O.x, O.y, radius, paint);

    // Drawing solid circle
    paint.setColor(solidColor);
    canvas.drawCircle(O.x, O.y, radius1, paint);

    if (ORIENTATION_ENABLED)
    {
      // Drawing arrow
      paint.setColor(arrowColor);
      Path path = new Path();
      path.moveTo(Q.x, Q.y);
      path.lineTo(R.x, R.y);
      path.lineTo(P.x, P.y);
      path.lineTo(S.x, S.y);
      path.lineTo(Q.x, Q.y);
      canvas.drawPath(path, paint);
    }
  }

  private void adjustDevice()
  {
    // Check if location is loaded
    if (mLocation == null || mCurrentSubLocationIndex < 0)
      return;

    // Check if navigation is available
    if (mDeviceInfo == null || !mDeviceInfo.isValid())
      return;

    long timeNow = System.currentTimeMillis();

    // Adjust map, if necessary
    if (timeNow >= mAdjustTime)
    {
      // Firstly, set the correct sublocation
      SubLocation subLoc = mLocation.getSubLocations().get(mCurrentSubLocationIndex);
      if (mDeviceInfo.getSubLocationId() != subLoc.getId())
      {
        for(int i = 0; i < mLocation.getSubLocations().size(); ++i)
          if (mLocation.getSubLocations().get(i).getId() == mDeviceInfo.getSubLocationId())
            loadSubLocation(i);
      }
      
      // Secondly, adjust device to the center of the screen
      PointF center = mLocationView.getScreenCoordinates(mDeviceInfo.getX(), mDeviceInfo.getY());
      float deltaX  = mLocationView.getWidth()  / 2 - center.x;
      float deltaY  = mLocationView.getHeight() / 2 - center.y;
      mAdjustTime   = timeNow;
      mLocationView.scrollBy(deltaX, deltaY);
    }
  }

  private void showDirections(RoutePath path)
  {
    switch (path.getEvents().get(0).getType())
    {
      case RouteEvent.TURN_LEFT:
        mDirectionImageView.setBackgroundResource(R.drawable.ic_left);
        break;
      case RouteEvent.TURN_RIGHT:
        mDirectionImageView.setBackgroundResource(R.drawable.ic_right);
        break;
      case RouteEvent.TRANSITION:
        mDirectionImageView.setBackgroundResource(R.drawable.ic_escalator);
        break;
    }
    float nextTurnDistance = Math.max(path.getEvents().get(0).getDistance(), 1);
    mDirectionTextView.setText(String.format(Locale.ENGLISH,"%.0f m", nextTurnDistance));
    mDirectionLayout.setVisibility(View.VISIBLE);
  }
  
  private String getLogFile(String extension)
  {
    try
    {
      final String extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/Navigine.Demo";
      (new File(extDir)).mkdirs();
      if (!(new File(extDir)).exists())
        return null;
      
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      
      return String.format(Locale.ENGLISH, "%s/%04d%02d%02d_%02d%02d%02d.%s", extDir,
                           calendar.get(Calendar.YEAR),
                           calendar.get(Calendar.MONTH) + 1,
                           calendar.get(Calendar.DAY_OF_MONTH),
                           calendar.get(Calendar.HOUR_OF_DAY),
                           calendar.get(Calendar.MINUTE),
                           calendar.get(Calendar.SECOND),
                           extension);
    }
    catch (Throwable e)
    {
      return null;
    }
  }
}
