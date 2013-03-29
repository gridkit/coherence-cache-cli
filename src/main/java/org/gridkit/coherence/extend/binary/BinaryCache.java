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
