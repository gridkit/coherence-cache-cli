/**
 * Copyright 2013 Alexey Ragozin
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
package org.gridkit.coherence.cachecli;

import java.io.File;
import java.util.Map;

import junit.framework.Assert;

import org.gridkit.coherence.chtest.CacheConfig;
import org.gridkit.coherence.chtest.CacheConfig.DistributedScheme;
import org.gridkit.coherence.chtest.CacheConfig.ProxyScheme;
import org.gridkit.coherence.chtest.CohCloud.CohNode;
import org.gridkit.coherence.chtest.DisposableCohCloud;
import org.gridkit.coherence.utils.pof.AutoPofSerializer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import sample.DomainObjKey;
import sample.DomainObject;
import sample.SimpleDomainObjGenerator;

import com.tangosol.net.NamedCache;

public class ImportExportTest {

	public int proxyPort = 12301;
	
	@Rule
	public DisposableCohCloud cloud = new DisposableCohCloud();
	
	@Before
	public void initCluster() {
		cloud.all().useEmptyCacheConfig();
		cloud.all().enableJmx(true);
		
		DistributedScheme distributed = CacheConfig.distributedSheme();
		distributed.serializer(AutoPofSerializer.class);
		distributed.backingMapScheme(CacheConfig.localScheme());
		
		ProxyScheme proxy = CacheConfig.proxyScheme();
		proxy.serviceName("ExtendProxy");
		proxy.cacheProxyEnabled(true);
		proxy.serializer(AutoPofSerializer.class);
		proxy.tcpAcceptorLocalAddress("127.0.0.1", proxyPort);
		proxy.autoStart(true);
		
		CohNode server = cloud.node("server");
		server.presetFastLocalCluster();
		server.localStorage(true);
		server.addScheme(proxy);
		server.mapCache("*", distributed);
		server.autoStartServices();
		
	}
	
	@Test
	public void import_export_simple_objects() {
		
		cloud.all().touch();
		NamedCache cache = cloud.node("server").getCache("test");
		
		for(int i = 0; i != 100; ++i) {
			cache.put("key-" + i, i);
		}
		
		new File("target").mkdirs();
		cliExec("-c", "extend://127.0.0.1:" + proxyPort + "/test", "export", "-o", "-zf", "target/dump.zip");
		
		cache.clear();

		Assert.assertEquals(0, cache.size());

		cliExec("-c", "extend://127.0.0.1:" + proxyPort + "/test", "import", "-zf", "target/dump.zip");
		
		Assert.assertEquals(100, cache.size());
		Assert.assertEquals(37, cache.get("key-37"));
	}

	@Test
	public void import_export_complex_objects() {
		
		cloud.all().touch();
		NamedCache cache = cloud.node("server").getCache("test");
		
		SimpleDomainObjGenerator gen = new SimpleDomainObjGenerator();
		
		Map<DomainObjKey, DomainObject> map = gen.generate(0, 100);
		
		cache.putAll(map);
		
		new File("target").mkdirs();
		cliExec("-c", "extend://127.0.0.1:" + proxyPort + "/test", "export", "-o", "-zf", "target/dump.zip");
		
		cache.clear();

		Assert.assertEquals(0, cache.size());

		cliExec("-c", "extend://127.0.0.1:" + proxyPort + "/test", "import", "-zf", "target/dump.zip");
		cliExec("-c", "extend://127.0.0.1:" + proxyPort + "/test", "list", "-pp");
		
		Assert.assertEquals(100, cache.size());

		DomainObjKey key = map.keySet().iterator().next();
		Assert.assertEquals(map.get(key), cache.get(key));
	}
	
	
	private void cliExec(String... command) {
		CacheCli cli = new CacheCli();
		cli.suppressSystemExit();
		Assert.assertTrue(cli.start(command));
	}

}
