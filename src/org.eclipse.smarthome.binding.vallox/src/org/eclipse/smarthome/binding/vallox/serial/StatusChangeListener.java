package org.eclipse.smarthome.binding.vallox.serial;

import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;

/**
 * Simple interface to allow some listener to listen to status changes.
 * 
 * @author Hauke
 *
 */
public interface StatusChangeListener {

    public void statusChanged(ThingStatus status, ThingStatusDetail detail, String message);

}
