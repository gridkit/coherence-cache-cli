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
