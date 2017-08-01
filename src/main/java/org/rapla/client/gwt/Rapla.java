package org.rapla.client.gwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import org.rapla.client.gwt.view.RaplaPopups;

public class Rapla implements EntryPoint
{

    public void onModuleLoad()
    {
        RaplaPopups.getProgressBar().setPercent(10);
        //GwtStarter starter = org.rapla.client.gwt.dagger.DaggerRaplaGwtComponent.create().getGwtStarter();
        GwtStarter starter = GWT.create(GwtStarter.class);
        starter.startApplication();
    }

}