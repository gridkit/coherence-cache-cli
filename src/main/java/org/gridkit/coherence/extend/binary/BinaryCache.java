package org.gridkit.coherence.extend.binary;

import java.util.Collection;
import java.util.Set;

import com.tangosol.coherence.component.net.extend.message.response.PartialResponse;
import com.tangosol.net.NamedCache;
import com.tangosol.net.messaging.Channel;
import com.tangosol.util.Binary;
import com.tangosol.util.Filter;

public interface BinaryCache extends NamedCache {

	public NamedCache getNamedCache();
	
	public boolean containsAll(Collection<Binary> colKeys);

	public Channel getChannel();

	public Set<Binary> binaryEntrySet();

	public Set<Binary> binaryKeySet();

	public Set<Binary> binaryKeySet(Filter f);

	PartialResponse keySetPage(Binary cookie);

	public Collection<Binary> binaryValues();

	public Object put(Binary oKey, Binary oValue, long cMillis, boolean fReturn);

	public Object remove(Binary oKey, boolean fReturn);

	public boolean removeAll(Collection<Binary> colKeys);
}
