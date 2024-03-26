package com.spilab.monact.threading;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Clase de utilidad para ejecutar en diversos threads de forma sencilla.
 */
public class ThreadChooser {

    /**
     * Clase para ejecutar en el thread de UI
     */
    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    /**
     * Utilidad de sincronización para singleton
     */
    private static final Object LOCK = new Object();

    /**
     * Instancia de singleton
     */
    private static ThreadChooser instance = null;

    /**
     * Thread de I/O a disco.
     */
    private final Executor diskIO;

    /**
     * Thread principal (de UI).
     */
    private final Executor mainThread;

    /**
     * Thread de I/O de red.
     */
    private final Executor networkIO;

    private ThreadChooser(Executor diskIO, Executor mainThread, Executor networkIO) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;
    }

    public static ThreadChooser getInstance(){
        synchronized (LOCK){
            if (instance == null){
                instance = new ThreadChooser(Executors.newFixedThreadPool(3),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return instance;
    }

    public Executor getDiskThread() {
        return diskIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkThread() {
        return networkIO;
    }
}
