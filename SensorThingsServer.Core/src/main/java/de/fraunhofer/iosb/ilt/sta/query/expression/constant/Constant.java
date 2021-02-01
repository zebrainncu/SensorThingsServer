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
package de.fraunhofer.iosb.ilt.sta.query.expression.constant;

import de.fraunhofer.iosb.ilt.sta.query.expression.Value;
import java.util.Objects;

/**
 *
 * @author jab, scf
 * @param <T> The type of the constant value.
 */
public abstract class Constant<T> implements Value {

    protected T value;

    public static Constant fromString(Class<? extends Constant> type, String value) {
        if (BooleanConstant.class.equals(type)) {
            return new BooleanConstant(value);
        } else if (DateConstant.class.equals(type)) {
            return new DateConstant(value);
        } else if (DoubleConstant.class.equals(type)) {
            return new DoubleConstant(value);
        } else if (DurationConstant.class.equals(type)) {
            return new DurationConstant(value);
        } else if (GeoJsonConstant.class.equals(type)) {
            return GeoJsonConstant.fromString(value);
        } else if (IntegerConstant.class.equals(type)) {
            return new IntegerConstant(value);
        } else if (StringConstant.class.equals(type)) {
            return new StringConstant(value);
        } else if (TimeConstant.class.equals(type)) {
            return new TimeConstant(value);
        } else if (DateTimeConstant.class.equals(type)) {
            return new DateTimeConstant(value);
        } else {
            return new StringConstant(value);
        }
    }

    protected Constant() {

    }

    public Constant(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.value);
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
        final Constant<?> other = (Constant<?>) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

}
