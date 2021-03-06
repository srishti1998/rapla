package org.rapla.client.gwt.view;

import io.reactivex.functions.Consumer;
import org.rapla.client.RaplaWidget;
import org.rapla.client.internal.edit.EditTaskPresenter;
import org.rapla.entities.Entity;
import org.rapla.framework.RaplaException;
import org.rapla.inject.DefaultImplementation;
import org.rapla.inject.InjectionContext;

import javax.inject.Inject;
import java.util.Collection;

@DefaultImplementation(of= EditTaskPresenter.EditTaskView.class,context = InjectionContext.gwt)
public class EditTaskViewGwt implements EditTaskPresenter.EditTaskView
{
    @Inject
    public EditTaskViewGwt()
    {

    }

    @Override public <T extends Entity> RaplaWidget doSomething(Collection<T> toEdit,  Consumer<Collection<T>> save, Runnable close, boolean isMerge)
            throws RaplaException
    {
        return null;
    }


}
