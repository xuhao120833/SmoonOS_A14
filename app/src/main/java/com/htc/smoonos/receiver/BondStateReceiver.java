package com.htc.smoonos.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author 作者：zgr
 * @version 创建时间：2017年3月27日 下午4:01:54 类说明 蓝牙配对状态广播
 */
public class BondStateReceiver extends BroadcastReceiver {

	private String TAG = "BondStateReceiver";
	
	private BondStateCallBack mcallback;
	
	public BondStateReceiver(BondStateCallBack callback){
		this.mcallback=callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//1:配对成功    2：正在配对   3：删除配对
		String action = intent.getAction();
		if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			String name = device.getName();
			Log.d(TAG, "device name: " + name);
			int state = intent
					.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
			switch (state) {
			case BluetoothDevice.BOND_NONE://取消配对/未配对
				Log.d(TAG, "BOND_NONE 删除配对  配对失败");
				mcallback.bondState(3, device);
				break;
			case BluetoothDevice.BOND_BONDING://正在配对
				Log.d(TAG, "BOND_BONDING 正在配对");
				mcallback.bondState(2, device);
				break;
			case BluetoothDevice.BOND_BONDED://配对结束
				Log.d(TAG, "BOND_BONDED 配对成功");
				mcallback.bondState(1, device);
				break;
			}
		}
	}

}
