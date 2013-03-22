/**
 * Copyright 2013 Alexey Ragozin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SimpleDomainObjGenerator  {

    public Map<DomainObjKey, DomainObject> generate(long from, long to) {
        Map<DomainObjKey, DomainObject> map = new HashMap<DomainObjKey, DomainObject>();
        
        for(long i = from; i != to; ++i) {
            DomainObject obj = generate(i);
            map.put(obj.getKey(), obj);
        }
        
        return map;
    }

    private DomainObject generate(long i) {
        Random rnd = new Random(i);
        DomainObject obj = new DomainObject(i);
        
        obj.a0 = makeAttr(0, i, rnd);
        obj.a1 = makeAttr(1, i, rnd);
        obj.a2 = makeAttr(2, i, rnd);
        obj.a3 = makeAttr(3, i, rnd);
        
        return obj;
    }

    private DomainObjAttrib makeAttr(int attrId, long i, Random rnd) {
        switch(attrId) {
        case 0: rnd.setSeed(i & 0xFFFFFFFF0l); break; 
        case 1: rnd.setSeed(i & 0xFFFFFFF0Fl); break; 
        case 2: rnd.setSeed(i & 0xFFFFFF0FFl); break; 
        case 3: rnd.setSeed(i & 0xFFFFF0FFFl); break; 
        }
        
        DomainObjAttrib attrib = new DomainObjAttrib();
        attrib.c0 = (char) ('0' + attrId);
        attrib.c1 = (char) ('A' + rnd.nextInt(23));
        attrib.c2 = (char) ('A' + rnd.nextInt(23));
        attrib.c3 = (char) ('A' + rnd.nextInt(23));
        attrib.c4 = (char) ('A' + rnd.nextInt(23));
        attrib.c5 = (char) ('A' + rnd.nextInt(23));
        attrib.c6 = (char) ('A' + rnd.nextInt(23));
        attrib.c7 = (char) ('A' + rnd.nextInt(23));
        
        return attrib;
    }
}