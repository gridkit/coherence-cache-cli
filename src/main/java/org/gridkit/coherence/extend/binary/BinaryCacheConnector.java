package org.gridkit.coherence.extend.binary;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.tangosol.coherence.component.net.extend.RemoteNamedCache;
import com.tangosol.coherence.component.net.extend.RemoteNamedCache$BinaryCache;
import com.tangosol.coherence.component.net.extend.message.response.PartialResponse;
import com.tangosol.coherence.component.util.SafeNamedCache;
import com.tangosol.net.CacheService;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.messaging.Channel;
import com.tangosol.run.xml.SimpleDocument;
import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlElement;
import com.tangosol.run.xml.XmlHelper;
import com.tangosol.util.Binary;
import com.tangosol.util.Filter;
import com.tangosol.util.MapListener;
import com.tangosol.util.ValueExtractor;

public class BinaryCacheConnector {

	private static AtomicInteger COUNTER = new AtomicInteger();
	
	private List<RemoteAddress> addresses = new ArrayList<RemoteAddress>();
	private XmlElement configTemplate;
	private int id = COUNTER.getAndIncrement();
	private CacheService service;
	
	public BinaryCacheConnector() {
		configTemplate = createDefaultConnetionConfig();
	}

	public void addRemoteAddress(String host, int port) {
		addresses.add(new RemoteAddress(host, port));
	}

	public NamedCache getCache(String cacheName) {
		return service.ensureCache(cacheName, null);
	}
	
	public BinaryCache getBinaryCache(String cacheName) {
		SafeNamedCache cache = (SafeNamedCache) service.ensureCache(cacheName, null);
		RemoteNamedCache rcache = (RemoteNamedCache)cache.getNamedCache();
		RemoteNamedCache$BinaryCache binCache = rcache.getBinaryCache();
		return new BinaryCacheDelegate(rcache, binCache);
	}
	
	public synchronized void connect() throws IOException {
		if (service != null) {
			if (service.isRunning()) {
				return;
			}
			else {
				service = null;
			}
		}
		XmlElement config = prepareConfig();
		DefaultConfigurableCacheFactory factory = new DefaultConfigurableCacheFactory();
		service = (CacheService) factory.ensureService(config);
	}
	
	private XmlElement prepareConfig() {
		if (addresses.isEmpty()) {
			throw new IllegalArgumentException("No remote hosts specifed");
		}
		XmlElement root = XmlHelper.loadXml(configTemplate.toString());
		root.ensureElement("service-name").setString(getClass().getSimpleName() + "-" + id);
		XmlElement ralist = root.ensureElement("initiator-config/tcp-initiator/remote-addresses");
		for(RemoteAddress ra: addresses) {
			XmlElement addr = ralist.addElement("socket-address");
			addr.addElement("address").setString(ra.getHost());
			addr.addElement("port").setInt(ra.getPort());
		}
		
		return root;
	}

	public synchronized void close() {
		if (service != null) {
			service.stop();
			service = null;
		}
	}
	
	protected static XmlElement createDefaultConnetionConfig() {
		XmlDocument root = new SimpleDocument();
		root.setName("remote-cache-scheme");
		root.ensureElement("scheme-name").setString("remote-connection");
		root.ensureElement("initiator-config");
		root.ensureElement("initiator-config/serializer/class-name").setString(BlobSerializer.class.getName());
		root.ensureElement("defer-key-association-check").setString("true");
		
		return root;
	}
	
	private static class RemoteAddress {
		
		private final String host;
		private final int port;

