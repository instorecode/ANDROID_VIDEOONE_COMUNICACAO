package com.comunicacao;

import android.util.Log;

import com.utils.RegistrarLog;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class TransferCustom implements FTPDataTransferListener {
    public int size = 0;
    @Override
    public void started() {
        RegistrarLog.imprimirMsg("LOG::DOWNLOAD", "STARTED");
    }

    @Override
    public void transferred(int i) {
        size += i;
    }

    @Override
    public void completed() {
        RegistrarLog.imprimirMsg("LOG::DOWNLOAD", "COMPLETED " + size);
    }

    @Override
    public void aborted() {
        RegistrarLog.imprimirMsg("LOG::DOWNLOAD", "ABORTED");
    }

    @Override
    public void failed() {
        RegistrarLog.imprimirMsg("LOG::DOWNLOAD", "FAILED");
    }
}
