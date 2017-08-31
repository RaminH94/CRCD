/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.continuedl.R;
import com.gtmd.crcd.SharedVariable;

public class MainActivity extends Activity {
	static public WifiConfiguration conf ;
	static public WifiManager wifiManager;
	static Server server;
	static ServerNeg serverneg;
	private static Client myClient;
	private static ClientNeg myclientneg;
	public static Context ctx;
	public static Activity myact;
	public static Handler move_client_handler ;
	public static Runnable move_client_runnable;

	public static Handler goto_client_handler,goto_server_handler,goto_server_handler_checktime,goto_client_handler_stopdl ;
	public static Runnable goto_client_runnable,goto_server_runnable,goto_server_runnable_checktime,goto_client_runnable_stopdl;
	public static Handler goto_client_handlerNeg,goto_server_handlerNeg ;
	public static Runnable goto_client_runnableNeg,goto_server_runnableNeg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		ctx = this;
		SharedVariable.vd = (VideoView)findViewById(R.id.videoView1);
		com.gtmd.crcd.MainActivity.videoview_setting(this);
		//SharedVariable.mc = new MediaController(this);

		myact = this;
		try{
			throughput_Calc();
			//SharedVariable.throughput_Thread.join();
		}
		catch(Exception e){

		}

		try{
			server_client_neg_setting();
		}
		catch(Exception e){}

