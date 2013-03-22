package org.gridkit.coherence.cachecli;

import java.io.IOException;

import com.tangosol.util.Binary;

public interface CacheDumpSink {

	public void add(Binary key, Binary value) throws IOException;
	
}
