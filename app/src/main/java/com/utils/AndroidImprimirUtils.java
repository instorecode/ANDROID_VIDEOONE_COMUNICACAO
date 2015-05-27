package com.utils;

import android.util.Log;

import com.br.instore.utils.ConfiguaracaoUtils;
import com.br.instore.utils.LogUtils;

public class AndroidImprimirUtils {

    public static <CLASS_OBJECT> void imprimirErro(CLASS_OBJECT o, Throwable throwable) {
        for (StackTraceElement ste : throwable.getStackTrace()) {
            if (ste.getClassName().equals(o.getClass().getName())) {
                Log.e("Log", "ERROR::MSG " + throwable.getMessage());
                Log.e("Log", "ERROR::CLASS " + ste.getClassName());
                Log.e("Log", "ERROR::METHOD " + ste.getMethodName());
                Log.e("Log", "ERROR::LINE " + ste.getLineNumber());
                Log.e("Log", "ERROR::FILE " + ste.getFileName());
                Log.e("Log", "-----------------------------------------------------");
                break;
            }
        }
    }

    public static void imprimirErro(Class<?> klass, Throwable throwable) {
        for (StackTraceElement ste : throwable.getStackTrace()) {
            if (ste.getClassName().equals(klass.getName())) {
                Log.e("Log", "ERROR::MSG " + throwable.getMessage());
                Log.e("Log", "ERROR::CLASS " + ste.getClassName());
                Log.e("Log", "ERROR::METHOD " + ste.getMethodName());
                Log.e("Log", "ERROR::LINE " + ste.getLineNumber());
                Log.e("Log", "ERROR::FILE " + ste.getFileName());
                Log.e("Log", "-----------------------------------------------------");
                break;
            }
        }
    }

    public static void imprimirErro(Class<?> klass, Throwable throwable, int flag) {
        for (StackTraceElement ste : throwable.getStackTrace()) {
            if (ste.getClassName().equals(klass.getName())) {
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 ERROR::MSG " + throwable.getMessage());
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 ERROR::MSG " + throwable.getMessage());
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 ERROR::CLASS " + ste.getClassName());
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 ERROR::METHOD " + ste.getMethodName());
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 ERROR::LINE " + ste.getLineNumber());
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 ERROR::FILE " + ste.getFileName());
                LogUtils.registrar(flag, ConfiguaracaoUtils.diretorio.isLogCompleto(), " 90 -----------------------------------------------------");
                break;
            }
        }
    }
}
