/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.model.builder;

import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.model.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import java.util.ArrayList;
import java.util.List;
import org.geojson.Polygon;

/**
 * Builder class for MultiDatastream objects.
 *
 * @author scf
 */
public class MultiDatastreamBuilder extends AbstractEntityBuilder<MultiDatastream, MultiDatastreamBuilder> {

    private String name;
    private String description;
    private List<String> multiObservationDataTypes;
    private List<UnitOfMeasurement> unitOfMeasurements;
    private Polygon observedArea;
    private TimeInterval phenomenonTime;
    private TimeInterval resultTime;
    private Sensor sensor;
    private EntitySet<ObservedProperty> observedProperties;
    private Thing thing;
    private EntitySet<Observation> observations;

    public MultiDatastreamBuilder() {
        this.observations = new EntitySetImpl<>(EntityType.Observation);
        this.observedProperties = new EntitySetImpl<>(EntityType.ObservedProperty);
        this.unitOfMeasurements = new ArrayList<>();
        this.multiObservationDataTypes = new ArrayList<>();
    }

    public MultiDatastreamBuilder setObservations(EntitySet<Observation> observations) {
        this.observations = observations;
        return this;
    }

    public MultiDatastreamBuilder addObservation(Observation observation) {
        this.observations.add(observation);
        return this;
    }

    public MultiDatastreamBuilder setObservedProperties(EntitySet<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
        return this;
    }

    public MultiDatastreamBuilder addObservedProperty(ObservedProperty observedProperty) {
        this.observedProperties.add(observedProperty);
        return this;
    }

    public MultiDatastreamBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MultiDatastreamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MultiDatastreamBuilder setMultiObservationDataTypes(List<String> multiObservationDataTypes) {
        this.multiObservationDataTypes = multiObservationDataTypes;
        return this;
    }

    public MultiDatastreamBuilder addObservationType(String observationType) {
        this.multiObservationDataTypes.add(observationType);
        return this;
    }

    public MultiDatastreamBuilder setUnitOfMeasurements(List<UnitOfMeasurement> unitOfMeasurements) {
        this.unitOfMeasurements = unitOfMeasurements;
        return this;
    }

    public MultiDatastreamBuilder addUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
        this.unitOfMeasurements.add(unitOfMeasurement);
        return this;
    }

    public MultiDatastreamBuilder setObservedArea(Polygon observedArea) {
        this.observedArea = observedArea;
        return this;
    }

    public MultiDatastreamBuilder setPhenomenonTime(TimeInterval phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    public MultiDatastreamBuilder setResultTime(TimeInterval resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    public MultiDatastreamBuilder setSensor(Sensor sensor) {
        this.sensor = sensor;
        return this;
    }

    public MultiDatastreamBuilder setThing(Thing thing) {
        this.thing = thing;
        return this;
    }

    @Override
    protected MultiDatastreamBuilder getThis() {
        return this;
    }

    @Override
    public MultiDatastream build() {
        MultiDatastream mds = new MultiDatastream(
                id,
                selfLink,
                navigationLink,
                name,
                description,
                multiObservationDataTypes,
                unitOfMeasurements,
                observedArea,
                phenomenonTime,
                resultTime,
                sensor,
                observedProperties,
                thing,
                observations);
        mds.setExportObject(isExportObject());
        return mds;
    }

}
