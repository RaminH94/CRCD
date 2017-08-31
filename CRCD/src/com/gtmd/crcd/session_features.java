/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd;

import com.example.continuedl.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class session_features extends Activity {

	public EditText ed_total_client_cnt,ed_session_time;
	Button btn_ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_features);

		ed_session_time = (EditText) findViewById(R.id.editText_session_time);
		ed_total_client_cnt = (EditText) findViewById(R.id.editText_total_client_cnt);
		btn_ok = (Button) findViewById(R.id.button_ok);

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
					SharedVariable.total_client_cnt = Integer.valueOf(ed_total_client_cnt.getText().toString());
				}
				catch(Exception e){}
				try{
					SharedVariable.Session_Time = Integer.valueOf(ed_session_time.getText().toString())/* *60000 */;
				}
				catch(Exception e){}
				SharedVariable.Blocked_List = new String[SharedVariable.total_client_cnt];
				
				startActivity(new Intent(getBaseContext(), com.gtmd.crcd.begin.MainActivity.class));


			}
		});

	}
}
