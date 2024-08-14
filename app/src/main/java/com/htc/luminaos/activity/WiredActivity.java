package com.htc.luminaos.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityWiredBinding;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.widget.StaticConfigDialog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class WiredActivity extends BaseActivity {

    private ActivityWiredBinding wiredNetworkLayoutBinding;
    ConnectivityManager connectivityManager;
    private EthernetManager mEthManager = null;
    private final static String nullIpInfo = "0.0.0.0";
    private IntentFilter mIntentFilter;

    private boolean showPre = true;
    String  mInterfaceName ="";
    IpConfiguration mIpConfiguration;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                Log.v("test", "===" + info.toString());
                if (null != info && ConnectivityManager.TYPE_ETHERNET == info.getType()) {
                    if (NetworkInfo.State.CONNECTED == info.getState()) {
                        wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.connected));
                        initNetworkInfo();

                    } else if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                        wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.not_connected));
                        resetNetworkInfo();

                    }
                }
            }
        }
    };



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_ip_setting:
                if (mIpConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC) {
                    mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
                    mIpConfiguration.setStaticIpConfiguration(null);
                    mEthManager.setConfiguration(mInterfaceName,mIpConfiguration);
                }else {
                    mIpConfiguration.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
                    mEthManager.setConfiguration(mInterfaceName,mIpConfiguration);
                }
                updateEthernetStatus();
                break;
            case R.id.rl_ip_address:
            case R.id.rl_gateway:
            case R.id.rl_dns:
            case R.id.rl_dns2:
            case R.id.rl_subnet_mask:
                StaticConfigDialog staticConfigDialog = new StaticConfigDialog(this,R.style.DialogTheme);
                staticConfigDialog.setIpConfiguration(mIpConfiguration);
                staticConfigDialog.show();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wiredNetworkLayoutBinding = ActivityWiredBinding.inflate(LayoutInflater.from(this));
        setContentView(wiredNetworkLayoutBinding.getRoot());
        mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
        connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        initView();
    }

    private void initView(){
        wiredNetworkLayoutBinding.rlIpSetting.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlDns.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlDns2.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlGateway.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlIpAddress.setOnClickListener(this);
        wiredNetworkLayoutBinding.rlSubnetMask.setOnClickListener(this);

        wiredNetworkLayoutBinding.rlIpSetting.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlDns.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlDns2.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlGateway.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlIpAddress.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlSubnetMask.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlWired.setOnHoverListener(this);
        wiredNetworkLayoutBinding.rlIpSetting.setVisibility(MyApplication.config.ipSetting?View.VISIBLE:View.GONE);
    }

    @Override
    protected void onResume() {
        loadIpConfiguration();
        updateEthernetStatus();
        if (isNetworkConnect()){
            wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.connected));
            initNetworkInfo();
        }else {
            wiredNetworkLayoutBinding.wiredTv.setText(getString(R.string.not_connected));
            resetNetworkInfo();
        }
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mIntentFilter);
        super.onResume();
    }

    public void loadIpConfiguration() {
        String[] ifaces = mEthManager.getAvailableInterfaces();
        if (ifaces.length > 0) {
            mInterfaceName = ifaces[0];
            mIpConfiguration = mEthManager.getConfiguration(mInterfaceName);

        }
    }

    private boolean isNetworkConnect(){
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return networkInfo!=null&& networkInfo.isConnected();
    }

    private void updateEthernetStatus(){
        if (mIpConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.STATIC) {
            //静态
            wiredNetworkLayoutBinding.ipSettingTv.setText(getString(R.string.static_ip));
            wiredNetworkLayoutBinding.rlIpAddress.setEnabled(true);
            wiredNetworkLayoutBinding.rlGateway.setEnabled(true);
            wiredNetworkLayoutBinding.rlSubnetMask.setEnabled(true);
            wiredNetworkLayoutBinding.rlDns.setEnabled(true);

        }else if (mIpConfiguration.getIpAssignment() == IpConfiguration.IpAssignment.DHCP){
            wiredNetworkLayoutBinding.ipSettingTv.setText(getString(R.string.dhcp));
            wiredNetworkLayoutBinding.rlIpAddress.setEnabled(false);
            wiredNetworkLayoutBinding.rlGateway.setEnabled(false);
            wiredNetworkLayoutBinding.rlSubnetMask.setEnabled(false);
            wiredNetworkLayoutBinding.rlDns.setEnabled(false);
        }
    }

    private void resetNetworkInfo(){
        wiredNetworkLayoutBinding.macTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.subnetMaskTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.dnsTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.dns2Tv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.gatewayTv.setText("0.0.0.0");
        wiredNetworkLayoutBinding.ipAddressTv.setText("0.0.0.0");
    }


    private void initNetworkInfo(){

        /*Network network= getFirstEthernet();
        if (network==null)
            return;
        String ipAddresses = formatIpAddresses(network);*/


        //wiredNetworkLayoutBinding.ipAddress.setText(ipAddresses);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String mac = networkInfo.getExtraInfo();
        wiredNetworkLayoutBinding.macTv.setText(mac);


        List<LinkAddress> linkAddresses = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getLinkAddresses();
        //获取当前连接的网络ip地址信息
        if(linkAddresses != null && !linkAddresses.isEmpty()){
            //注意：同时可以查看到两个网口的信息，但是ip地址不是固定的位置（即下标）
            //所以遍历的时候需要判断一下当前获取的ip地址是否符合ip地址的正则表达式
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < linkAddresses.size(); i++) {
                InetAddress address = linkAddresses.get(i).getAddress();
                LogUtils.d("ip地址"+address.getHostAddress());
                //判断ip地址的正则表达
               /* if(isCorrectIp(address.getHostAddress())){
                    wiredNetworkLayoutBinding.ipAddress.setText(address.getHostAddress());
                }*/
                builder.append(address.getHostAddress());
                if (i!=linkAddresses.size()-1){
                    builder.append("\n");
                }
            }

            wiredNetworkLayoutBinding.ipAddressTv.setText(builder.toString());
        }

        List<RouteInfo> routes = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getRoutes();
        if(routes != null && !routes.isEmpty()){
            for (int i = 0; i < routes.size(); i++) {
                //和ip地址一样，需要判断获取的网址符不符合正则表达式
                String hostAddress = routes.get(i).getGateway().getHostAddress();
                if(isCorrectIp(hostAddress)){
                    LogUtils.d("网关信息：" + hostAddress);
                    wiredNetworkLayoutBinding.gatewayTv.setText(hostAddress.replace("/",""));
                }
            }
        }


        List<InetAddress> dnsServers = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getDnsServers();
        if(dnsServers != null && dnsServers.size() >= 2){
            LogUtils.d("dns1 " + dnsServers.get(0).toString());
            LogUtils.d("dns2 " + dnsServers.get(1).toString());
            wiredNetworkLayoutBinding.dnsTv.setText(dnsServers.get(0).getHostAddress());
            wiredNetworkLayoutBinding.dns2Tv.setText(dnsServers.get(1).getHostAddress());
        }
        String mask = getIpAddressMaskForInterfaces(connectivityManager.getLinkProperties(
                connectivityManager.getActiveNetwork()).getInterfaceName());
        wiredNetworkLayoutBinding.subnetMaskTv.setText(mask);
    }


    /**
     * 获取子网掩码
     * @param interfaceName
     * @return
     */
    public static String getIpAddressMaskForInterfaces(String interfaceName) {
        //"eth0"
        try {
            //获取本机所有的网络接口
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            //判断 Enumeration 对象中是否还有数据
            while (networkInterfaceEnumeration.hasMoreElements()) {
                //获取 Enumeration 对象中的下一个数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) {
                    //判断网口是否在使用，判断是否时我们获取的网口
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {
                        //仅仅处理ipv6
                        //获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                        return String.valueOf(calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength()));
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();

        }

        return "0.0.0.0";
    }

    //通过子网掩码的位数计算子网掩码
    public static String calcMaskByPrefixLength(int length) {

        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }


    public static boolean isCorrectIp(String ip) {
        if (ip == null || "".equals(ip))
            return false;
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        return ip.matches(regex);
    }

    private Network getFirstEthernet() {
        final Network[] networks = connectivityManager.getAllNetworks();
        for (final Network network : networks) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return network;
            }
        }
        return null;
    }

    private String formatIpAddresses(Network network) {
        final LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
        if (linkProperties == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean gotAddress = false;
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (gotAddress) {
                sb.append("\n");
            }
            sb.append(linkAddress.getAddress().getHostAddress());
            gotAddress = true;
        }
        if (gotAddress) {
            return sb.toString();
        } else {
            return null;
        }
    }

    @Override
    protected void onStop() {
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        super.onStop();
    }
}