		public RemoteAddress(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static class BinaryCacheDelegate implements BinaryCache {
		
		private final RemoteNamedCache remoteCache;
		private final RemoteNamedCache$BinaryCache delegate;

		private BinaryCacheDelegate(RemoteNamedCache remoteCache, RemoteNamedCache$BinaryCache delegate) {
			this.remoteCache = remoteCache;
			this.delegate = delegate;
		}

		@Override
		public NamedCache getNamedCache() {
			return remoteCache;
		}

		@Override
		public void addIndex(ValueExtractor arg0, boolean arg1, Comparator arg2) {
			delegate.addIndex(arg0, arg1, arg2);
		}

		@Override
		public void addMapListener(MapListener fLite, Filter filter, boolean listener) {
			delegate.addMapListener(fLite, filter, listener);
		}

		@Override
		public void addMapListener(MapListener fLite, Object listener,	boolean oKey) {
			delegate.addMapListener(fLite, listener, oKey);
		}

		@Override
		public void addMapListener(MapListener listener) {
			delegate.addMapListener(listener);
		}

		@Override
		public Object aggregate(Collection arg0, EntryAggregator arg1) {
			return delegate.aggregate(arg0, arg1);
		}

		@Override
		public Object aggregate(Filter arg0, EntryAggregator arg1) {
			return delegate.aggregate(arg0, arg1);
		}

		@Override
		public void clear() {
			delegate.clear();
		}

		@Override
		public boolean containsAll(Collection arg0) {
			return delegate.containsAll(arg0);
		}

		@Override
		public boolean containsKey(Object arg0) {
			return delegate.containsKey(arg0);
		}

		@Override
		public boolean containsValue(Object arg0) {
			return delegate.containsValue(arg0);
		}

		@Override
		public void destroy() {
			delegate.destroy();
		}

		@Override
		public Set entrySet() {
			return delegate.entrySet();
		}

		@Override
		public Set entrySet(Filter comparator, Comparator filter) {
			return delegate.entrySet(comparator, filter);
		}

		@Override
		public Set entrySet(Filter filter) {
			return delegate.entrySet(filter);
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public Object get(Object arg0) {
			return delegate.get(arg0);
		}

		@Override
		public Map getAll(Collection arg0) {
			return delegate.getAll(arg0);
		}

		@Override
		public String getCacheName() {
			return delegate.getCacheName();
		}

		@Override
		public CacheService getCacheService() {
			return delegate.getCacheService();
		}

		@Override
		public Channel getChannel() {
			return delegate.getChannel();
		}

		@Override
		public Set binaryEntrySet() {
			return delegate.getEntrySet();
		}

		@Override
		public Set binaryKeySet() {
			return delegate.getKeySet();
		}

		@Override
		public Collection binaryValues() {
			return delegate.getValues();
		}

		@Override
		public Object invoke(Object arg0, EntryProcessor arg1) {
			return delegate.invoke(arg0, arg1);
		}

		@Override
		public Map invokeAll(Collection arg0, EntryProcessor arg1) {
			return delegate.invokeAll(arg0, arg1);
		}

		@Override
		public Map invokeAll(Filter arg0, EntryProcessor arg1) {
			return delegate.invokeAll(arg0, arg1);
		}

		@Override
		public boolean isActive() {
			return delegate.isActive();
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public Set keySet() {
			return delegate.keySet();
		}

		@Override
		public Set keySet(Filter filter) {
			return delegate.keySet(filter);
		}

		@Override
		public PartialResponse keySetPage(Binary arg0) {
			return delegate.keySetPage(arg0);
		}

		@Override
		public boolean lock(Object arg0, long arg1) {
			return delegate.lock(arg0, arg1);
		}

		@Override
		public boolean lock(Object oKey) {
			return delegate.lock(oKey);
		}

		@Override
		public Object put(Binary key, Binary value, long expiry, boolean needValue) {
			return delegate.put(key, value, expiry, needValue);
		}

		@Override
		public Object put(Object cMillis, Object oKey, long oValue) {
			return delegate.put(cMillis, oKey, oValue);
		}

		@Override
		public Object put(Object oKey, Object oValue) {
			return delegate.put(oKey, oValue);
		}

		@Override
		public void putAll(Map arg0) {
			delegate.putAll(arg0);
		}

		@Override
		public void release() {
			delegate.release();
		}

		@Override
		public Object remove(Binary key, boolean returnPrev) {
			return delegate.remove(key, returnPrev);
		}

		@Override
		public Object remove(Object oKey) {
			return delegate.remove(oKey);
		}

		@Override
		public boolean removeAll(Collection arg0) {
			return delegate.removeAll(arg0);
		}

		@Override
		public void removeIndex(ValueExtractor arg0) {
			delegate.removeIndex(arg0);
		}

		@Override
		public void removeMapListener(MapListener filter, Filter listener) {
			delegate.removeMapListener(filter, listener);
		}

		@Override
		public void removeMapListener(MapListener listener, Object oKey) {
			delegate.removeMapListener(listener, oKey);
		}

		@Override
		public void removeMapListener(MapListener listener) {
			delegate.removeMapListener(listener);
		}

		@Override
		public int size() {
			return delegate.size();
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public boolean unlock(Object arg0) {
			return delegate.unlock(arg0);
		}

		@Override
		public Collection values() {
			return delegate.values();
		}
	}
}
