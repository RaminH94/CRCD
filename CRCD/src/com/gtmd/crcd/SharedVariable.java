/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;

import android.net.Uri;
import android.os.Handler;
import android.widget.MediaController;
import android.widget.VideoView;
 
import com.gtmd.crcd.begin.AES_encryption;

public class SharedVariable {
	public static long connection_startTime=0 , connection_stopTime=0;
	public static int blocked_cnt=0;
	public static Thread throughput_Thread;
	public static boolean Throughout_Ended = false;
	public static String pass= "password123456789";
	public static int pass_length = 10;
	public static int Throughput_Diff_Sec = 7;
	public static HttpURLConnection connection1;
	public static Handler Throughput_Handler; 
	public static Handler Throughput_Handler_neg;
	public static Runnable Throughput_Runnable;
	public static Runnable Throughput_Runnable_neg;

	public static int Throughput_Downloaded;
	public static double Throughput_Tmp_Downloaded;

	public static int Session_Time = 60000;
	public static String start_connection = "hello";
	public static String stop_connection = "connectionEnded";
	public static int[] times_to_be_server;
	//public static double[] times_to_be_server;
	public static String [] Imeis;
	public static String My_Imei="";
	public static int time_being_server =10000;
	public static int time_stop_server =800;
	public static int myCondition;
	public static double myCondition2;
	public static int myTurn;
	public static int total_turned=1;
	public static VideoView vd ;
	public static Handler timerHandler;
	public static Runnable timerRunnable ;
	public static MediaController mc;
	public static long video_starttime = 0,video_stoptime=0;
	public static int video_isstart=0;
	public static int video_lastseekpoint=0;
	public static long videofile_lastlength=0, videofile_length=0;
	public static long videofile_changed_size = 6000;
	public static int postdelayed_time = 900;
	public static Uri videofile_uri ;	
	public static File videofile ;
	public static String videofile_path ; 
	public static int buffer_size = 1024;
	public static boolean stop_dl_server = false;
	public static int total_client_cnt=1;  
	public static int zero_throughput_cheaters = 0;
	public static String DLURL="http://as10.fars.asset.aparat.com/aparat-video/6ae016f4a5811051e603c3638f4071455504109-720p__59905.mp4"; 
			//"http://dl1.video.varzesh3.com/video/clip95/5/video/havashi/ronaldo_tarikhsaz.mp4";
	//"http://dl1.video.varzesh3.com/video/clip95/4/video/havashi/eftetah_hotel_cr7.mp4";
	public static String video_name = "/hotel.mp4";
	//"http://kaardooni.ir/eftetah_hotel_cr7.3gp";
	//"http://dl1.video.varzesh3.com/video/clip95/4/video/havashi/eftetah_hotel_cr7.mp4";
	public static int minimum_file_transfered = 30000;
	public static boolean is_server = false;
	public static boolean is_server_neg = false;
	public static String IMEI ;
	public static String [] Blocked_List;
	public static boolean neg_end=false;
	
	public static FileOutputStream outFile ;
	public static AES_encryption aes;
	/*************		client		*************/
	//WifiConfiguration wifi_config;
	//WifiConfiguration conf = new WifiConfiguration();
	public static String ssid_name ="D2DHotspot";
	public static String client_ssid_name = "\"\"" +ssid_name +total_turned + "\"\"";
	public static String server_ssid_name = "\""+ssid_name+total_turned+"\"";
	public static String client_dest_ip = "192.168.43.1";
	public static int client_port = 8585;
	public static int server_port = 8585;
	public static int client_port_neg = 4030;
	public static int server_port_neg = 4030;

	public static Socket socket = null;
	public static int client_rcv_fileLenght=1000000;
	public static int client_rcv_total_bytesRead=0;

	public static Runnable client_ssid_check_runnable,client_ssid_check_runnable_neg;
	public static Runnable client_wifi_connected_check_runnable,client_wifi_connected_check_runnable_neg;
	public static Handler client_ssid_check_handler,client_ssid_check_handler_neg;
	public static Handler client_wifi_connected_check_handler,client_wifi_connected_check_handler_neg;
	public static String client_condition_file_name = "mycondition.txt";
	public static String client_condition_file_path = "/Notes/";
	public static String all_nodes_condition_file_name = "allnodes.txt";
	public static String server_all_nodes_condition_file_name_sorted = "sorted.txt";
	public static Socket socketNeg = null;
	public static ServerSocket serverSocketNeg;
	public static ServerSocket serverSocket;
	/***********************************/

	public static Thread socketServerThread ;
	public static Thread socketServerThreadneg ;
}
