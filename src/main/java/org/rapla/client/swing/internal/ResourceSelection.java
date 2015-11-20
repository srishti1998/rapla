/*--------------------------------------------------------------------------*
 | Copyright (C) 2006  Christopher Kohlhaas                                 |
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

package org.rapla.client.swing.internal;

import org.rapla.RaplaResources;
import org.rapla.client.dialog.DialogUiFactoryInterface;
import org.rapla.client.swing.EditController;
import org.rapla.client.swing.InfoFactory;
import org.rapla.client.swing.MenuContext;
import org.rapla.client.swing.MenuFactory;
import org.rapla.client.swing.RaplaGUIComponent;
import org.rapla.client.swing.TreeFactory;
import org.rapla.client.swing.images.RaplaImages;
import org.rapla.client.swing.internal.FilterEditButton.FilterEditButtonFactory;
import org.rapla.client.swing.internal.action.RaplaObjectAction;
import org.rapla.client.swing.internal.common.MultiCalendarView;
import org.rapla.client.swing.internal.edit.ClassifiableFilterEdit;
import org.rapla.client.swing.internal.edit.fields.BooleanField.BooleanFieldFactory;
import org.rapla.client.swing.internal.edit.fields.DateField.DateFieldFactory;
import org.rapla.client.swing.internal.edit.fields.LongField.LongFieldFactory;
import org.rapla.client.swing.internal.edit.fields.TextField.TextFieldFactory;
import org.rapla.client.swing.internal.view.TreeFactoryImpl;
import org.rapla.client.swing.toolkit.PopupEvent;
import org.rapla.client.swing.toolkit.PopupListener;
import org.rapla.client.swing.toolkit.RaplaMenu;
import org.rapla.client.swing.toolkit.RaplaPopupMenu;
import org.rapla.client.swing.toolkit.RaplaTree;
import org.rapla.client.swing.toolkit.RaplaWidget;
import org.rapla.components.calendar.RaplaArrowButton;
import org.rapla.components.layout.TableLayout;
import org.rapla.entities.Entity;
import org.rapla.entities.RaplaObject;
import org.rapla.entities.RaplaType;
import org.rapla.entities.User;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Period;
import org.rapla.entities.domain.permission.PermissionController;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.CalendarSelectionModel;
import org.rapla.facade.ClientFacade;
import org.rapla.facade.ModificationEvent;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.framework.logger.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ResourceSelection extends RaplaGUIComponent implements RaplaWidget {
    protected JPanel content = new JPanel();
    public RaplaTree treeSelection = new RaplaTree();
    TableLayout tableLayout;
    protected JPanel buttonsPanel = new JPanel();

    protected final CalendarSelectionModel model;
    MultiCalendarView view;
    Listener listener = new Listener();
  
    protected FilterEditButton filterEdit;
    private final TreeFactory treeFactory;
    private final MenuFactory menuFactory;
    private final EditController editController;
    private final InfoFactory infoFactory;
    private final RaplaImages raplaImages;
    private final DialogUiFactoryInterface dialogUiFactory;
    private final PermissionController permissionController;
    private final RaplaMenuBarContainer menuBar;
	private ResourceSelection(RaplaMenuBarContainer menuBar,ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, MultiCalendarView view, CalendarSelectionModel model, TreeFactory treeFactory, MenuFactory menuFactory, EditController editController, InfoFactory infoFactory, RaplaImages raplaImages, DateFieldFactory dateFieldFactory, DialogUiFactoryInterface dialogUiFactory, BooleanFieldFactory booleanFieldFactory, PermissionController permissionController, TextFieldFactory textFieldFactory, LongFieldFactory longFieldFactory, FilterEditButtonFactory filterEditButtonFactory) throws RaplaException {
        super(facade, i18n, raplaLocale, logger);

        this.menuBar = menuBar;
        this.model = model;
        this.view = view;
        this.treeFactory = treeFactory;
        this.menuFactory = menuFactory;
        this.editController = editController;
        this.infoFactory = infoFactory;
        this.raplaImages = raplaImages;
        this.dialogUiFactory = dialogUiFactory;
        this.permissionController = permissionController;
        /*double[][] sizes = new double[][] { { TableLayout.FILL }, { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL } };
        tableLayout = new TableLayout(sizes);*/
        content.setLayout(new BorderLayout());

        content.add(treeSelection);
        // content.setPreferredSize(new Dimension(260,400));
        content.setBorder(BorderFactory.createRaisedBevelBorder());
        
        content.add(buttonsPanel, BorderLayout.NORTH);
        
        buttonsPanel.setLayout( new BorderLayout());
        filterEdit = filterEditButtonFactory.create(model,true,listener);
        buttonsPanel.add(filterEdit.getButton(), BorderLayout.EAST);
        
        treeSelection.setToolTipRenderer(getTreeFactory().createTreeToolTipRenderer());
        treeSelection.setMultiSelect(true);
        treeSelection.getTree().setSelectionModel(((TreeFactoryImpl) getTreeFactory()).createComplexTreeSelectionModel());

        updateTree();
        updateSelection();
        treeSelection.addChangeListener(listener);
        treeSelection.addPopupListener(listener);
        treeSelection.addDoubleclickListeners(listener);
        treeSelection.getTree().addFocusListener(listener);
        javax.swing.ToolTipManager.sharedInstance().registerComponent(treeSelection.getTree());

        updateMenu();
    }
	
    protected HashSet<?> setSelectedObjects(){
        HashSet<Object> elements = new HashSet<Object>(treeSelection.getSelectedElements());
        getModel().setSelectedObjects(elements);
        return elements;
    }
    
    public RaplaArrowButton getFilterButton() {
        return filterEdit.getButton();
    }
	
	public RaplaTree getTreeSelection() {
        return treeSelection;
    }

    protected CalendarSelectionModel getModel() {
        return model;
    }

    public void dataChanged(ModificationEvent evt) throws RaplaException {
    	if ( evt != null && evt.isModified())
    	{
    		 updateTree();
    		 updateMenu();
    	}
    	// No longer needed here as directly done in RaplaClientServiceImpl
    	// ((CalendarModelImpl) model).dataChanged( evt);
    }

    final protected TreeFactory getTreeFactory() {
        return treeFactory;
    }

    boolean treeListenersEnabled = true;

    /*
     * (non-Javadoc)
     * 
     * @see org.rapla.client.swing.gui.internal.view.ITreeFactory#createClassifiableModel(org.rapla.entities.dynamictype.Classifiable[], org.rapla.entities.dynamictype.DynamicType[])
     */
    protected void updateTree() throws RaplaException {

        treeSelection.getTree().setRootVisible(false);
        treeSelection.getTree().setShowsRootHandles(true);
        treeSelection.getTree().setCellRenderer(((TreeFactoryImpl) getTreeFactory()).createRenderer());
        
        DefaultTreeModel treeModel = generateTree();
        try {
            treeListenersEnabled = false;
            treeSelection.exchangeTreeModel(treeModel);
            updateSelection();
        } finally {
            treeListenersEnabled = true;
        }

    }

    protected DefaultTreeModel generateTree() throws RaplaException {
        ClassificationFilter[] filter = getModel().getAllocatableFilter();
        final TreeFactoryImpl treeFactoryImpl = (TreeFactoryImpl) getTreeFactory();
        DefaultTreeModel treeModel = treeFactoryImpl.createModel(filter);
        return treeModel;
    }

    protected void updateSelection() {
        Collection<Object> selectedObjects = new ArrayList<Object>(getModel().getSelectedObjects());
        treeSelection.select(selectedObjects);
    }

    public JComponent getComponent() {
        return content;
    }

    
    protected MenuContext createMenuContext(Point p, Object obj) {
        MenuContext menuContext = new MenuContext( obj, new SwingPopupContext(getComponent(), p));
        return menuContext;
    }
    
    protected void showTreePopup(PopupEvent evt) {
        try {

            Point p = evt.getPoint();
            Object obj = evt.getSelectedObject();
            List<?> list = treeSelection.getSelectedElements();

            MenuContext menuContext = createMenuContext(p, obj);
            menuContext.setSelectedObjects(list);
            

            RaplaPopupMenu menu = new RaplaPopupMenu();

            RaplaMenu newMenu = new RaplaMenu("new");
            newMenu.setText(getString("new"));
            boolean addNewReservationMenu = obj instanceof Allocatable || obj instanceof DynamicType;
            ((MenuFactoryImpl) getMenuFactory()).addNew(newMenu, menuContext, null, addNewReservationMenu);

            getMenuFactory().addObjectMenu(menu, menuContext, "EDIT_BEGIN");
            newMenu.setEnabled( newMenu.getMenuComponentCount() > 0);
            menu.insertAfterId(newMenu, "EDIT_BEGIN");

            JComponent component = (JComponent) evt.getSource();

            menu.show(component, p.x, p.y);
        } catch (RaplaException ex) {
            dialogUiFactory.showException(ex, new SwingPopupContext(getComponent(), null));
        }
    }
    
    class Listener implements PopupListener, ChangeListener, ActionListener, FocusListener {

        public void showPopup(PopupEvent evt) {
            showTreePopup(evt);
        }
     
        public void actionPerformed(ActionEvent evt) {
            Object focusedObject = evt.getSource();
            if ( focusedObject == null || !(focusedObject instanceof RaplaObject))
            	return;
            // System.out.println(focusedObject.toString());
            RaplaType type = ((RaplaObject) focusedObject).getRaplaType();
            if (    type == User.TYPE
                 || type == Allocatable.TYPE
                 || type ==Period.TYPE 
               )
            {
            	
                RaplaObjectAction editAction = new RaplaObjectAction(getClientFacade(), getI18n(), getRaplaLocale(), getLogger(),
                        createPopupContext(getComponent(), null), editController, infoFactory, raplaImages, dialogUiFactory, permissionController);
                if (permissionController.canModify( focusedObject, getClientFacade()))
                {
                    editAction.setEdit((Entity<?>)focusedObject);
                    editAction.actionPerformed();
                }
            }
        }

        public void stateChanged(ChangeEvent evt) {
            if (!treeListenersEnabled) {
                return;
            }
            try {
            	Object source = evt.getSource();
				ClassifiableFilterEdit filterUI = filterEdit.getFilterUI();
				if ( filterUI != null && source == filterUI)
            	{
            		final ClassificationFilter[] filters = filterUI.getFilters();
            		model.setAllocatableFilter( filters);
            		updateTree();
            		applyFilter();
            	}
            	else if ( source == treeSelection)
                {
            		updateChange();
            	}
            } catch (Exception ex) {
                dialogUiFactory.showException(ex, new SwingPopupContext(getComponent(), null));
            }
        }
        
       
		public void focusGained(FocusEvent e) {
			try {
				if (!getUserModule().isSessionActive())
				{
					return;
				}
				updateMenu();
			} catch (Exception ex) {
			    dialogUiFactory.showException(ex, new SwingPopupContext(getComponent(), null));
            }
		}

		public void focusLost(FocusEvent e) {
		}


    }

    public void updateChange() throws RaplaException {
        setSelectedObjects();
        updateMenu();
        applyFilter();
    }
    
    public void applyFilter() throws RaplaException {
        view.getSelectedCalendar().update();
    }

   
    
    public void updateMenu() throws RaplaException {
        RaplaMenu editMenu = menuBar.getEditMenu();
        RaplaMenu newMenu = menuBar.getNewMenu();

        editMenu.removeAllBetween("EDIT_BEGIN", "EDIT_END");
        newMenu.removeAll();

        List<?> list = treeSelection.getSelectedElements();
        Object focusedObject = null;
        if (list.size() == 1) {
            focusedObject = treeSelection.getSelectedElement();
        }

        MenuContext menuContext = createMenuContext( null,  focusedObject);
        menuContext.setSelectedObjects(list);
        if ( treeSelection.getTree().hasFocus())
        {
        	getMenuFactory().addObjectMenu(editMenu, menuContext, "EDIT_BEGIN");
        }
        ((MenuFactoryImpl) getMenuFactory()).addNew(newMenu, menuContext, null, true);
        newMenu.setEnabled(newMenu.getMenuComponentCount() > 0 );
    }

    public MenuFactory getMenuFactory() {
        return menuFactory;
    }
    
    @Singleton
    public static class ResourceSelectionFactory
    {
        private final ClientFacade facade;
        private final RaplaResources i18n;
        private final RaplaLocale raplaLocale;
        private final Logger logger;
        private final CalendarSelectionModel model;
        private final TreeFactory treeFactory;
        private final MenuFactory menuFactory;
        private final EditController editController;
        private final InfoFactory infoFactory;
        private final RaplaImages raplaImages;
        private final DateFieldFactory dateFieldFactory;
        private final DialogUiFactoryInterface dialogUiFactory;
        private final BooleanFieldFactory booleanFieldFactory;
        private final PermissionController permissionController;
        private final TextFieldFactory textFieldFactory;
        private final LongFieldFactory longFieldFactory;
        private final FilterEditButtonFactory filterEditButtonFactory;
        private final RaplaMenuBarContainer menuBar;

        @Inject
        public ResourceSelectionFactory(ClientFacade facade, RaplaResources i18n, RaplaLocale raplaLocale, Logger logger, CalendarSelectionModel model,
                TreeFactory treeFactory, MenuFactory menuFactory, EditController editController, InfoFactory infoFactory,
                RaplaImages raplaImages, DateFieldFactory dateFieldFactory, DialogUiFactoryInterface dialogUiFactory, BooleanFieldFactory booleanFieldFactory,
                PermissionController permissionController, TextFieldFactory textFieldFactory, LongFieldFactory longFieldFactory,
                FilterEditButtonFactory filterEditButtonFactory, RaplaMenuBarContainer menuBar)
        {
            this.facade = facade;
            this.i18n = i18n;
            this.raplaLocale = raplaLocale;
            this.logger = logger;
            this.model = model;
            this.treeFactory = treeFactory;
            this.menuFactory = menuFactory;
            this.editController = editController;
            this.infoFactory = infoFactory;
            this.raplaImages = raplaImages;
            this.dateFieldFactory = dateFieldFactory;
            this.dialogUiFactory = dialogUiFactory;
            this.booleanFieldFactory = booleanFieldFactory;
            this.permissionController = permissionController;
            this.textFieldFactory = textFieldFactory;
            this.longFieldFactory = longFieldFactory;
            this.filterEditButtonFactory = filterEditButtonFactory;
            this.menuBar = menuBar;
        }

        public ResourceSelection create(MultiCalendarView view)
        {
            return new ResourceSelection(menuBar,facade, i18n, raplaLocale, logger, view, model, treeFactory, menuFactory, editController, infoFactory, raplaImages,
                    dateFieldFactory, dialogUiFactory, booleanFieldFactory, permissionController, textFieldFactory, longFieldFactory, filterEditButtonFactory);
        }
    }

}
