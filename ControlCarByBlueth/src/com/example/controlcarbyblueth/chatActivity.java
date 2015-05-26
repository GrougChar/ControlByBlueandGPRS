package com.example.controlcarbyblueth;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import tjlg.blutooth.util.JsonParser;

import com.example.controlcarbyblueth.Bluetooth.ServerOrCilent;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class chatActivity extends Activity{
    /** Called when the activity is first created. */
	
	private  ListView mListView;
	private  ArrayList<deviceListItem>list;
	private Button sendButton;
	private Button voice;
	private EditText et;
	private  deviceListAdapter mAdapter;
	private  Context mContext;
	// ������д����
		private SpeechRecognizer mIat;
			// ������дUI
		private RecognizerDialog iatDialog;
	/* һЩ��������������������� */
	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
	
	private BluetoothServerSocket mserverSocket = null;
	private ServerThread startServerThread = null;
	private clientThread clientConnectThread = null;
	private static BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private readThread mreadThread = null;;	
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.chat);
        mContext = this;
        init();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=537ea208");
    }
    
	private void init() {		   
		list = new ArrayList<deviceListItem>();
		mAdapter = new deviceListAdapter(this, list);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		//mListView.setOnItemClickListener(this);
		mListView.setFastScrollEnabled(true);
		et= (EditText)findViewById(R.id.MessageText);	
		et.clearFocus();
		
		// ��ʼ��ʶ�����
		mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
		// ��ʼ����дDialog,���ֻʹ����UI��д����,���贴��SpeechRecognizer
		iatDialog = new RecognizerDialog(this,mInitListener);

		
		sendButton= (Button)findViewById(R.id.btn_msg_send);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String msgText =et.getText().toString();
				if (msgText.length()>0) {
					if(msgText.contains("ǰ��")){
					msgText = "a";
					}
					else if(msgText.contains("��ת"))
					{
						msgText = "b";
					}
					else if(msgText.contains("��ת"))
					{
						msgText = "c"; 
					}
					else if(msgText.contains("����"))
					{
						msgText = "d";
					}
					else if(msgText.contains("���ҽ���"))
					{
						msgText = "e";
					}
					else if(msgText.contains("����"))
					{
						msgText = "f";
					}
					else if(msgText.contains("��ʫ"))
					{
						msgText = "g";
					}
					else if(msgText.contains("Ѳ��"))
					{
						msgText = "h";
					}
					else if(msgText.contains("˵����ʶ��"))
					{
						msgText = "i";
					}
					else if(msgText.contains("Σ������ʶ��"))
					{
						msgText = "j";
					}
					else if(msgText.contains("����ʶ��"))
					{
						msgText = "k";
					}
					else if(msgText.contains("��ɫʶ��"))
					{
						msgText = "l";
					}
					else if(msgText.contains("��̬Ŀ����"))
					{
						msgText = "m";
					}
					sendMessageHandle(msgText);	
					et.setText("");
					et.clearFocus();
					//close InputMethodManager
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
				}else
				Toast.makeText(mContext, "�������ݲ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
			}
		});
		
		voice= (Button)findViewById(R.id.voice_recognize);
		voice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				et.setText(null);// �����ʾ����
				// ���ò���
				setParam();
				iatDialog.setListener(recognizerDialogListener);
				iatDialog.show();
			}

			
		});		
	}    
	
	
	/**
	 * ��ʼ����������
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code == ErrorCode.SUCCESS) {
				findViewById(R.id.voice_recognize).setEnabled(true);
			}
		}
	};
	
	protected void setParam() {
		
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// ������������
		mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
	// ��������ǰ�˵�
	mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
	// ����������˵�
	mIat.setParameter(SpeechConstant.VAD_EOS,  "1000");
	// ���ñ�����
	mIat.setParameter(SpeechConstant.ASR_PTT,  "0");
	// ������Ƶ����·��
	mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "/sdcard/iflytek/wavaudio.pcm");
}
	
	
	
	/**
	 * ��дUI������
	 */
	private RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener(){
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			
			et.append(text);
			if(text.contains("ǰ��")){
				text="a";
			}
			else if(text.contains("��ת"))
			{
				text= "b";
			}
			else if(text.contains("��ת"))
			{
				text = "c"; 
			}
			else if(text.contains("����"))
			{
				text = "d";
			}
			else if(text.contains("���ҽ���"))
			{
				text = "e";
			}
			else if(text.contains("����"))
			{
				text = "f";
			}
			else if(text.contains("��ʫ"))
			{
				text = "g";
			}
			else if(text.contains("Ѳ��"))
			{
				text = "h";
			}
			else if(text.contains("˵����ʶ��"))
			{
				text = "i";
			}
			else if(text.contains("Σ������ʶ��"))
			{
				text = "j";
			}
			else if(text.contains("����ʶ��"))
			{
				text = "k";
			}
			else if(text.contains("��ɫ��λ"))
			{
				text = "l";
			}
			else if(text.contains("��̬Ŀ����"))
			{
				text = "m";
			}
			sendMessageHandle(text);//��������
			//close InputMethodManager
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
			
			et.setText("");
			et.clearFocus();
			et.setSelection(et.length());
		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
		}

	};

	
    private Handler LinkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	//Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
        	if(msg.what==1)
        	{
        		list.add(new deviceListItem((String)msg.obj, true));
        	}
        	else
        	{
        		list.add(new deviceListItem((String)msg.obj, false));
        	}
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
        }
        
    };    
    
    @Override
    public synchronized void onPause() {
        super.onPause();
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(Bluetooth.isOpen)
        {
        	Toast.makeText(mContext, "�����Ѿ��򿪣�����ͨ�š����Ҫ�ٽ������ӣ����ȶϿ���", Toast.LENGTH_SHORT).show();
        	return;
        }
        if(Bluetooth.serviceOrCilent==ServerOrCilent.CILENT)
        {
			String address = Bluetooth.BlueToothAddress;
			if(!address.equals("null"))
			{
				device = mBluetoothAdapter.getRemoteDevice(address);	
				clientConnectThread = new clientThread();
				clientConnectThread.start();
				Bluetooth.isOpen = true;
			}
			else
			{
				Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();
			}
        }
        else if(Bluetooth.serviceOrCilent==ServerOrCilent.SERVICE)
        {        	      	
        	startServerThread = new ServerThread();
        	startServerThread.start();
        	Bluetooth.isOpen = true;
        }
    }
	//�����ͻ���
	private class clientThread extends Thread { 		
		public void run() {
			try {
				//����һ��Socket���ӣ�ֻ��Ҫ��������ע��ʱ��UUID��
				// socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				//����
				Message msg2 = new Message();
				msg2.obj = "���Ժ��������ӷ�����:"+Bluetooth.BlueToothAddress;
				msg2.what = 0;
				LinkDetectedHandler.sendMessage(msg2);
				
				socket.connect();
				
				Message msg = new Message();
				msg.obj = "�Ѿ������Ϸ���ˣ����Է�����Ϣ��";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				//������������
				mreadThread = new readThread();
				mreadThread.start();
			} 
			catch (IOException e) 
			{
				Log.e("connect", "", e);
				Message msg = new Message();
				msg.obj = "���ӷ�����쳣���Ͽ�����������һ�ԡ�";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
			} 
		}
	};

	//����������
	private class ServerThread extends Thread { 
		public void run() {
					
			try {
				/* ����һ������������ 
				 * �����ֱ𣺷��������ơ�UUID	 */	
				mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
						UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));		
				
				Log.d("server", "wait cilent connect...");
				
				Message msg = new Message();
				msg.obj = "���Ժ����ڵȴ��ͻ��˵�����...";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				
				/* ���ܿͻ��˵��������� */
				socket = mserverSocket.accept();
				Log.d("server", "accept success !");
				
				Message msg2 = new Message();
				String info = "�ͻ����Ѿ������ϣ����Է�����Ϣ��";
				msg2.obj = info;
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg2);
				//������������
				mreadThread = new readThread();
				mreadThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	/* ֹͣ������ */
	private void shutdownServer() {
		new Thread() {
			public void run() {
				if(startServerThread != null)
				{
					startServerThread.interrupt();
					startServerThread = null;
				}
				if(mreadThread != null)
				{
					mreadThread.interrupt();
					mreadThread = null;
				}				
				try {					
					if(socket != null)
					{
						socket.close();
						socket = null;
					}
					if (mserverSocket != null)
					{
						mserverSocket.close();/* �رշ����� */
						mserverSocket = null;
					}
				} catch (IOException e) {
					Log.e("server", "mserverSocket.close()", e);
				}
			};
		}.start();
	}
	/* ֹͣ�ͻ������� */
	private void shutdownClient() {
		new Thread() {
			public void run() {
				if(clientConnectThread!=null)
				{
					clientConnectThread.interrupt();
					clientConnectThread= null;
				}
				if(mreadThread != null)
				{
					mreadThread.interrupt();
					mreadThread = null;
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket = null;
				}
			};
		}.start();
	}
	//��������
	private  void sendMessageHandle(String msg) 
	{		
		if (socket == null) 
		{
			Toast.makeText(mContext, "û������", Toast.LENGTH_SHORT).show();
			return;
		}
		try {				
			OutputStream os = socket.getOutputStream(); 
			os.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		list.add(new deviceListItem(msg, false));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(list.size() - 1);
	}
	//��ȡ����
    private class readThread extends Thread { 
        public void run() {
        	
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            
			try {
				mmInStream = socket.getInputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
            while (true) {
                try {
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer)) > 0 )
                    {
	                    byte[] buf_data = new byte[bytes];
				    	for(int i=0; i<bytes; i++)
				    	{
				    		buf_data[i] = buffer[i];
				    	}
						String s = new String(buf_data);
						Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                	try {
						mmInStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    break;
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Bluetooth.serviceOrCilent == ServerOrCilent.CILENT) 
		{
        	shutdownClient();
		}
		else if (Bluetooth.serviceOrCilent == ServerOrCilent.SERVICE) 
		{
			shutdownServer();
		}
        Bluetooth.isOpen = false;
		Bluetooth.serviceOrCilent = ServerOrCilent.NONE;
    }
	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}

	
	public class deviceListItem {
		String message;
		boolean isSiri;

		public deviceListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
}