package org.gridkit.coherence.extend.binary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.gridkit.coherence.extend.binary.BinaryCache;
import org.gridkit.coherence.extend.binary.BinaryCacheConnector;
import org.gridkit.coherence.misc.pofviewer.PofEntry;
import org.gridkit.coherence.misc.pofviewer.PofFinePrinter;
import org.gridkit.coherence.misc.pofviewer.PofParser;
import org.gridkit.coherence.misc.pofviewer.PofPath;
import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.vicluster.ViManager;
import org.gridkit.vicluster.ViNode;
import org.junit.Test;

import com.tangosol.coherence.component.net.extend.message.response.PartialResponse;
import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.net.NamedCache;
import com.tangosol.net.partition.PartitionSet;
import com.tangosol.util.Binary;
import com.tangosol.util.Filter;
import com.tangosol.util.aggregator.Count;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.EqualsFilter;
import com.tangosol.util.filter.PartitionedFilter;

public class ConnectionCheck {

	@Test
	public void remote_connection_check() {
		
		ViManager manager = CloudFactory.createSimpleSshCloud();
		ViNode target = manager.node("longmrdfappu1.uk.db.com");
		
		target.exec(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				new ConnectionCheck().test_connection();
				return null;
			}
		});
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_connection() throws IOException {
		
		System.setProperty("tangosol.pof.enabled", "true");
		
		BinaryCacheConnector connector = new BinaryCacheConnector();
		connector.addRemoteAddress("host", 9180);
		connector.connect();
//		NamedCache cache = connector.getCache("RISK_GROUP");
//		NamedCache cache = connector.getCache("DICTIONARY");
//		NamedCache cache = connector.getCache("DICTIONARY_ITEM");
		NamedCache cache = connector.getCache("REFERENCE_DATA");
//		NamedCache cache = connector.getCache("CONFIG_CHANGE");
//		NamedCache cache = connector.getCache("RISK_ENGINE");

		BinaryCache binCache = connector.getBinaryCache(cache.getCacheName());
		
//		cache.get("");
		System.out.println("Cache size: " + cache.size());
//		ArrayList<Object> keys = new ArrayList<Object>(cache.keySet());
//		System.out.println("Cache keys: " + keys);
//		for(Object v: cache.values()) {
//			System.out.println("  " + v);
//		}

		System.out.println("Bin cache size: " + binCache.size());
//		ArrayList<Object> bkeys = new ArrayList<Object>(binCache.keySet());
//		System.out.println("Bin cache keys: " + bkeys);
//		for (Object v : bkeys) {
//			System.out.println("  " + binCache.get(v));
//		}
		
		if (true) {
			PofContext ctx = new ConfigurablePofContext();
			Binary cookie = null;
			int n = 0;
			while(true) {
				PartialResponse pr = binCache.keySetPage(cookie);
				Collection<?> keys = (Collection<?>) pr.getResult();
				for(Object o : keys) {
					Binary bk = (Binary) o;
					System.out.println("KEY #" + n++);			
					System.out.println(printKey(ctx, bk));
					System.out.println("");
					System.out.println("VALUE");			
					System.out.println(printKey(ctx, (Binary)binCache.get(bk)));
					System.out.println("\n");				
				}
	
				cookie = pr.getCookie();
				if (cookie == null) {
					break;
				}
			}
		}
		
		PartitionSet ps = new PartitionSet(577);
		ps.fill();
		
		System.out.println("Partition size: " + cache.aggregate(new PartitionedFilter(AlwaysFilter.INSTANCE, ps), new Count()));
		
		Filter pc17 = new EqualsFilter(new PofExtractor(null, new SimplePofPath(new int[]{1,1})), "PC_QA_17");
		Filter pc10 = new EqualsFilter(new PofExtractor(null, new SimplePofPath(new int[]{1,1})), "PC_QA_10");
		
//		System.out.println("Selection size: " + pc17 + " - " + cache.aggregate(pc17, new Count()));
//		System.out.println("Selection size: " + pc10 + " - " + cache.aggregate(pc10, new Count()));
//		
//
//		System.out.println("Cache size: " + cache.size());
//		System.out.println("Cache keys: " + new ArrayList(cache.keySet()));
//
//		System.out.println("View names: " + cache.get("dbViewNames"));
//
//		Binary key = ExternalizableHelper.toBinary("dbViewNames", cache.getCacheService().getSerializer());
//		BinaryEntry holder = new BinaryEntryHolder(key);
//		binCache.load(holder);
//
//		System.out.println("View names binary: " + holder.getBinaryValue());
		
		
	}
	
	public String printKey(PofContext context, Binary binary) {
		List<PofEntry> entries = PofParser.parsePof(binary, context);
		PofFinePrinter fp = new PofFinePrinter();
		Map<PofPath, String> aliases = fp.findAliases(entries);
		
		StringBuilder sb = new StringBuilder();
		for(PofEntry entry : entries) {
			String type = fp.getClassName(entry.getTypeId());
			if (type == null) {
				type = String.valueOf(entry.getTypeId());
			}
			String path = aliases.get(entry.getPath());
			if (path == null) {
				path = entry.getPath().toString();
			}
			String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
			sb.append(path).append(", ").append(type).append(", ").append(value).append('\n');
		}

		return sb.toString();
	}
}
