package org.drools.spring.metadata;

import org.drools.spi.ObjectType;

public class BeanObjectType implements ObjectType {

    private Class clazz;

    public BeanObjectType(Class objectTypeClass) {
        this.clazz = objectTypeClass;
    }

    public Class getType() {
        return this.clazz;
    }

    public boolean matches(Object object) {
        return getType().isInstance(object);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        return this.clazz == ((BeanObjectType) object).clazz;
    }

    public int hashCode() {
        return getType().hashCode();
    }

    public String toString() {
        return getType().getName();
    }
}