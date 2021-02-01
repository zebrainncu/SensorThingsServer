package de.fraunhofer.iosb.ilt.sta.model;

import de.fraunhofer.iosb.ilt.sta.model.builder.ThingBuilder;
import de.fraunhofer.iosb.ilt.sta.model.core.AbstractEntity;
import de.fraunhofer.iosb.ilt.sta.model.id.Id;
import de.fraunhofer.iosb.ilt.sta.path.EntityPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntitySetPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePathElement;
import de.fraunhofer.iosb.ilt.sta.util.IncompleteEntityException;

import java.util.Objects;

/**
 * Created by zebrawlord on 2017/5/5.
 */
public class Actuator extends AbstractEntity {

    private String description = null;
    private String encodingType = null;
    private Object metadata;

    private boolean setDescription;
    //for update
    private boolean setEncodingType;
    private boolean setMetadata;

    private TaskingCapability taskingCapability;
    private boolean setTaskingCapability;

    public Actuator(Id id,
                    String description,
                    Object metadata,
                    String encodingType,
                    TaskingCapability taskingCapability) {
        this.id = id;
        this.description = description;
        this.encodingType=encodingType;
        this.metadata=metadata;
        this.taskingCapability=taskingCapability;
    }

    public Actuator() {

    }

///之後再開通
    @Override
    public EntityType getEntityType() {
//        return EntityType.Actuator;
        return EntityType.Thing;
    }

    @Override
    public void setEntityPropertiesSet() {
        setDescription = true;
        setEncodingType = true;
        setMetadata = true;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public Object getMetadata() {
        return metadata;
    }


    public void setMetadata(Object metadata) {
        this.metadata = metadata;
        setMetadata = true;
    }
    public boolean isSetDescription() {
        return setDescription;
    }
    public boolean isSetEncodingType() {
        return setEncodingType;
    }
    public boolean isSetMetadata() {
        return setMetadata;
    }
    public boolean isSetTaskingCapability() {
        return setTaskingCapability;
    }

    public TaskingCapability getTaskingCapability() {
        return taskingCapability;
    }

    public void setTaskingCapability(TaskingCapability taskingCapability) {
        this.taskingCapability = taskingCapability;
        setTaskingCapability = true;
    }

//    @Override
//    public void complete(EntitySetPathElement containingSet) throws IncompleteEntityException {
//        ResourcePathElement parent = containingSet.getParent();
//        if (parent != null && parent instanceof EntityPathElement) {
//            EntityPathElement parentEntity = (EntityPathElement) parent;
//            Id parentId = parentEntity.getId();
//            if (parentId != null) {
//                switch (parentEntity.getEntityType()) {
//
//
//                    case TaskingCapability: //for eThing()
////                        setThing(new ThingBuilder().setId(parentId).build());
////                        LOGGER.debug("Set thingId to {}.", parentId);
//                        break;
//
//                }
//            }
//        }
//
//        super.complete(containingSet);
//    }

    @Override
    public int hashCode() {
        int hash = 77;

        hash = 71 * hash + Objects.hashCode(this.getDescription());
        hash = 71 * hash + Objects.hashCode(this.getEncodingType());
        hash = 71 * hash + Objects.hashCode(this.getMetadata());
        hash = 71 * hash + Objects.hashCode(this.getTaskingCapability());//add

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
        final Actuator other = (Actuator) obj;
        if (!super.equals(other)) {
            return false;
        }

        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.encodingType, other.encodingType)) {
            return false;
        }
        if (!Objects.equals(this.metadata, other.metadata)) {
            return false;
        }

        //add
        if (!Objects.equals(this.taskingCapability, other.taskingCapability)) {
            return false;
        }

        return true;
    }
}
