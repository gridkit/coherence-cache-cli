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
