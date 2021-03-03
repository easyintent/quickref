package io.github.easyintent.quickref;

import android.os.Handler;

import java.util.concurrent.ExecutorService;


public interface ExecutorProvider {
    ExecutorService getBackgroundExecutor();
    Handler getUiExecutor();
}
