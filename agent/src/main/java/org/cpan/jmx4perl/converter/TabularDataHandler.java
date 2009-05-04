/*
 * jmx4perl - WAR Agent for exporting JMX via JSON
 *
 * Copyright (C) 2009 Roland Huß, roland@cpan.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact roland@cpan.org for any licensing questions.
 */

package org.cpan.jmx4perl.converter;

import org.json.simple.JSONArray;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * @author roland
 * @since Apr 19, 2009
 */
public class TabularDataHandler implements AttributeToJsonConverter.Handler {

    public Class getType() {
        return TabularData.class;
    }

    public Object handle(AttributeToJsonConverter pConverter, Object pValue,
                         Stack<String> pExtraArgs) {
        TabularData td = (TabularData) pValue;

        if (!pExtraArgs.isEmpty()) {
            String index = pExtraArgs.pop();
            int idx = Integer.valueOf(index).intValue();
            CompositeData cd = getRow(idx, td.values().iterator());
            return pConverter.prepareForJson(cd,pExtraArgs);
        } else {
            JSONArray ret = new JSONArray();
            for (CompositeData cd : (Collection <CompositeData>) td.values()) {
                ret.add(pConverter.prepareForJson(cd,pExtraArgs));
            }
            return ret;
        }
    }

    private static CompositeData getRow(int idx, Iterator it) {
        try {
            for (int i = 0; i < idx; i++) {
                it.next();
            }
        } catch (NoSuchElementException ex) {
             throw new IllegalArgumentException(
                     "Index " + idx + " out of range. Ex: " + ex.toString());
        }
        return (CompositeData) it.next();
    }

}