package org.eclipse.smarthome.binding.vallox.service;

import java.util.Collection;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

import com.google.common.collect.Lists;

public class ValloxThingHandlerFactory extends BaseThingHandlerFactory {

    private final static Collection<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Lists
            .newArrayList(ValloxBindingConstants.THING_TYPE_VALLOX_SERIAL);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(ValloxBindingConstants.THING_TYPE_VALLOX_SERIAL)) {
            return new ValloxSerialHandler(thing);
        }

        return null;
    }

}
