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
