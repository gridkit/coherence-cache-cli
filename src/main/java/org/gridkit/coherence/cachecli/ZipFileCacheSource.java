package org.gridkit.coherence.cachecli;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.tangosol.util.Binary;

public class ZipFileCacheSource implements CacheDumpSource {

	private final ZipInputStream stream;
	private String prefix;

	private Binary key;
	private Binary value;
	
	public ZipFileCacheSource(ZipInputStream in) {
		this.stream = in;
		this.prefix = "";		
	}
	
	public synchronized void setPrefix(String pre) {
		prefix = pre == null ? "" : pre;
	}

	@Override
	public boolean isReady() {
		return key != null;
	}

	@Override
	public boolean next() throws IOException {
		key = null;
		value = null;
		while(true) {
			ZipEntry entry = stream.getNextEntry();
			if (entry == null) {
				return false;
			}			
			String name = entry.getName();
			if (!name.startsWith(prefix)) {
				continue;
			}
			else if (name.endsWith(".key")) {
				int size = (int) entry.getSize();
				byte[] data = new byte[size];
				readFully(data);
				key = new Binary(data);
				entry = stream.getNextEntry();
				String expect = name.substring(0, name.length() - 4) + ".val";
				if (!expect.equals(entry.getName())) {
					throw new IOException("Expected entry: " + expect);
				}
				data = new byte[(int) entry.getSize()];
				readFully(data);
				value = new Binary(data);
				return true;
			}
		}
	}

	private void readFully(byte[] data) throws IOException {
		int n = 0;
		while(n < data.length) {
			n += stream.read(data, n, data.length - n);
		}
	}
	
	@Override
	public Binary getKey() {
		if (key == null) {
			throw new IllegalStateException("Not ready");
		}
		return key;
	}

	@Override
	public Binary getValue() {
		if (key == null) {
			throw new IllegalStateException("Not ready");
		}
		return value;
	}

	public void close() throws IOException {
		stream.close();
	}
}
