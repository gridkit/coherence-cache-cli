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

import java.io.IOException;

import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.run.xml.XmlElement;

public class BlobSerializer extends ConfigurablePofContext {

	private static final BlobPofSerializer BLOB_SERIALIZER = new BlobPofSerializer();
	
	public BlobSerializer() {
	}

	public BlobSerializer(String sLocator) {
		super(sLocator);
	}

	public BlobSerializer(XmlElement xml) {
		super(xml);
	}

	@Override
	public PofSerializer getPofSerializer(int id) {
		try {
			return super.getPofSerializer(id);
		}
		catch(Exception e) {
			return BLOB_SERIALIZER;
		}
	}
	
	@Override
	public int getUserTypeIdentifier(Object o) {
		if (o instanceof Blob) {
			return ((Blob)o).getTypeId();
		}
		else {
			return super.getUserTypeIdentifier(o);
		}
	}
	
	private static class BlobPofSerializer implements PofSerializer {

		@Override
		public Object deserialize(PofReader in) throws IOException {
			return new Blob(in.getUserTypeId(), in.readRemainder());
		}

		@Override
		public void serialize(PofWriter out, Object obj) throws IOException {
			out.writeRemainder(((Blob)obj).asBinary());
		}
	}
}
