package com.jeffmony.videocache.proxy;

import com.jeffmony.videocache.socket.SocketProcessTask;
import com.jeffmony.videocache.utils.LogUtils;
import com.jeffmony.videocache.utils.ProxyCacheUtils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jeffmony
 * 本地代理服务端类
 */

public class LocalProxyVideoServer {

    private static final String TAG = "LocalProxyCacheServer";

    private final ExecutorService mSocketPool = Executors.newFixedThreadPool(16);

    private ServerSocket mLocalServer;
    private Thread mRequestThread;
    private int mPort;
    public LocalProxyListener mListener;

    public LocalProxyVideoServer() {
        try {
            InetAddress address = InetAddress.getByName(ProxyCacheUtils.LOCAL_PROXY_HOST);
            mLocalServer = new ServerSocket(0, 8, address);
            LogUtils.w(TAG, "LocalProxyVideoServer  init: " +mLocalServer);
            mPort = mLocalServer.getLocalPort();
            ProxyCacheUtils.getConfig().setPort(mPort);
            ProxyCacheUtils.setLocalPort(mPort);
            CountDownLatch startSignal = new CountDownLatch(1);
            WaitSocketRequestsTask task = new WaitSocketRequestsTask(startSignal);
            mRequestThread = new Thread(task);
            mRequestThread.setName("LocalProxyServerThread");
            mRequestThread.start();
            startSignal.await();
        } catch (Exception e) {
            if (mListener != null) {
                mListener.onFailed(e);
            }
            shutdown();
            LogUtils.w(TAG,"Cannot create serverSocket, exception=" + e);
        }
    }

    public void setLocalProxyListener(LocalProxyListener listener) {
        mListener = listener;
    }

    public interface LocalProxyListener {
        void onFailed(Exception e);
    }

    private class WaitSocketRequestsTask implements Runnable {

        private CountDownLatch mLatch;

        public WaitSocketRequestsTask(CountDownLatch latch) {
            mLatch = latch;
        }

        @Override
        public void run() {
            mLatch.countDown();
            initSocketProcessor();
        }
    }

    private void initSocketProcessor() {
        do {
            try {
                Socket socket = mLocalServer.accept();
                socket.setSoTimeout(5000);
                mSocketPool.submit(new SocketProcessTask(socket, mListener));
            } catch (Exception e) {
                LogUtils.w(TAG, "WaitRequestsRun ServerSocket accept failed, exception=" + e);
                if (mListener != null && e instanceof SocketTimeoutException) {
                    mListener.onFailed(e);
                }
            }
        } while (mRequestThread.isAlive() && !mLocalServer.isClosed());
    }

    public void shutdown() {
        if (mLocalServer != null) {
            if (mRequestThread != null && mRequestThread.isAlive()) {
                mRequestThread.interrupt();
            }
            try {
                mLocalServer.close();
            } catch (Exception e) {
                if (e != null) {
                    LogUtils.w(TAG, "ServerSocket close failed, exception=" + e.getMessage());
                }
            } finally {
                mSocketPool.shutdownNow();
            }
        }
    }
}
