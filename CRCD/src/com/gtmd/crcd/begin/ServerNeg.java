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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import android.R.bool;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.gtmd.crcd.SharedVariable;

public class ServerNeg {


	public ServerNeg(MainActivity activity) {
		SharedVariable.socketServerThreadneg = new Thread(new SocketServerThread());
		SharedVariable.socketServerThreadneg.start();
		Toast.makeText(MainActivity.ctx, "BaseStation started", Toast.LENGTH_SHORT).show();
	}
	public int getPort() {
		return SharedVariable.server_port_neg;
	}

	public void onDestroy() {
		if (SharedVariable.socketNeg != null) {
			try {
				SharedVariable.serverSocketNeg.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class SocketServerThread extends Thread {
		int count = 0;

		@Override
		public void run() {
			try {

				SharedVariable.serverSocketNeg = new ServerSocket(SharedVariable.server_port_neg);
				Socket[] socketNeg = new Socket[SharedVariable.total_client_cnt];
				//serverSocketNeg.close();
				while (count<SharedVariable.total_client_cnt) {
					socketNeg[count] = SharedVariable.serverSocketNeg.accept();
					count++;
				}


				byte[] buffer_Neg = new byte[SharedVariable.buffer_size];

				InputStream[] inputstream_Neg = new InputStream[SharedVariable.total_client_cnt]; //amirreza



				byte[] mBufferNeg = new byte[SharedVariable.buffer_size]; //amirreza

				try {
					for(int i = 0 ; i< SharedVariable.total_client_cnt;i++){
						//outputStream[i] = socket[i].getOutputStream();
						inputstream_Neg[i] = socketNeg[i].getInputStream(); //amirreza


					}


					int mLen;

					//new   amirreza


					int bytesReadNeg; //amirreza

					int total=0; //amirreza
					int fileLenght=1000000;//amirreza


					//InputStream inputStreamNeg = SharedVariable.socketNeg.getInputStream();//amirreza


					//amirreza
					String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.client_condition_file_path ;
					File file = new File(fullPath);//file name
					file.mkdirs();
					file = new File(fullPath,SharedVariable.all_nodes_condition_file_name);
					file.createNewFile();
					FileOutputStream outFile =new FileOutputStream(file);



					//amirreza
					int [] cheaters= new int[SharedVariable.total_client_cnt];
					int cnt=0;
					//amirreza
					for(int j=0;j<SharedVariable.total_client_cnt;j++){
						bytesReadNeg = inputstream_Neg[j].read(buffer_Neg); 
						String tmp = new String(buffer_Neg);
						Boolean a=false;

						for (int i = 0; i < SharedVariable.blocked_cnt; i++) {

							if(tmp.contains(SharedVariable.Blocked_List[i]))
								//if(SharedVariable.Blocked_List[i].equals(SharedVariable.Imeis[SharedVariable.total_turned-2]))
							{
								try{
									a=true;
									cheaters[cnt]=j;
									//socketNeg[i]=socketNeg[SharedVariable.total_client_cnt-cnt];
									cnt++;
									break;
								}
								catch(Exception e){}
							}




						}

						try{
							if(!a){
								String [] parts = tmp.split("\n");

								if(Integer.valueOf(parts[0]) + 10000  == 0)
								{


									a=true;
									cheaters[cnt]=j;
									//socketNeg[i]=socketNeg[SharedVariable.total_client_cnt-cnt];
									cnt++;
									SharedVariable.zero_throughput_cheaters++;

								}
							}
						}
						catch(Exception e){}


						if(!a)
						{

							outFile.write(buffer_Neg, 0, bytesReadNeg);

							total += bytesReadNeg;
						}
						//publishProgress((int)(total*100/fileLenght));

					}




					//new amirreza
					/*Random r = new Random();
					int i1 = r.nextInt(100 - 1) + 1;
					int i2=0;

					i2=FileSharing.throughput();*/

					//SharedVariable.myCondition=i1;

					//SharedVariable.myCondition=i2;

					TelephonyManager telephonyManager = (TelephonyManager)MainActivity.ctx.getSystemService(Context.TELEPHONY_SERVICE);
					SharedVariable.My_Imei = telephonyManager.getDeviceId();

					FileSharing.WriteFile(SharedVariable.client_condition_file_name, String.valueOf(SharedVariable.myCondition)+"\n"+SharedVariable.My_Imei+"\n");

					//FileSharing.WriteFile2( "",false);

					//FileSharing.WriteFile2(  String.valueOf(i1)+"\n",true);



					String s = FileSharing.ReadFile();
					String []s2 = s.split("\n");
					SharedVariable.times_to_be_server =  new int[SharedVariable.total_client_cnt+1-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters];
					//SharedVariable.times_to_be_server =  new double[SharedVariable.total_client_cnt+1];
					SharedVariable.Imeis = new String[(SharedVariable.total_client_cnt+1-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters)];



					for( int i =0,k=0;i<(SharedVariable.total_client_cnt-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters)*2;i+=2,k++){
						SharedVariable.times_to_be_server[k]=Integer.valueOf(s2[i]) + 100000  ;

					}

					for(int j =1,k=0 ; j< (SharedVariable.total_client_cnt-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters)*2 ; j+=2,k++)
						SharedVariable.Imeis[k] = s2[j];


					SharedVariable.times_to_be_server[SharedVariable.total_client_cnt-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters]=SharedVariable.myCondition+100000;
					SharedVariable.Imeis[SharedVariable.total_client_cnt-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters] = SharedVariable.My_Imei;
					//neeeeeeeeeeeeewwwwwwwwwwwwwwwwwwwwwwwwwwwwww

					BubbleSort(SharedVariable.times_to_be_server, SharedVariable.Imeis);
					double sum_in=0;
					for(int i=0;i<SharedVariable.total_client_cnt+1-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters;i++){
						sum_in += ((double)1/(double)SharedVariable.times_to_be_server[i]);
					}

					FileSharing.WriteFile2( "",false);



					//String[] time= new String[in.length];

					double[] inv = new double[SharedVariable.times_to_be_server.length];


					for(int i = 0 ; i< SharedVariable.times_to_be_server.length;i++){
						inv[i]=(((double)1/(double)SharedVariable.times_to_be_server[i])/(double)sum_in);
						inv[i] =  Math.round(inv[i]*100.0)/100.0;
						//inv[i]+= .1;
					}
					for (int i = 0; i < SharedVariable.total_client_cnt+1-SharedVariable.blocked_cnt-SharedVariable.zero_throughput_cheaters; i++) {
						//SharedVariable.times_to_be_server[i] = (Integer.valueOf(SharedVariable.times_to_be_server[i])+10)*300;

						SharedVariable.times_to_be_server[i] = ((int)(inv[i]*SharedVariable.Session_Time));
						FileSharing.WriteFile2(  String.valueOf(SharedVariable.times_to_be_server[i])+"\n"+SharedVariable.Imeis[i]+"\n",true);
					}
					//*************		tst111		*****************//
					String stAll = FileSharing.ReadFileServer();
					String st[]= stAll.split("\n");
					for(int i=1,k=0;i<st.length;i+=2,k++){
						if(SharedVariable.My_Imei.equals(st[i])){
							SharedVariable.myTurn=k+1;
							SharedVariable.time_being_server=Integer.valueOf(st[i-1]);

						}
					}

					InputStream inFile = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile()+SharedVariable.client_condition_file_path+SharedVariable.server_all_nodes_condition_file_name_sorted);//amirreza

					OutputStream[] outputStream_Neg1 = new OutputStream[SharedVariable.total_client_cnt]; //amirreza

					try {
						for(int i = 0 ; i< SharedVariable.total_client_cnt;i++){
							//outputStream[i] = socket[i].getOutputStream();
							outputStream_Neg1[i] = socketNeg[i].getOutputStream(); //amirreza
						}
					}
					catch(Exception e){}

					RandomString rs = new RandomString(SharedVariable.pass_length);
					SharedVariable.pass = rs.nextString();
					SharedVariable.aes = new AES_encryption();
					while((mLen = inFile.read(mBufferNeg, 0, 1024)) > 0){
						for(int j=0;j<SharedVariable.total_client_cnt;j++) { 

							Boolean aaa=false;
							try{
								for (int l = 0; l < SharedVariable.blocked_cnt+SharedVariable.zero_throughput_cheaters; l++) {
									if(j == cheaters[l]){
										aaa=true;
										break;
									}

								}

								if(!aaa)
								{


									byte[] key = SharedVariable.pass.getBytes();
									System.arraycopy(key, 0, mBufferNeg, mLen, key.length);
									outputStream_Neg1[j].write(mBufferNeg, 0, mLen+key.length);
									//outputStream_Neg1[j].write(mBufferNeg, 0, mLen);
								}
								else{
									socketNeg[j].close();
								}
							}
							catch (Exception e) {
								// TODO: handle exception
							}
						}


					}
					SharedVariable.total_client_cnt -= SharedVariable.blocked_cnt+SharedVariable.zero_throughput_cheaters;
					SharedVariable.blocked_cnt=0;
					SharedVariable.zero_throughput_cheaters=0;


					inFile.close();

					for (int i = 0; i < 9999999; i++) {

					}




					SharedVariable.neg_end = true;


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!SharedVariable.serverSocketNeg.isClosed())
				SharedVariable.neg_end = true;


		}

	}

	public static void BubbleSort( int[ ] num , String [] im)
	{
		int j;
		boolean flag = true;   // set flag to true to begin first pass
		int temp;   //holding variable
		String temp_im;
		while ( flag )
		{
			flag= false;    //set flag to false awaiting a possible swap
			for( j=0;  j < num.length -1;  j++ )
			{
				if ( num[ j ] > num[j+1] )   // change to > for ascending sort
				{
					temp = num[ j ];                //swap elements
					temp_im = im[j];

					num[ j ] = num[ j+1 ];
					im[j] = im[j+1];
					num[ j+1 ] = temp;
					im[j+1] = temp_im;
					flag = true;              //shows a swap occurred  
				} 
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

