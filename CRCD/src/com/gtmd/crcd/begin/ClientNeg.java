/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.gtmd.crcd.SharedVariable;

public class ClientNeg extends AsyncTask<String, Integer, String> {



	public static OutputStream outputStream_Neg;
	//public static ProgressDialog mProgressDialog;

	ClientNeg(String addr, int port) {
		/*	mProgressDialog = new ProgressDialog(MainActivity.ctx);
		mProgressDialog.setMessage("Loading");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 */
		Toast.makeText(MainActivity.ctx, "Negotiation", Toast.LENGTH_SHORT).show();
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		try{
			Toast.makeText(MainActivity.ctx, "Negotiation started", Toast.LENGTH_SHORT).show();

			//	mProgressDialog.show();
		}
		catch(Exception e ){}
	}
	@Override
	protected String doInBackground(String... arg0) {

		try {
			SharedVariable.socketNeg = new Socket(SharedVariable.client_dest_ip, SharedVariable.client_port_neg);

			//Socket socketNeg = new Socket();
			byte[] mBufferNeg = new byte[SharedVariable.buffer_size];

			//outputStream_Neg = new OutputStream;
			outputStream_Neg = SharedVariable.socketNeg.getOutputStream();
			byte[] buffer_Neg = new byte[SharedVariable.buffer_size];
			SharedVariable.myTurn = -1;
			//SharedVariable.blocked_cnt=0;

			//InputStream[] inputstream_Neg = new InputStream[SharedVariable.total_client_cnt];
			int mLen;

			//WifiManager wm = (WifiManager) MainActivity.ctx.getSystemService(Context.WIFI_SERVICE);
			//String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
			/*Random r = new Random();
			int i1 = r.nextInt(100 - 1) + 1;

			int i2=0;

			i2= FileSharing.throughput();
			SharedVariable.myCondition=i2;*/



			TelephonyManager telephonyManager = (TelephonyManager)MainActivity.ctx.getSystemService(Context.TELEPHONY_SERVICE);
			SharedVariable.My_Imei = telephonyManager.getDeviceId();

			//neeeeeeeeeeeeewwwwwwwwwwwwww
			FileSharing.WriteFile(SharedVariable.client_condition_file_name, String.valueOf(SharedVariable.myCondition)+"\n"+SharedVariable.My_Imei+"\n");
			//FileSharing.WriteFile2("new_note.txt",ip +"\n");

			InputStream inFile = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()+SharedVariable.client_condition_file_path+SharedVariable.client_condition_file_name);//amirreza


			try{


				while((mLen = inFile.read(mBufferNeg, 0, 1024)) > 0){
					//for(int j=0;j<SharedVariable.total_client_cnt;j++)    
					outputStream_Neg.write(mBufferNeg, 0, mLen);

				}



				inFile.close();





				InputStream inputStreamNeg = SharedVariable.socketNeg.getInputStream();//amirreza


				//amirreza
				String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.client_condition_file_path ;
				File file = new File(fullPath);//file name
				file.mkdirs();
				file = new File(fullPath,SharedVariable.all_nodes_condition_file_name);
				file.createNewFile();
				FileOutputStream outFile =new FileOutputStream(file);

				int bytesReadNeg; //amirreza

				int total=0; //amirreza
				int fileLenght=1000000;//amirreza

				//amirreza

				//	MainActivity.move_clientneg_handler.postDelayed(MainActivity.move_clientneg_runnable, 500);

				//amirreza
				bytesReadNeg = inputStreamNeg.read(buffer_Neg);
				//while ((bytesReadNeg = inputStreamNeg.read(buffer_Neg))!= -1) {
				//	byteArrayOutputStream.write(buffer, 0, bytesRead);
				//	response += byteArrayOutputStream.toString("UTF-8");


				total += bytesReadNeg;


				outFile.write(buffer_Neg, 0, bytesReadNeg);
				//publishProgress((int)(total*100/fileLenght));
				//}



				InputStream key_in = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()+SharedVariable.client_condition_file_path+SharedVariable.all_nodes_condition_file_name);    
				bytesReadNeg=key_in.read(buffer_Neg); 

				String s = FileSharing.ReadFile();

				String []s2 = s.split("\n");
				//SharedVariable.total_client_cnt -= SharedVariable.blocked_cnt;
				SharedVariable.total_client_cnt = (s2.length/2)-1;
				try{
					SharedVariable.Imeis = new String[(SharedVariable.total_client_cnt+1)];
					//SharedVariable.times_to_be_server = new int[(SharedVariable.total_client_cnt+1)];
					SharedVariable.times_to_be_server = new int[(SharedVariable.total_client_cnt+1)];
					for( int i =0,k=0;i<(SharedVariable.total_client_cnt+1)*2;i+=2,k++){
						SharedVariable.times_to_be_server[k]=Integer.valueOf(s2[i]);

					}

					for(int j =1,k=0 ; j< (SharedVariable.total_client_cnt+1)*2 ; j+=2,k++)
						SharedVariable.Imeis[k] = s2[j];

				} 
				catch(Exception e){}


				byte [] key = new byte[SharedVariable.pass_length];    
				for(int i = 0 ; i<key.length ;i++){     

					key[i]=buffer_Neg[bytesReadNeg-SharedVariable.pass_length+i];   
				} 
				SharedVariable.pass = new String(key);
				/*SecretKeySpec aeskey = new SecretKeySpec(key, "AES"); 
			SharedVariable.aes = new AES_encryption();    
			SharedVariable.aes.set_key(aeskey);*/

				String stAll = FileSharing.ReadFile();
				String st[]= stAll.split("\n");
				try{
					for(int i=1, k=0;i<st.length;i+=2,k++){
						if(SharedVariable.My_Imei.equals(st[i])){
							SharedVariable.myTurn=k+1;
							SharedVariable.time_being_server=(int)SharedVariable.times_to_be_server[k];
							break;
						}
					}
				}
				catch(Exception e ){}

				try{
					//mProgressDialog.dismiss();
					SharedVariable.neg_end = true;	
				}
				catch(Exception e)
				{}

				SharedVariable.neg_end = true;	


			}

			catch(Exception e ){
				MainActivity.myact.runOnUiThread(new Runnable() {
					public void run() {

						try {
							Toast.makeText(MainActivity.ctx, "Selfish behaviour detected !!!", Toast.LENGTH_SHORT).show();
							MainActivity.myact.finish();
							//	adapter.notifyDataSetChanged();
							// some code 2
						} catch (Exception e) {
						}


					}
				});


			}




		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally {
			if (SharedVariable.socketNeg != null) {
				try {
					SharedVariable.socketNeg.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (SharedVariable.socketNeg != null) {
			try {
				SharedVariable.socketNeg.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try{
			//mProgressDialog.dismiss();
			SharedVariable.neg_end = true;	
		}
		catch(Exception e)
		{}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		//mProgressDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		//textResponse.setText(response);
		try{
			//mProgressDialog.dismiss();
			SharedVariable.neg_end = true;	
		}
		catch(Exception e)
		{}
		try{
			//	mProgressDialog.dismiss();
		}
		catch(Exception e)
		{}

	}

}
