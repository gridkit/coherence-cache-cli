/**
 * Copyright 2012 Alexey Ragozin
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
package org.gridkit.coherence.extend.binary;

import com.tangosol.util.Binary;

public class Blob {
	
	private final int typeId;
	private final Binary data;
	
	public Blob(int typeId, Binary data) {
		this.typeId = typeId;
		this.data = data;
	}

	public int getTypeId() {
		return typeId;
	}
	
	public Binary asBinary() {
		return data;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		Blob other = (Blob) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
	    int cb = data.length();
	    boolean fTrunc = cb > 256;
	    return new StringBuilder().append("Binary(typeId=" + typeId + ", length=").append(cb).append(", value=").append(Binary.toHexEscape(data.toByteArray(0, fTrunc ? 256 : cb))).append(fTrunc ? "...)" : ")").toString();
	}
}
