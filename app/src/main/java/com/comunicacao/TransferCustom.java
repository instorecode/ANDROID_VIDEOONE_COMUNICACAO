package com.comunicacao;

import android.util.Log;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class TransferCustom implements FTPDataTransferListener {
    public int size = 0;
    @Override
    public void started() {
        Log.e("LOG::DOWNLOAD", "STARTED");
    }

    @Override
    public void transferred(int i) {
        size += i;
    }

    @Override
    public void completed() {
        Log.e("LOG::DOWNLOAD", "COMPLETED " + size);
    }

    @Override
    public void aborted() {
        Log.e("LOG::DOWNLOAD", "ABORTED");
    }

    @Override
    public void failed() {
        Log.e("LOG::DOWNLOAD", "FAILED");
    }
}
