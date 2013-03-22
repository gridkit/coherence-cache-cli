package org.gridkit.coherence.cachecli;

import java.io.IOException;

import com.tangosol.util.Binary;

public interface CacheDumpSource {
	
	public boolean isReady();
	
	public boolean next() throws IOException;
	
	public Binary getKey();

	public Binary getValue();

}
