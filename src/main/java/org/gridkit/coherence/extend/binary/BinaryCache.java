package org.gridkit.coherence.extend.binary;

import java.util.Collection;
import java.util.Set;

import com.tangosol.coherence.component.net.extend.message.response.PartialResponse;
import com.tangosol.net.NamedCache;
import com.tangosol.net.messaging.Channel;
import com.tangosol.util.Binary;

public interface BinaryCache extends NamedCache {

	public NamedCache getNamedCache();
	
	public boolean containsAll(Collection<Binary> colKeys);

	public Channel getChannel();

	public Set<Binary> getEntrySet();

	public Set<Binary> getKeySet();

	PartialResponse keySetPage(Binary cookie);

	public Collection<Binary> getValues();

	public Object put(Binary oKey, Binary oValue, long cMillis, boolean fReturn);

	public Object remove(Binary oKey, boolean fReturn);

	public boolean removeAll(Collection<Binary> colKeys);
}
