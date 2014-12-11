package com.ds.bluetoothUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
	
/**
 * ����ͨѶ�߳�
 * @author GuoDong
 *
 */
public class BluetoothCommunThread extends Thread {

	private Handler serviceHandler;		//��Serviceͨ�ŵ�Handler
	private BluetoothSocket socket;
	private InputStream inStream;		//����������
	private OutputStream outStream;	//���������
	public volatile boolean isRun = true;	//���б�־λ
	
	/**
	 * ���캯��
	 * @param handler ���ڽ�����Ϣ
	 * @param socket
	 */
	public BluetoothCommunThread(Handler handler, BluetoothSocket socket) {
		this.serviceHandler = handler;
		this.socket = socket;
		try {
			this.outStream = socket.getOutputStream();
			this.inStream = socket.getInputStream();
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//��������ʧ����Ϣ
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			byte[] buffer = new byte[1024];
			int bytes;
			if (!isRun) {
				break;
			}
			try {
				bytes =inStream.read(buffer);
				//���ͳɹ���ȡ���������Ϣ����Ϣ��obj����Ϊ��ȡ���Ķ���
				Message msg = serviceHandler.obtainMessage(2,bytes,-1,buffer);
				msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
				msg.sendToTarget();
			} catch (Exception ex) {
				//��������ʧ����Ϣ
				serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
				ex.printStackTrace();
				return;
			}
		}
		
		//�ر���
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * д��һ�������л��Ķ���
	 * @param obj
	 */
	public void write(String obj) {
		byte[] buffer =obj.getBytes();
        try {
            outStream.write(buffer);
        } catch (IOException e) {
            ;
        }
		
	}

}
