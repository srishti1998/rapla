package org.rapla.client.dialog;

import org.rapla.framework.Disposable;
import org.rapla.scheduler.Promise;

public interface DialogInterface
{
    Promise<Integer> start(boolean pack);
    int getSelectedIndex();
    void setTitle(String createTitle);
    void setIcon(String iconKey);
    void setSize(int width, int height);
    void close();
    boolean isVisible();
    void setPosition(double x, double y);
    DialogAction getAction(int commandIndex);
    void setAbortAction(Runnable abortAction);
    void setDefault(int commandIndex);
    void addWindowListener(Disposable disposable);
    void requestFocus();
    void toFront();
    
    interface DialogAction
    {
        void setEnabled(boolean enabled);
        void setRunnable(Runnable runnable);
        void setIcon(String iconKey);
        void execute();
    }



}
