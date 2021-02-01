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
package de.fraunhofer.iosb.ilt.sta.model;

import de.fraunhofer.iosb.ilt.sta.model.builder.ObservedPropertyBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.SensorBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.ThingBuilder;
import de.fraunhofer.iosb.ilt.sta.model.core.AbstractEntity;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.model.id.Id;
import de.fraunhofer.iosb.ilt.sta.path.EntityPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntitySetPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePathElement;
import de.fraunhofer.iosb.ilt.sta.util.IncompleteEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 *
 *




 -----------         ---------------------      ----------
 |  Task   | ------- | TaskingCapability | -----| Thing  |
 -----------         ---------------------      ----------
                           |
                           |
                           |
                       ------------
                       | Actuator |
                       ------------

 TaskingCapability have three properties, description 、 <Parameter>  and  <HTTPProtocol>

 */
public class TaskingCapability extends AbstractEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(Datastream.class);

    private String name;
    private String description;
    //    private Map<String, Object> properties;
    private Object parameters;
    private Object httpProtocol;
//    private Map<String, Object> parameters;
//    private Map<String, Object> protocols;
//    private EntitySet<Location> locations; // 0..*

    //new
//    private ArrayList<Parameter> parameterList = new ArrayList();
//    private ArrayList<Protocol> protocolList = new ArrayList();
//    private Object parameterList;
//    private Object protocolList;

//    private ArrayList<Thing> thingList = new ArrayList(); //for creating
//    private ArrayList<Task> taskList = new ArrayList(); //for creating
//    private ArrayList<Actuator> actuatorList = new ArrayList(); //for creating


    private boolean setName;
    private boolean setDescription;
    //for update
    private boolean setParameters;
    private boolean sethttpProtocol;


//    private EntitySet<Thing> things;//for cThings()
    private Thing thing;//for eThing()  參考 HistoricalLocation.java
   //TaskingCapability看到的Thing是一個  所以只能用eThing(),而不是多個,是多個才採用EntitySet<Thing>裝資料

    private boolean setThing;

    private Actuator actuator;
    // TaskingCapability看到的Actuator也是一個,同Thing, 所以只能用eActuator(),而不是多個,是多個才採用EntitySet<Thing>裝資料

//    private EntitySet<Task> tasks;//// 0..*   for cThings()
    
    public TaskingCapability(){

    }

    public TaskingCapability(Id id,
                             String description,
                             Object parameters
//            ,Thing thing
    ){
        this.id = id;
        this.description = description;
        this.parameters = parameters;
//        this.thing = thing;//add  用不到
 }
    public TaskingCapability(Id id,
                             String description,
                             Object parameters,
                             Object httpProtocol
//            ,Thing thing
    ){
        this.id = id;
        this.description = description;
        this.parameters = parameters;
        this.httpProtocol = httpProtocol;
//        this.thing = thing;//add
    }


    @Override
    public EntityType getEntityType() {
        return EntityType.TaskingCapability;
    }

    @Override
    public void setEntityPropertiesSet() {
        setDescription = true;
        setParameters = true;
        sethttpProtocol = true;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public Object getParameters() {
        return parameters;
    }

    public Object gethttpProtocol() {
        return httpProtocol;
    }

    public boolean isSetName() {
        return setName;
    }

    public boolean isSetDescription() {
        return setDescription;
    }
    public boolean isSetParameter() {
        return setParameters;
    }
    public boolean isSethttpProtocol() {
        return sethttpProtocol;
    }
    public boolean isSetThing() {
        return setThing;
    }

    public void setName(String name) {
        this.name = name;
        setName = true;
    }

    public void setDescription(String description) {
        this.description = description;
        setDescription = true;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
        setParameters = true;
    }
    public void sethttpProtocol(Object protocol) {
        this.httpProtocol = protocol;
        sethttpProtocol= true;

    }

    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
        setThing = true;
    }

    //GET Need
    public Actuator getActuator() {
        return actuator;
    }
    //POST Need
    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    //20170502 add
    @Override
    public void complete(EntitySetPathElement containingSet) throws IncompleteEntityException {
        ResourcePathElement parent = containingSet.getParent();
        if (parent != null && parent instanceof EntityPathElement) {
            EntityPathElement parentEntity = (EntityPathElement) parent;
            Id parentId = parentEntity.getId();
            if (parentId != null) {
//                switch (parentEntity.getEntityType()) {

//                    case Thing://for cThings()
//                        getThings().add(new ThingBuilder().setId(parentId).build());
//                        LOGGER.debug("Added thingId to {}.", parentId);
//                        break;
//                    case Thing: //for eThing()  //用不到
//                        setThing(new ThingBuilder().setId(parentId).build());
//                        LOGGER.debug("Set thingId to {}.", parentId);
//                        break;
//                }
            }
        }

        super.complete(containingSet);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.getName());
        hash = 71 * hash + Objects.hashCode(this.getDescription());
        hash = 71 * hash + Objects.hashCode(this.getParameters());
        hash = 71 * hash + Objects.hashCode(this.gethttpProtocol());
        hash = 71 * hash + Objects.hashCode(this.getThing());//add

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaskingCapability other = (TaskingCapability) obj;
        if (!super.equals(other)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        if (!Objects.equals(this.httpProtocol, other.httpProtocol)) {
            return false;
        }

        //add 為什麼要加?
        if (!Objects.equals(this.thing, other.thing)) {
            return false;
        }

        return true;
    }



}
