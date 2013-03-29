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
package org.gridkit.coherence.extend.binary;

import junit.framework.Assert;

import org.gridkit.coherence.extend.binary.BinaryCacheConnector;
import org.junit.Test;

import com.tangosol.run.xml.XmlHelper;

public class RemoteCacheConnectorTest {

	private static String DEFAULT_CONFIG = "" +
			"<?xml version='1.0'?>" +
			"<remote-cache-scheme>" +
			"  <scheme-name>remote-connection</scheme-name>" +
			"  <initiator-config>" +
			"    <tcp-initiator/>" +
			"    <serializer>" +
			"      <class-name>org.gridkit.coherence.extend.BlobSerializer</class-name>" +
			"    </serializer>" +
			"  </initiator-config>" +
			"</remote-cache-scheme>";
	
	@Test
	public void test_config() {
		Assert.assertEquals(XmlHelper.loadXml(DEFAULT_CONFIG).toString(), BinaryCacheConnector.createDefaultConnetionConfig().toString());
	}
}
