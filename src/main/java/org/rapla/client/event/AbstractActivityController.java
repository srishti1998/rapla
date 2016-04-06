package org.rapla.client.event;

import com.google.web.bindery.event.shared.EventBus;
import org.rapla.client.event.PlaceChangedEvent.PlaceChangedEventHandler;
import org.rapla.client.swing.toolkit.RaplaWidget;
import org.rapla.facade.ModificationEvent;
import org.rapla.framework.RaplaException;
import org.rapla.framework.logger.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractActivityController implements PlaceChangedEventHandler, Activity.ActivityEventHandler
{
    protected Place place;
    protected final Set<Activity> activities = new LinkedHashSet<Activity>();
    protected final Logger logger;
    private final Map<String, ActivityPresenter> activityPresenters;
    private RaplaWidget activePlace;

//    public Application getApplication()
//    {
//        return application;
//    }
//
//    public void setApplication(Application application)
//    {
//        this.application = application;
//    }
//
//    protected Application application;

    public AbstractActivityController(@SuppressWarnings("rawtypes") EventBus eventBus, Logger logger, Map<String, ActivityPresenter> activityPresenters)
    {
        this.logger = logger;
        this.activityPresenters = activityPresenters;
        eventBus.addHandler(PlaceChangedEvent.TYPE, this);
        eventBus.addHandler(Activity.TYPE, this);
    }

    @Override
    public void handleActivity(Activity activity)
    {
        if ( activity.isStop())
        {
            activities.remove(activity);
            updateHistroryEntry();
        }
        else
        {
            activities.add(activity);
            updateHistroryEntry();
            startActivity(activity);
        }
    }

    private boolean startActivity(Activity activity)
    {
        if ( activity == null)
        {
            return false;
        }
        final String activityId = activity.getId();
        final ActivityPresenter activityPresenter = activityPresenters.get(activityId);
        if ( activityPresenters == null)
        {
            return false;
        }
        final RaplaWidget<Object> objectRaplaWidget = activityPresenter.startActivity(activity);

        activePlace = objectRaplaWidget;
        if ( objectRaplaWidget != null)
        {
            initComponent( objectRaplaWidget);
        }
        final boolean result =  objectRaplaWidget != null;
        return result;
    }

    protected abstract void initComponent( RaplaWidget<Object> objectRaplaWidget);

    private void selectPlace(Place place)
    {
//        if (place != null && placePresenters.containsKey(place.getId()))
//        {
//            final String placeId = place.getId();
//            actualPlacePresenter = placePresenters.get(placeId);
//            actualPlacePresenter.initForPlace(place);
//            // FIXME for gwt
//            //mainView.updateContent( actualPlacePresenter.provideContent());
//        }
//        else
//        {
//            actualPlacePresenter = findBestSuited();
//            actualPlacePresenter.resetPlace();
//            // FIXME for gwt
//            //mainView.updateContent( actualPlacePresenter.provideContent());
//        }
    }

//    private PlacePresenter findBestSuited()
//    {
//        final Set<Map.Entry<String, PlacePresenter>> entrySet = placePresenters.entrySet();
//        for (Map.Entry<String, PlacePresenter> entry : entrySet)
//        {
//            if(entry.getKey().equals(CalendarPlacePresenter.PLACE_ID))
//            {
//                return entry.getValue();
//            }
//        }
//        // last change take first...
//        return placePresenters.values().iterator().next();
//    }


    @Override
    public void placeChanged(PlaceChangedEvent event)
    {
        place = event.getNewPlace();
        updateHistroryEntry();
        selectPlace(place);
    }

    public final void init() throws RaplaException
    {
        parsePlaceAndActivities();
        selectPlace(place);
        if (!activities.isEmpty())
        {
            ArrayList<Activity> toRemove = new ArrayList<Activity>();
            for (Activity activity : activities)
            {
                if (!startActivity(activity))
                {
                    toRemove.add(activity);
                }
            }
            if (!toRemove.isEmpty())
            {
                activities.removeAll(toRemove);
                updateHistroryEntry();
            }
        }
    }

    protected abstract void parsePlaceAndActivities() throws RaplaException;

    protected abstract void updateHistroryEntry();


    public void updateView(ModificationEvent e)
    {
        //actualPlacePresenter.updateView(e);
    }

    public <T> RaplaWidget<T> provideContent()
    {
        return  activePlace;
    }

    private static final String PLACE_SEPARATOR = "/";

    public static class Place
    {
        private final String id;
        private final String info;

        public Place(String id, String info)
        {
            this.id = id;
            this.info = info;
        }

        public String getId()
        {
            return id;
        }

        public String getInfo()
        {
            return info;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder(id);
            if (info != null)
            {
                sb.append(PLACE_SEPARATOR);
                sb.append(info);
            }
            return sb.toString();
        }

        public static Place fromString(String token)
        {
            if (token == null || token.isEmpty())
            {
                return null;
            }
            String substring = token;
            int paramsIndex = substring.indexOf("?");
            if (paramsIndex == 0)
            {
                return null;
            }
            if (paramsIndex > 0)
            {
                substring = substring.substring(0, paramsIndex);
            }
            int separator = substring.indexOf(PLACE_SEPARATOR);
            final String info;
            final String id;
            if (separator >= 0)
            {
                id = substring.substring(0, separator);
                info = substring.substring(separator + 1);
            }
            else
            {
                id = substring;
                info = null;
            }
            return new Place(id, info);
        }
    }

}
