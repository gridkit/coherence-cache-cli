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
