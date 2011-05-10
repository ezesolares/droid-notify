package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class PhoneReceiver extends BroadcastReceiver{

	//================================================================================
    // Properties
    //================================================================================

	//================================================================================
	// Constructors
	//================================================================================
		
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		if (Log.getDebug()) Log.v("PhoneReceiver.onReceive()");
		//Schedule phone task 5 seconds after the broadcast.
		//This should allow enough time to pass for the phone log to be written to.
		//TODO - Create a user advanced preference to tailor this timeout period before checking the call log.
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent newIntent = new Intent(context, PhoneAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), pendingIntent);		
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