		SharedVariable.Throughput_Handler_neg.postDelayed(SharedVariable.Throughput_Runnable_neg, 400);

	}


	public void client_wifi_setting(){
		WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
		//wifiManager.setWifiEnabled(false);
		if(!wifiManager.isWifiEnabled())
		{
			if(ApManager.isApOn(MainActivity.ctx)) // check Ap state :boolean
				ApManager.turnOffAP(MainActivity.ctx); // change Ap state :boolean
			wifiManager.setWifiEnabled(true);          
		}   



	}
	public void client_setting(){
		client_wifi_setting();
		//connect();
		SharedVariable.client_ssid_check_handler= new Handler();
		SharedVariable.client_wifi_connected_check_handler = new Handler();
		try{
			//			WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
			//		WifiInfo info = wifiManager.getConnectionInfo ();
			//	String a = getCurrentSsid(this);


			try{
				myClient.cancel(true);

			}
			catch(Exception e){}

			myClient = new Client(SharedVariable.client_dest_ip
					, SharedVariable.client_port);

			SharedVariable.client_ssid_check_runnable = new Runnable() {
				@Override
				public void run() {


					SharedVariable.client_ssid_check_handler.postDelayed(this, 2000);

					try{
						client_wifi_setting();
						if(SharedVariable.server_ssid_name.equalsIgnoreCase(getCurrentSsid(ctx)) || SharedVariable.client_ssid_name.equalsIgnoreCase(getCurrentSsid(ctx)))
						{
							//Toast.makeText(this, "hihihihi", Toast.LENGTH_LONG).show():
							SharedVariable.client_ssid_check_handler.removeCallbacks(SharedVariable.client_ssid_check_runnable);
							SharedVariable.client_wifi_connected_check_handler.removeCallbacks(SharedVariable.client_wifi_connected_check_runnable);
							myClient.execute();


						}
					}
					catch(Exception e){}

				}
			};

		}
		catch(Exception e)
		{

		}

		SharedVariable.client_ssid_check_handler.postDelayed(SharedVariable.client_ssid_check_runnable, 3000);


		try{

			SharedVariable.client_wifi_connected_check_runnable = new Runnable() {
				@Override
				public void run() {

					try{
						connect(true);
					}
					catch(Exception e)
					{

					}

					SharedVariable.client_wifi_connected_check_handler.postDelayed(SharedVariable.client_wifi_connected_check_runnable, 7000);




				}
			};


		}
		catch(Exception e)
		{


		}

		SharedVariable.client_wifi_connected_check_handler.postDelayed(SharedVariable.client_wifi_connected_check_runnable, 4000);


	}
	public void server_wifi_setting(){

		WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(false);          
		}

		WifiConfiguration wifiCon = new WifiConfiguration();
		wifiCon.SSID= SharedVariable.server_ssid_name;
		wifiCon.preSharedKey = SharedVariable.pass;
		wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		try
		{
			Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, wifiCon,true);
		} 
		catch (Exception e) 
		{
			//Log.e(this.getClass().toString(), "", e);
			e.printStackTrace();
		}

	}
	public void server_wifi_setting_neg(){

		WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
		if(wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(false);          
		}

		WifiConfiguration wifiCon = new WifiConfiguration();
		wifiCon.SSID= SharedVariable.server_ssid_name;
		wifiCon.preSharedKey = SharedVariable.pass;
		wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		try
		{
			Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, wifiCon,true);
		} 
		catch (Exception e) 
		{
			//Log.e(this.getClass().toString(), "", e);
			e.printStackTrace();
		}
	}

	public void server_setting(){
		server_wifi_setting();
		server = new Server();



	}

	@Override
	public void onPause() {
		super.onPause();
		//SharedVariable.vd.suspend();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		//SharedVariable.vd.resume();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//SharedVariable.vd.stopPlayback();
	}

	public void connect(boolean with_pass){

		conf = new WifiConfiguration();
		conf.SSID  =SharedVariable.client_ssid_name;
		conf.preSharedKey = "\"" + SharedVariable.pass + "\"";
		conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		conf.status = WifiConfiguration.Status.ENABLED;
		conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		conf.allowedPairwiseCiphers
		.set(WifiConfiguration.PairwiseCipher.TKIP);
		conf.allowedPairwiseCiphers
		.set(WifiConfiguration.PairwiseCipher.CCMP);
		conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);

		wifiManager.addNetwork(conf);
		DhcpInfo dhcp;




		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();



		for( WifiConfiguration i : list ) {
			if(i.SSID != null && i.SSID.equals(SharedVariable.client_ssid_name)) {
				try {
					wifiManager.disconnect();
					wifiManager.enableNetwork(i.networkId, true);
					//System.out.print("i.networkId " + i.networkId + "\n");
					wifiManager.reconnect();               
					break;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}           
		}




	}

	public String intToIp(int addr) {
		return  ((addr & 0xFF) + "." + 
				((addr >>>= 8) & 0xFF) + "." + 
				((addr >>>= 8) & 0xFF) + "." + 
				((addr >>>= 8) & 0xFF));
	}

	public String getCurrentSsid(Context context) {

		String ssid = null;
		try{

			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (networkInfo.isConnected()) {
				final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
				if (connectionInfo != null && !connectionInfo.getSSID().equalsIgnoreCase("")) {
					ssid = connectionInfo.getSSID();
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ssid;
	}

	public void client2client(){

		move_client_handler= new Handler();
		try{

			move_client_runnable = new Runnable() {
				public void run() {
					move_client_handler.postDelayed(move_client_runnable, 100);
					try{

						if( SharedVariable.socket.isClosed() )
						{

							try{
								SharedVariable.socket=null;
								//a;

								client_wifi_setting();

							}
							catch(Exception e){

							}
							move_client_handler.removeCallbacks(move_client_runnable);

							switch_server_client();			

						}
					}
					catch(Exception e ){}
					/*//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					move_clientneg_handler.postDelayed(move_clientneg_runnable, 500);
					try{
						if( SharedVariable.neg_end ||SharedVariable.socketNeg.isClosed()){
							//SharedVariable.socket==null ||
							//aa;
							myclientneg.cancel(true);
							client_setting();
							move_clientneg_handler.removeCallbacks(move_clientneg_runnable);
							goto_server_setting();
							goto_server_handler.postDelayed(MainActivity.goto_server_runnable, 500);

						}
					}
					catch(Exception e){

					}*/
				}
			};


		}
		catch(Exception e)
		{

		}


	}

	public void goto_server_setting(){

		goto_server_handler= new Handler();
		try{

			goto_server_runnable = new Runnable() {
				public void run() {

					//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					goto_server_handler.postDelayed(goto_server_runnable, 100);
					try{
						if( SharedVariable.socket.isClosed()){
							//SharedVariable.socket==null ||
							//aa;
							myClient.cancel(true);

							goto_server_handler.removeCallbacks(goto_server_runnable);

							switch_server_client();

						}
					}
					catch(Exception e){

					}
				}
			};


		}
		catch(Exception e)
		{

		}
 

	}
	public void goto_client_setting(){


		goto_client_handler = new Handler();
		try{


			goto_client_runnable = new Runnable() {
				public void run() {
					goto_client_handler.postDelayed(goto_client_runnable, 100);
					try{

						if( SharedVariable.is_server ){
							try{

								SharedVariable.socketServerThread.interrupt();
							}

							//SharedVariable.socketServerThread.
							catch(Exception e){}

							goto_client_handler.removeCallbacks(goto_client_runnable);

							switch_server_client();			

						}
					}
					catch(Exception e ){}
				}
			};
			

		}
		catch(Exception e)
		{

		}
		/*******************		stop dl *********************/
		goto_client_handler_stopdl = new Handler();
		try{


			goto_client_runnable_stopdl = new Runnable() {
				public void run() {
					goto_client_handler_stopdl.postDelayed(goto_client_runnable_stopdl, 100);
					try{

						if( SharedVariable.is_server ){
							try{

								SharedVariable.stop_dl_server = true;
							}

							//SharedVariable.socketServerThread.
							catch(Exception e){}

							goto_client_handler_stopdl.removeCallbacks(goto_client_runnable_stopdl);

							//	switch_server_client();			

						}
					}
					catch(Exception e ){}
				}
			};


		}
		catch(Exception e)
		{

		}

		return;
	}

	public void client_setting_neg(){



		client_wifi_setting();
		//connect();
		SharedVariable.client_ssid_check_handler_neg= new Handler();
		SharedVariable.client_wifi_connected_check_handler_neg = new Handler();
		try{
			//			WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
			//		WifiInfo info = wifiManager.getConnectionInfo ();
			//	String a = getCurrentSsid(this);

			myclientneg = new ClientNeg(SharedVariable.client_dest_ip
					, SharedVariable.client_port_neg);

			SharedVariable.client_ssid_check_runnable_neg = new Runnable() {
				@Override
				public void run() {


					SharedVariable.client_ssid_check_handler_neg.postDelayed(this, 2000);

					try{
						client_wifi_setting();
						if(SharedVariable.server_ssid_name.equalsIgnoreCase(getCurrentSsid(ctx)) || SharedVariable.client_ssid_name.equalsIgnoreCase(getCurrentSsid(ctx)))
						{
							//Toast.makeText(this, "hihihihi", Toast.LENGTH_LONG).show():
							SharedVariable.client_ssid_check_handler_neg.removeCallbacks(SharedVariable.client_ssid_check_runnable_neg);
							SharedVariable.client_wifi_connected_check_handler_neg.removeCallbacks(SharedVariable.client_wifi_connected_check_runnable_neg);
							myclientneg.execute();	


						}
					}
					catch(Exception e){}

				}
			};


		}
		catch(Exception e)
		{

		}

		SharedVariable.client_ssid_check_handler_neg.postDelayed(SharedVariable.client_ssid_check_runnable_neg, 2000);


		try{

			SharedVariable.client_wifi_connected_check_runnable_neg = new Runnable() {
				@Override
				public void run() {

					try{
						connect(false);
					}
					catch(Exception e)
					{

					}

					SharedVariable.client_wifi_connected_check_handler_neg.postDelayed(SharedVariable.client_wifi_connected_check_runnable_neg, 7000);




				}
			};


		}
		catch(Exception e)
		{

		}

		SharedVariable.client_wifi_connected_check_handler_neg.postDelayed(SharedVariable.client_wifi_connected_check_runnable_neg, 4000);


	}


	public void server_setting_neg(){

		try{
			server_wifi_setting_neg();		//server = new Server(this);
			serverneg = new ServerNeg(this);
		}
		catch(Exception e){}


	}


	public void goto_client_setting_looking(){


		goto_client_handlerNeg = new Handler();
		try{

			goto_client_runnableNeg = new Runnable() {
				public void run() {



					goto_client_handlerNeg.postDelayed(goto_client_runnableNeg, 100);
					try{
						if(SharedVariable.neg_end){

							goto_client_handlerNeg.removeCallbacks(goto_client_runnableNeg);
							SharedVariable.socketServerThreadneg.interrupt();

							switch_server_client();


						}
					}
					catch(Exception e){

					}

				}
			};


		}
		catch(Exception e)
		{

		}


	}

	public void goto_server_setting_looking(){

		goto_server_handlerNeg= new Handler();
		try{

			goto_server_runnableNeg = new Runnable() {
				public void run() {

					//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					goto_server_handlerNeg.postDelayed(goto_server_runnableNeg, 100);
					try{
						if(SharedVariable.neg_end){

							goto_server_handlerNeg.removeCallbacks(goto_server_runnableNeg);
							myclientneg.cancel(true);


							switch_server_client();

							/*	client_setting();
							goto_server_setting();
							goto_server_handler.postDelayed(MainActivity.goto_server_runnable, 500);
							 */

						}
					}
					catch(Exception e){

					}
				}
			};


		}
		catch(Exception e)
		{

		}


	}

	public void switch_server_client(){
		try{
			SharedVariable.connection_stopTime=System.currentTimeMillis();
			if(SharedVariable.is_server_neg)
			Client.cheating_check(SharedVariable.total_turned);
		}
		catch(Exception e){}

		/*try{
			myClient.mProgressDialog.dismiss();
		}
		catch(Exception e){}
*/

		try{
			SharedVariable.socketServerThreadneg.interrupt();


			//SharedVariable.serverSocketNeg.close();
		}
		catch(Exception e){
		}
		try{	SharedVariable.serverSocketNeg.close();
		}
		catch(Exception e){
		}
		try{
			SharedVariable.serverSocket.close();

		}
		catch(Exception e){

		}
		try{	
			SharedVariable.socketServerThread.interrupt();
		}
		catch(Exception e){
		}
		try{

			myClient.cancel(true);
		}
		catch(Exception e){}
		try{
			myclientneg.cancel(true);
		}
		catch(Exception e){}


		try{
			if(!SharedVariable.socket.isClosed())
				SharedVariable.socket.close();
			SharedVariable.socket=null;

		}
		catch(Exception e){}
		try{
			if(!SharedVariable.socketNeg.isClosed())
				SharedVariable.socketNeg.close();
			SharedVariable.socketNeg=null;

		}
		catch(Exception e){}

		SharedVariable.connection_stopTime = SharedVariable.connection_startTime = 0 ;
		SharedVariable.client_ssid_name = "\"\"" + SharedVariable.ssid_name+SharedVariable.total_turned + "\"\"";
		SharedVariable.server_ssid_name = "\""+SharedVariable.ssid_name+SharedVariable.total_turned+"\"";

		Toast.makeText(MainActivity.ctx, String.valueOf(SharedVariable.myTurn), Toast.LENGTH_SHORT).show();
		SharedVariable.total_turned++;
		try{
			
			if(SharedVariable.total_turned-1== (SharedVariable.total_client_cnt+2)){
				SharedVariable.total_turned = 1;
				SharedVariable.neg_end= false;
				SharedVariable.Throughout_Ended = false;
				try{
					throughput_Calc();
					//			SharedVariable.throughput_Thread.join();
				}
				catch(Exception e){

				}
				try{
					server_client_neg_setting_2();
				}
				catch(Exception e){}
				SharedVariable.Throughput_Handler_neg.postDelayed(SharedVariable.Throughput_Runnable_neg, 400);
			}


			//if(share)
			else if(SharedVariable.total_turned-1 == SharedVariable.myTurn){
				try{
					WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
					wifiManager.setWifiEnabled(false);

				}
				catch(Exception e){}
				try{
					if(ApManager.isApOn(MainActivity.ctx)) // check Ap state :boolean
						ApManager.turnOffAP(MainActivity.ctx); // change Ap state :boolean
				}
				catch(Exception e){}

				SharedVariable.is_server=true;
				goto_client_setting();
				server_setting();
				
				//goto_client_handler.postDelayed(MainActivity.goto_client_runnable, 25000);
			}
			else
			{
				try{
					WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
					wifiManager.setWifiEnabled(false);

				}
				catch(Exception e){}
				try{
					client_wifi_setting();
				}
				catch(Exception e){}

				client_setting();
				SharedVariable.is_server=false;

				if((SharedVariable.total_turned) == SharedVariable.myTurn){
					goto_server_setting();
					goto_server_setting_check_time();
					goto_server_handler.postDelayed(MainActivity.goto_server_runnable, SharedVariable.times_to_be_server[SharedVariable.total_turned-2]);

				}
				else{
					goto_server_setting_check_time();
					client2client();
					move_client_handler.postDelayed(move_client_runnable,SharedVariable.times_to_be_server[SharedVariable.total_turned-2]);

				}
			}

		}
		catch(Exception e){}


	}













	public void goto_server_setting_check_time(){

		goto_server_handler_checktime= new Handler();
		try{

			goto_server_runnable_checktime = new Runnable() {
				public void run() {

					//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					//goto_server_handler_checktime.postDelayed(goto_server_runnable_checktime, 100);
					try{
						/*SharedVariable.connection_stopTime = System.currentTimeMillis();
						Client.cheating_check(SharedVariable.total_turned);*/
						SharedVariable.socket.close();

					}
					catch(Exception e){

					}
				}
			};


		}
		catch(Exception e)
		{

		}


	}


	public void throughput_Calc(){

		SharedVariable.Throughput_Handler= new Handler();
		try{

			SharedVariable.Throughput_Runnable = new Runnable() {
				public void run() {

					//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					//goto_server_handler_checktime.postDelayed(goto_server_runnable_checktime, 100);
					try{
						//SharedVariable.socket.close();
						try{
							SharedVariable.throughput_Thread.interrupt();
							SharedVariable.connection1.disconnect();
							SharedVariable.throughput_Thread.stop();
						}
						catch(Exception e){

						}

						SharedVariable.Throughout_Ended = true;
						SharedVariable.Throughput_Downloaded =(int)(SharedVariable.Throughput_Tmp_Downloaded/SharedVariable.Throughput_Diff_Sec);
						SharedVariable.myCondition = SharedVariable.Throughput_Downloaded;

					}
					catch(Exception e){

					}
				}
			};


		}
		catch(Exception e)
		{

		}



		try{
			WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);

		}
		catch(Exception e){}

		SharedVariable.throughput_Thread = new Thread() {
			@Override
			public void run() {
				try {


					//        	SharedVariable.Throughput_Handler.postDelayed(SharedVariable.Throughput_Runnable, 100);

					FileSharing.throughput();


				} catch (Exception e) {
					SharedVariable.myCondition = 0;
					e.printStackTrace();
				}
			}
		};



		try{
			//	SharedVariable.Throughput_Handler.postDelayed(SharedVariable.Throughput_Runnable, 500);

			SharedVariable.throughput_Thread.start();

		}

		catch(Exception e){

			SharedVariable.myCondition = 0;
		}




	}



	public void server_client_neg_setting(){

		SharedVariable.Throughput_Handler_neg= new Handler();
		try{

			SharedVariable.Throughput_Runnable_neg= new Runnable() {
				public void run() {

					//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					SharedVariable.Throughput_Handler_neg.postDelayed(SharedVariable.Throughput_Runnable_neg, 100);
					try{
						if(SharedVariable.Throughout_Ended){


							SharedVariable.Throughput_Handler_neg.removeCallbacks(SharedVariable.Throughput_Runnable_neg);	



							/**************				client			**************/
							if(!SharedVariable.is_server){
								//move_clientneg2client1();
								client_setting_neg();
								goto_server_setting_looking();
								MainActivity.goto_server_handlerNeg.postDelayed(MainActivity.goto_server_runnableNeg, 100);

								//client_setting();
								//goto_server_setting();
								//goto_server_handler.postDelayed(goto_server_runnable, 500);

							}
							/****************		server		***************************/
							else{
								server_setting_neg();
								goto_client_setting_looking();
								MainActivity.goto_client_handlerNeg.postDelayed(MainActivity.goto_client_runnableNeg, 100);

								//server_setting();
								//goto_client_setting();
								//goto_client_handler.postDelayed(goto_client_runnable, 25000);

							}







						}
					}
					catch(Exception e){

					}
				}
			};


		}
		catch(Exception e)
		{

		}



	}

	public void server_client_neg_setting_2(){


		SharedVariable.Throughput_Handler_neg= new Handler();
		try{

			SharedVariable.Throughput_Runnable_neg= new Runnable() {
				public void run() {

					//SharedVariable.socketServerThread.interrupt();
					//SharedVariable.socketServerThread.
					SharedVariable.Throughput_Handler_neg.postDelayed(SharedVariable.Throughput_Runnable_neg, 100);
					try{
						if(SharedVariable.Throughout_Ended){


							SharedVariable.Throughput_Handler_neg.removeCallbacks(SharedVariable.Throughput_Runnable_neg);	





							if(SharedVariable.is_server_neg){
								SharedVariable.is_server = true;
								try{
									WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
									wifiManager.setWifiEnabled(false);

								}
								catch(Exception e){}

								try{
									if(ApManager.isApOn(MainActivity.ctx)) // check Ap state :boolean
										ApManager.turnOffAP(MainActivity.ctx); // change Ap state :boolean
								}
								catch(Exception e){}

								server_setting_neg();
								goto_client_setting_looking();
								goto_client_handlerNeg.postDelayed(goto_client_runnableNeg, 100);
							}

							else{
								SharedVariable.is_server = false;
								try{
									WifiManager wifiManager = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
									wifiManager.setWifiEnabled(false);

								}
								catch(Exception e){}
								try{
									client_wifi_setting();
								}
								catch(Exception e){}

								client_setting_neg();
								goto_server_setting_looking();
								goto_server_handlerNeg.postDelayed(goto_server_runnableNeg, 100);
							}




						}
					}
					catch(Exception e){

					}
				}
			};


		}
		catch(Exception e)
		{

		}



	}












}





