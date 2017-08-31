/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.gtmd.crcd.SharedVariable;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Environment;
import android.widget.Toast;

public class FileSharing {

	
	FileSharing(Context ctx){
		
		
		
		WriteFile("new_note.txt","hi,s,wda,wfa");
	}
	
	
	public static void WriteFile(String sFileName, String sBody) {
	    try {
	        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.client_condition_file_path) ;
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	      
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	

	public static void WriteFile2( String sBody, boolean append) {
	    try {
	        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.client_condition_file_path );
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, SharedVariable.server_all_nodes_condition_file_name_sorted);
	        FileWriter writer = new FileWriter(gpxfile,append);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	       // Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	public static String ReadFile(){
		
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.client_condition_file_path +SharedVariable.all_nodes_condition_file_name);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}

		//Find the view by its id
		//TextView tv = (TextView)findViewById(R.id.text_view);

		//Set the text
		//tv.setText(text.toString());
		
		return text.toString();
		
	}
	
	
	
	
public static String ReadFileServer(){
		
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+SharedVariable.client_condition_file_path +SharedVariable.server_all_nodes_condition_file_name_sorted);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}

		//Find the view by its id
		//TextView tv = (TextView)findViewById(R.id.text_view);

		//Set the text
		//tv.setText(text.toString());
		
		return text.toString();
		
	}


public static void throughput(){

	 SharedVariable.Throughput_Tmp_Downloaded = 0;
	
	int testing=0;
	long downloaded = 0;
	try{
		
		 byte[] data = new byte[1024];
		   int x = 0;	
	
	//long TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
	long TotalRxBeforeTest = TrafficStats.getTotalRxBytes();
	BufferedInputStream in;
		// FileOutputStream fos;
		 //BufferedOutputStream bout;
		URL urll;
		urll = new URL("http://dl1.video.varzesh3.com/video/clip95/4/video/havashi/eftetah_hotel_cr7.mp4");
		
		   SharedVariable.Throughput_Handler.postDelayed(SharedVariable.Throughput_Runnable, SharedVariable.Throughput_Diff_Sec*1000);

	//	final String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/topp.mp4";
		//url = new URL("http://dl1.video.varzesh3.com/video/clip95/4/video/havashi/eftetah_hotel_cr7.mp4");
		SharedVariable.connection1 = (HttpURLConnection) urll.openConnection();
	    /*File file=new File(path);
	    if(file.exists()){
	             downloaded = (int) file.length();
	             SharedVariable.connection1.setRequestProperty("Range", "bytes="+(file.length())+"-");
	        }
	    else{
	    	SharedVariable.connection1.setRequestProperty("Range", "bytes=" + downloaded + "-");
	    }*/

	 in = new BufferedInputStream(SharedVariable.connection1.getInputStream());
   // fos=(downloaded==0)? new FileOutputStream(path): new FileOutputStream(path,true);
    //bout = new BufferedOutputStream(fos, 1024);
   
   long BeforeTime = System.currentTimeMillis();
	
   while ((x = in.read(data, 0, 1024)) >= 0) {
      // bout.write(data, 0, x);
        downloaded += x;
        SharedVariable.Throughput_Tmp_Downloaded =downloaded;
        

        //mProgressDialog.setProgress((int) MainActivity.downloaded);

   }
   SharedVariable.Throughput_Handler.removeCallbacks(SharedVariable.Throughput_Runnable);
   
   SharedVariable.Throughout_Ended = true;
	//long TotalTxAfterTest = TrafficStats.getTotalTxBytes();
	long TotalRxAfterTest = TrafficStats.getTotalRxBytes();
	long AfterTime = System.currentTimeMillis();

	double TimeDifference = AfterTime - BeforeTime;

	double rxDiff = TotalRxAfterTest - TotalRxBeforeTest;
	//double txDiff = TotalTxAfterTest - TotalTxBeforeTest;

	double rxBPS=0;
	
	if((downloaded != 0) ) {
	     rxBPS = (downloaded / (TimeDifference/1000)); // total rx bytes per second.
	
	
	     testing =(int) rxBPS;
	}

	
	
	
	else {
	    testing = 0;
	}

	SharedVariable.myCondition = testing;
	
	}
	catch(Exception e){}
	
	
	//return testing;		
}
}


