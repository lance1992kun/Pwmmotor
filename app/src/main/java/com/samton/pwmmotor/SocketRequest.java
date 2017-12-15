package com.samton.pwmmotor;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/07/11
 *     desc   : Socket请求线程
 *     version: 1.0
 * </pre>
 */

public class SocketRequest extends Thread {
    /**
     * 服务器地址
     */
    private String mServerHost = "192.168.8.100";
    /**
     * 服务端口号
     */
    private int mServerPort = 50000;
//    /**
//     * 远程连接
//     */
//     private SocketAddress socketAddress = null;
    /**
     * 服务器读写对象
     */
    private Socket socket = null;
    /**
     * 是否停止接受消息
     */
    private boolean isStop = false;
    /**
     * 输出流
     */
    private DataOutputStream dos = null;
    /**
     * 输入流
     */
    private DataInputStream dis = null;
    /**
     * 回调
     */
    private IResultCallBack mCallBack = null;

    /**
     * 设置回调
     *
     * @param mCallBack 回调
     */
    public void setCallBack(IResultCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    /**
     * 构造函数
     *
     * @param mServerHost IP地址
     * @param mServerPort 端口
     */
    public SocketRequest(String mServerHost, int mServerPort) {
        this.mServerHost = mServerHost;
        this.mServerPort = mServerPort;
        // 初始化连接
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 服务器远程对象
        SocketAddress socketAddress = new InetSocketAddress(mServerHost, mServerPort);
        // 生成Socket对象
        socket = new Socket();
        try {
            // 设置目标地址,请求超时限制
            socket.connect(socketAddress, 5 * 1000);
            // 判断是否连接成功
            if (socket.isConnected()) {
                // 发送消息
                dos = new DataOutputStream(socket.getOutputStream());
                // 接收服务器消息
                dis = new DataInputStream(socket.getInputStream());
                // 开启线程获取返回值
                this.start();
            } else {
                LogUtils.e("未能成功连接至服务器！");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                LogUtils.e("连接超时!");
            } else {
                LogUtils.e("通讯过程发生异常:" + e.toString());
            }
        }
    }


    /**
     * 发送指令
     *
     * @param command 命令执行代码
     */
    public void sendCommand(String command) {
        // 组合命令
        String tempCommand = "!message:" + command;
        LogUtils.e("发送的指令>>>>" + tempCommand);
        if (socket.isConnected()) {
            try {
                // 服务区/客户端双方的写/读方式要一直,否则会报错
                dos.writeUTF(tempCommand);
                // 刷新输出流，使Server马上收到该字符串
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        // 不间断循环一直获取返回信息
        while (!isStop && socket.isConnected()) {
            try {
                Thread.sleep(300);
                // 回写回调
                String result = dis.readLine().replaceAll("!message:", "");
                JSONObject jsonObject = new JSONObject(result);
                String type = jsonObject.optString("Type");
                if (!type.equals("PosMessage")) {
                    mCallBack.onResult(result);
                }
            } catch (Exception e) {
                // 出现异常后回调
                mCallBack.onError(e.getMessage());
            }
        }
    }

    /**
     * 摧毁一系列数据
     */
    public void destory() {
        try {
            // 关闭连接
            socket.close();
            // 关闭流
            dis.close();
            dos.close();
            // 不在接受底盘的返回值
            isStop = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调接口
     */
    public interface IResultCallBack {
        /**
         * 回写信息
         *
         * @param result 信息
         */
        void onResult(String result);

        /**
         * 错误信息
         *
         * @param errorMsg 错误信息
         */
        void onError(String errorMsg);
    }
}
