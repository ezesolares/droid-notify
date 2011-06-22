package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

/**
 * This class listens for scheduled MMS notifications that we want to display.
 * 
 * @author Camille S�vigny
 */
public class MMSAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	private static final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	
	//================================================================================
    // Properties
    //================================================================================

	//================================================================================
	// Constructors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives the incomming MMS message. The MMS message is located within the Intent object.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("MMSAlarmReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("MMSAlarmReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if MMS notifications are disabled.
	    if(!preferences.getBoolean(MMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("MMSAlarmReceiver.onReceive() MMS Notifications Disabled. Exiting...");
			return;
		}
	  //Check the state of the users phone.
	    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    // If the user is not in a call then start out work. 
	    if(callStateIdle){
			WakefulIntentService.acquireStaticLock(context);
			Intent mmsIntent = new Intent(context, MMSReceiverService.class);
			mmsIntent.putExtras(intent.getExtras());
			context.startService(mmsIntent);
	    }else{
	    	// Set alarm to go off x minutes from the current time as defined by the user preferences.
	    	long rescheduleInterval = Long.parseLong(preferences.getString(RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
	    	if(rescheduleInterval > 0){
		    	if (Log.getDebug()) Log.v("MMSReceiver.onReceive() Phone Call In Progress. Rescheduling notification.");
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent mmsIntent = new Intent(context, MMSAlarmReceiver.class);
				mmsIntent.putExtras(intent.getExtras());
				mmsIntent.setAction("apps.droidnotify.VIEW/MMSReschedule/" + System.currentTimeMillis());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mmsIntent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, pendingIntent);
	    	}
	    }
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
}