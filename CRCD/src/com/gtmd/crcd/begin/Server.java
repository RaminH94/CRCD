/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.gtmd.crcd.SharedVariable;

public class Server {

	//ProgressDialog mProgressDialog;
	public Server() {
		/*mProgressDialog = new ProgressDialog(MainActivity.ctx);
		mProgressDialog.setMessage("A message");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 */
		SharedVariable.is_server = true;
		SharedVariable.socketServerThread = new Thread(new SocketServerThread());
		SharedVariable.socketServerThread.start();
		Toast.makeText(MainActivity.ctx, "Seed started", Toast.LENGTH_LONG).show();
	}

	public int getPort() {
		return SharedVariable.server_port;
	}

	public void onDestroy() {
		if (SharedVariable.serverSocket != null) {
			try {
				SharedVariable.serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SocketServerThread extends Thread {

		int count = 0;

		@Override
		public void run() {
			try {

				SharedVariable.serverSocket = new ServerSocket(SharedVariable.server_port);
				Socket[] socket = new Socket[SharedVariable.total_client_cnt];
				while (count<SharedVariable.total_client_cnt) {
					socket[count] = SharedVariable.serverSocket.accept();
					count++;
				}
				try{
					SharedVariable.videofile_length = SharedVariable.videofile.length();
					if(SharedVariable.videofile_length>SharedVariable.minimum_file_transfered && SharedVariable.video_isstart==0){
						SharedVariable.video_isstart=1;
						SharedVariable.timerHandler.postDelayed(SharedVariable.timerRunnable, 0);

					}
					}
					catch(Exception e){}

				//Toast.makeText(MainActivity.ctx, "all devices connected successfully", Toast.LENGTH_LONG).show();
				OutputStream[] outputStream = new OutputStream[SharedVariable.total_client_cnt];
				byte[] mBuffer = new byte[SharedVariable.buffer_size];
				try {
					for(int i = 0 ; i< SharedVariable.total_client_cnt;i++)
						outputStream[i] = socket[i].getOutputStream();

					try{
						byte [] tst = SharedVariable.start_connection.getBytes();
						for(int j=0;j<SharedVariable.total_client_cnt;j++)    
							outputStream[j].write(tst, 0, tst.length);
					}
					catch(Exception e){}

					
					try{
					MainActivity.goto_client_handler.postDelayed(MainActivity.goto_client_runnable, SharedVariable.time_being_server);
					}
					catch(Exception e){
						e.printStackTrace();
					}
					MainActivity.goto_client_handler_stopdl.postDelayed(MainActivity.goto_client_runnable_stopdl, SharedVariable.time_being_server-SharedVariable.time_stop_server);

					URL url = new URL(SharedVariable.DLURL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					int mLen;
					if(SharedVariable.videofile.exists()){
						SharedVariable.client_rcv_total_bytesRead = (int) SharedVariable.videofile.length();
						connection.setRequestProperty("Range", "bytes="+(SharedVariable.videofile.length())+"-");
					}
					else{
						connection.setRequestProperty("Range", "bytes=" + SharedVariable.videofile.length()+ "-");
					}
					BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

					FileOutputStream fos=(SharedVariable.client_rcv_total_bytesRead==0)? new FileOutputStream(SharedVariable.videofile_path): new FileOutputStream(SharedVariable.videofile_path,true);
					SharedVariable.stop_dl_server=false;

					
					
					while(!SharedVariable.stop_dl_server && (mLen = in.read(mBuffer, 0, SharedVariable.buffer_size)) > 0){
						if(!SharedVariable.stop_dl_server){
							for(int j=0;j<SharedVariable.total_client_cnt;j++)    
								outputStream[j].write(mBuffer, 0, mLen);


							fos.write(mBuffer, 0, mLen);
							SharedVariable.client_rcv_total_bytesRead += mLen;
							try{
							SharedVariable.videofile_length = SharedVariable.videofile.length();
							if(SharedVariable.videofile_length>SharedVariable.minimum_file_transfered && SharedVariable.video_isstart==0){
								SharedVariable.video_isstart=1;
								SharedVariable.timerHandler.postDelayed(SharedVariable.timerRunnable, 0);

							}
							}
							catch(Exception e){}
						}

					}


				} catch (IOException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	public String getIpAddress() {
		String ip = "";
		try {	
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
							.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "Server running at : "
								+ inetAddress.getHostAddress();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}
}

