package org.rapla.client.gwt.view;

import org.rapla.client.CalendarContainer;
import org.rapla.client.RaplaWidget;
import org.rapla.client.internal.PresenterChangeCallback;
import org.rapla.facade.ModificationEvent;
import org.rapla.framework.RaplaException;
import org.rapla.inject.DefaultImplementation;
import org.rapla.inject.InjectionContext;

import javax.inject.Inject;

@DefaultImplementation(of=CalendarContainer.class,context = InjectionContext.gwt)
public class CalendarContainerGwt implements CalendarContainer
{
    @Inject
    public CalendarContainerGwt()
    {
    }

    @Override public void scrollToStart()
    {

    }

    @Override public void closeFilterButton()
    {

    }

    @Override public void update()
    {

    }

    @Override public void update(ModificationEvent evt) throws RaplaException
    {

    }

    @Override public void init(boolean editable, PresenterChangeCallback callback) throws RaplaException
    {

    }

    @Override public RaplaWidget provideContent()
    {
        return null;
    }
}
