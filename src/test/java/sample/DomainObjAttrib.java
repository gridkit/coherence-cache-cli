/**
 * Copyright 2008-2009 Grid Dynamics Consulting Services, Inc.
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

import java.io.Serializable;

/**
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
@SuppressWarnings("serial")
public class DomainObjAttrib implements Serializable {
    
    char c0, c1, c2, c3, c4, c5, c6, c7;
    
    public DomainObjAttrib() {
    }

    public DomainObjAttrib(String str) {
        c0 = str.length() > 0 ? str.charAt(0) : 0;
        c1 = str.length() > 1 ? str.charAt(1) : 0;
        c2 = str.length() > 2 ? str.charAt(2) : 0;
        c3 = str.length() > 3 ? str.charAt(3) : 0;
        c4 = str.length() > 4 ? str.charAt(4) : 0;
        c5 = str.length() > 5 ? str.charAt(5) : 0;
        c6 = str.length() > 6 ? str.charAt(6) : 0;
        c7 = str.length() > 7 ? str.charAt(7) : 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + c0;
        result = prime * result + c1;
        result = prime * result + c2;
        result = prime * result + c3;
        result = prime * result + c4;
        result = prime * result + c5;
        result = prime * result + c6;
        result = prime * result + c7;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DomainObjAttrib other = (DomainObjAttrib) obj;
        if (c0 != other.c0)
            return false;
        if (c1 != other.c1)
            return false;
        if (c2 != other.c2)
            return false;
        if (c3 != other.c3)
            return false;
        if (c4 != other.c4)
            return false;
        if (c5 != other.c5)
            return false;
        if (c6 != other.c6)
            return false;
        if (c7 != other.c7)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(8);
        if (c0 > 0) buf.append(c0);
        if (c1 > 0) buf.append(c1);
        if (c2 > 0) buf.append(c2);
        if (c3 > 0) buf.append(c3);
        if (c4 > 0) buf.append(c4);
        if (c5 > 0) buf.append(c5);
        if (c6 > 0) buf.append(c6);
        if (c7 > 0) buf.append(c7);
        return buf.toString();
    }
}
