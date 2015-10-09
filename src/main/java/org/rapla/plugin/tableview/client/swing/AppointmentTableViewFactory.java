/*--------------------------------------------------------------------------*
 | Copyright (C) 2014 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.tableview.client.swing;

import javax.inject.Inject;
import javax.swing.Icon;

import org.rapla.client.extensionpoints.ObjectMenuFactory;
import org.rapla.facade.CalendarModel;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.SwingCalendarView;
import org.rapla.client.swing.extensionpoints.SwingViewFactory;
import org.rapla.gui.images.RaplaImages;
import org.rapla.inject.Extension;
import org.rapla.plugin.tableview.TableViewPlugin;
import org.rapla.plugin.tableview.client.swing.extensionpoints.AppointmentSummaryExtension;

import java.util.Set;

@Extension(provides = SwingViewFactory.class, id = TableViewPlugin.TABLE_APPOINTMENTS_VIEW)
public class AppointmentTableViewFactory extends RaplaComponent implements SwingViewFactory
{
    private final Set<AppointmentSummaryExtension> appointmentSummaryExtensions;
    private final Set<ObjectMenuFactory> objectMenuFactories;
    @Inject
    public AppointmentTableViewFactory(RaplaContext context,
            Set<AppointmentSummaryExtension> appointmentSummaryExtensions, Set<ObjectMenuFactory> objectMenuFactories)
    {
        super( context );
        this.appointmentSummaryExtensions = appointmentSummaryExtensions;
        this.objectMenuFactories = objectMenuFactories;
    }

    public final static String TABLE_VIEW =  "table_appointments";

    public SwingCalendarView createSwingView(RaplaContext context, CalendarModel model, boolean editable) throws RaplaException
    {
        return new SwingAppointmentTableView( context, model, appointmentSummaryExtensions, objectMenuFactories,editable);
    }

    public String getViewId()
    {
        return TABLE_VIEW;
    }

    public String getName()
    {
        return getString("appointments");
    }

    Icon icon;
    public Icon getIcon()
    {
        if ( icon == null) {
            icon = RaplaImages.getIcon("/org/rapla/plugin/tableview/images/table.png");
        }
        return icon;
    }

    public String getMenuSortKey() {
        return "0";
    }

}

