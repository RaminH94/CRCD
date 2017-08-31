/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.gtmd.crcd.SharedVariable;

public class Client extends AsyncTask<String, Integer, String> {

	//public static ProgressDialog mProgressDialog;

	Client(String addr, int port) {
		try{
		/*	mProgressDialog = new ProgressDialog(MainActivity.ctx);

			mProgressDialog.setMessage("Loading");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
*/		}
		catch(Exception e ){}
		try{
			SharedVariable.is_server=false;
			Toast.makeText(MainActivity.ctx, "Sink started", Toast.LENGTH_SHORT).show();
		}
		catch(Exception e ){}
	} 
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		try{
			Toast.makeText(MainActivity.ctx, "connected to Seed", Toast.LENGTH_LONG).show();
			//mProgressDialog.show();
		}
		catch(Exception e ){}
	}
	@Override
	protected String doInBackground(String... arg0) {

		try {
			SharedVariable.socket = new Socket(SharedVariable.client_dest_ip, SharedVariable.client_port);
			byte[] buffer = new byte[SharedVariable.buffer_size];
			int bytesRead;
			SharedVariable.videofile_length = 0;
			
			if(SharedVariable.videofile.exists()){
				SharedVariable.client_rcv_total_bytesRead = (int) SharedVariable.videofile.length();
				SharedVariable.videofile_length = SharedVariable.videofile.length();
			}
			SharedVariable.outFile=(SharedVariable.client_rcv_total_bytesRead==0)? new FileOutputStream(SharedVariable.videofile_path): new FileOutputStream(SharedVariable.videofile_path,true);//hiiiiiissssssssss
			InputStream inputStream = SharedVariable.socket.getInputStream();
			//inputStream.
			try{

				byte [] tst = new byte[SharedVariable.start_connection.length()];
				int tmp = inputStream.read(tst);

				if(SharedVariable.start_connection.equals(new String(tst)))
				{
					//Toast.makeText(MainActivity.ctx, "hello", Toast.LENGTH_SHORT).show();
					MainActivity.goto_server_handler_checktime.postDelayed(MainActivity.goto_server_runnable_checktime,(int) SharedVariable.times_to_be_server[SharedVariable.total_turned-2]+700);
					SharedVariable.connection_startTime = System.currentTimeMillis();
				}

			}
			catch(Exception e){}
			//inputStream.
			SharedVariable.videofile_length = SharedVariable.videofile.length();
			
			try{
				if(SharedVariable.videofile_length>SharedVariable.minimum_file_transfered && SharedVariable.video_isstart==0){
/*					try{
						mProgressDialog.dismiss();
					}
					catch(Exception e){
					}
*/					SharedVariable.video_isstart=1;
					SharedVariable.timerHandler.postDelayed(SharedVariable.timerRunnable, 0);
					
				}
			}
			catch(Exception e){}

			
			while ((bytesRead = inputStream.read(buffer)) > 0) {
				try{
					SharedVariable.client_rcv_total_bytesRead += bytesRead;
					SharedVariable.outFile.write(buffer, 0, bytesRead);
					publishProgress((int)(SharedVariable.client_rcv_total_bytesRead*100/SharedVariable.client_rcv_fileLenght));
				}
				catch(Exception e){}
				SharedVariable.videofile_length = SharedVariable.videofile.length();
				try{
					if(SharedVariable.videofile_length>SharedVariable.minimum_file_transfered && SharedVariable.video_isstart==0){
/*						try{
							mProgressDialog.dismiss();
						}
						catch(Exception e){
						}
*/						SharedVariable.video_isstart=1;
						SharedVariable.timerHandler.postDelayed(SharedVariable.timerRunnable, 0);
						
					}
				}
				catch(Exception e){}

				try{
					
					SharedVariable.connection_stopTime = System.currentTimeMillis();
					if(SharedVariable.is_server_neg)
					cheating_check(SharedVariable.total_turned);
				}
				catch(Exception e){}

			}

		}
		catch (UnknownHostException e) {
			try {
				//SharedVariable.socket.close();	test111
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			try {
				//SharedVariable.socket.close();	test111
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {
			if (SharedVariable.socket != null) {
				try {
					SharedVariable.socket.close();	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
//		mProgressDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		//textResponse.setText(response);
	/*	try{
			mProgressDialog.dismiss();
		}
		catch(Exception e)
		{}
	*/}

	public static void cheating_check(int turn){
		try{
		if(SharedVariable.connection_startTime!=0 && (SharedVariable.times_to_be_server[turn-2]-700)<(SharedVariable.connection_stopTime-SharedVariable.connection_startTime))
		{
			
			try{
			for (int i = 0; i < SharedVariable.blocked_cnt; i++) {
				if(SharedVariable.Blocked_List[i].equals(SharedVariable.Imeis[turn-2]))
					return;
			}
			}
			catch(Exception e){}
			SharedVariable.Blocked_List[SharedVariable.blocked_cnt++]=SharedVariable.Imeis[turn-2];
			
		}
		else{
			SharedVariable.connection_startTime = SharedVariable.connection_stopTime;
		}
	}
		catch(Exception e){}
	}
	
		
}
