package com.widget.hotspot;



import com.example.controlcarbyblueth.R;

import android.os.Bundle;  
import android.app.Activity;  
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
import android.content.IntentFilter;  
import android.util.Log;  
import android.view.Menu;  
import android.view.View;  
import android.widget.Button;
	  
	public class WifiMn extends Activity {  
	  
	    public static final String TAG = "MainActivity";  
	      
	    private Button mBtn1, mBtn2;  
	      
	    private WifiAdmin mWifiAdmin;  
	      
	    private Context mContext = null;  
	    @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	          
	        mContext = this;  
	          
	        setContentView(R.layout.activity_main);  
	          
	        mBtn1 = (Button)findViewById(R.id.button1);  
	        mBtn2 = (Button)findViewById(R.id.button2);  
	        mBtn1.setText("�������Wifi");  
	        mBtn2.setText("�������Wifi�ȵ�");  
	        mBtn1.setOnClickListener(new Button.OnClickListener() {  
	              
	            @Override  
	            public void onClick(View v) {  
	                // TODO Auto-generated method stub  
	  
	                mWifiAdmin = new WifiAdmin(mContext) {  
	                      
	                    @Override  
	                    public void myUnregisterReceiver(BroadcastReceiver receiver) {  
	                        // TODO Auto-generated method stub  
	                    	WifiMn.this.unregisterReceiver(receiver);  
	                    }  
	                      
	                    @Override  
	                    public Intent myRegisterReceiver(BroadcastReceiver receiver,  
	                            IntentFilter filter) {  
	                        // TODO Auto-generated method stub  
	                    	WifiMn.this.registerReceiver(receiver, filter);  
	                        return null;  
	                    }  
	                      
	                    @Override  
	                    public void onNotifyWifiConnected() {  
	                        // TODO Auto-generated method stub  
	                        Log.v(TAG, "have connected success!");  
	                        Log.v(TAG, "###############################");  
	                    }  
	                      
	                    @Override  
	                    public void onNotifyWifiConnectFailed() {  
	                        // TODO Auto-generated method stub  
	                        Log.v(TAG, "have connected failed!");  
	                        Log.v(TAG, "###############################");  
	                    }  
	                };  
	                mWifiAdmin.openWifi();  
	                mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo("Netcore214", "19930909", WifiAdmin.TYPE_WPA));  
	                  
	            }  
	        });  
	          
	        mBtn2.setOnClickListener(new Button.OnClickListener() {  
	              
	            @Override  
	            public void onClick(View v) {  
	                // TODO Auto-generated method stub  
	                  
	                WifiApAdmin wifiAp = new WifiApAdmin(mContext);  
	                wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");  
	            }  
	        });  
	          
	    }  
	  
	    @Override  
	    public boolean onCreateOptionsMenu(Menu menu) {  
	        // Inflate the menu; this adds items to the action bar if it is present.  
	        getMenuInflater().inflate(R.menu.activity_main_wifi, menu);  
	        return true;  
	    }  
	  
	     @Override  
	        public void onResume() {  
	            super.onResume();  
	              
	            Log.d("Rssi", "Registered");  
	        }  
	  
	        @Override  
	        public void onPause() {  
	            super.onPause();  
	              
	            Log.d("Rssi", "Unregistered");  
	        }  
	      
	  


}
