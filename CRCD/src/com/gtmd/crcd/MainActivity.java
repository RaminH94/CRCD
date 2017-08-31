/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;

import com.example.continuedl.R;
import com.gtmd.crcd.begin.AES_encryption;
import com.gtmd.crcd.begin.ApManager;
import com.gtmd.crcd.begin.Client;
public class MainActivity extends Activity {
	private static final int REQUEST_SCAN_ALWAYS_AVAILABLE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/**************		 shared variable setting		****************/
		SharedVariable.timerHandler = new Handler();
		SharedVariable.aes = new AES_encryption();
		SharedVariable.videofile_path = Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.video_name;
		//SharedVariable.video_lastseekpoint = 0;
		SharedVariable.videofile = new File(SharedVariable.videofile_path) ;
		SharedVariable.videofile_uri = Uri.parse(SharedVariable.videofile_path);
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		SharedVariable.IMEI = mngr.getDeviceId();
		/*************		create video file		***********/
		try{
			FileOutputStream fos=(SharedVariable.videofile.length()==0)? new FileOutputStream(SharedVariable.videofile_path): new FileOutputStream(SharedVariable.videofile_path,true);

			//file.mkdirs();
			//SharedVariable.videofile.createNewFile();
		} 
		catch(Exception e )
		{}

		try{
			if(ApManager.isApOn(this)) // check Ap state :boolean
				ApManager.turnOffAP(this); // change Ap state :boolean
		}
		catch(Exception e){}

		try{
			WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);

		}
		catch(Exception e){}
		try{
			SharedVariable.timerRunnable = new Runnable() {
				@Override
				public void run() {
					SharedVariable.timerHandler.postDelayed(this, SharedVariable.postdelayed_time);
					SharedVariable.videofile_length = SharedVariable.videofile.length();
					if(SharedVariable.videofile_length - SharedVariable.videofile_lastlength 
							> SharedVariable.videofile_changed_size)
					{
						SharedVariable.timerHandler.removeCallbacks(SharedVariable.timerRunnable);
						SharedVariable.mc.setAnchorView(SharedVariable.vd);   
						SharedVariable.vd.setMediaController(SharedVariable.mc);

						SharedVariable.vd.setVideoURI(SharedVariable.videofile_uri);
						SharedVariable.vd.requestFocus();

						SharedVariable.vd.seekTo(SharedVariable.video_lastseekpoint);
						try{
							SharedVariable.vd.start();
							SharedVariable.video_isstart=1;
							SharedVariable.videofile_lastlength = SharedVariable.videofile_length;
							SharedVariable.video_starttime = System.currentTimeMillis();
					/*		try{
								Client.mProgressDialog.dismiss();
							}
							catch(Exception e){
								
							}*/
						}
						catch(Exception e)
						{
							
						//	e.printStackTrace();
						}
						SharedVariable.vd.resume();
						SharedVariable.video_isstart=1;
						SharedVariable.videofile_lastlength = SharedVariable.videofile_length;
						SharedVariable.video_starttime = System.currentTimeMillis();
					}
				}
			};
		}
		catch(Exception e ){
			
		//	e.printStackTrace();
		}

		/*******************************************************************/
		
		Button btn_server=(Button) findViewById(R.id.btn_server);
		btn_server.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedVariable.is_server = true;
				SharedVariable.is_server_neg = true;
				
				startActivity(new Intent(getBaseContext(), session_features.class));
			}
		});
		Button btn_client=(Button) findViewById(R.id.btn_client);
		btn_client.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedVariable.is_server = false;
				SharedVariable.is_server_neg = false;
				startActivity(new Intent(getBaseContext(), com.gtmd.crcd.begin.MainActivity.class));
			}
		});

		
			}

	public static void videoview_setting(Context ctx){
		SharedVariable.mc = new MediaController(ctx);
		SharedVariable.mc.setAnchorView(SharedVariable.vd);   
		SharedVariable.vd.setMediaController(SharedVariable.mc);
		SharedVariable.vd.setVideoURI(SharedVariable.videofile_uri);

		SharedVariable.vd.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
				if (i == MediaPlayer.MEDIA_ERROR_SERVER_DIED || i == MediaPlayer.MEDIA_ERROR_IO || i == MediaPlayer.MEDIA_ERROR_UNKNOWN)
				{

					SharedVariable.video_stoptime = System.currentTimeMillis();
					int tmp = (int)(SharedVariable.video_stoptime-SharedVariable.video_starttime);
					if(SharedVariable.video_starttime>0)
						SharedVariable.video_lastseekpoint += tmp;
					SharedVariable.videofile_length = SharedVariable.videofile.length();
					SharedVariable.timerHandler.postDelayed(SharedVariable.timerRunnable, SharedVariable.postdelayed_time);
				}
				return true;
			}
		});


		SharedVariable.vd.setOnPreparedListener(new OnPreparedListener()
		{

			public void onPrepared(MediaPlayer mp)
			{                  
			}
		});   

		SharedVariable.vd.requestFocus();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		try{
			SharedVariable.socketServerThreadneg.interrupt();
			
			SharedVariable.serverSocketNeg.close();
			}
		catch(Exception e){
		}
		try{
			SharedVariable.serverSocket.close();
			SharedVariable.socketServerThread.interrupt();
		}
		catch(Exception e){
			
		}

		
		
		try{		
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (Build.VERSION.SDK_INT >= 18 && !wifiManager.isScanAlwaysAvailable()) {
				  startActivityForResult(new Intent(WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE) ,REQUEST_SCAN_ALWAYS_AVAILABLE);
				}
		}
		catch(Exception e){}
			
	}
	
}
