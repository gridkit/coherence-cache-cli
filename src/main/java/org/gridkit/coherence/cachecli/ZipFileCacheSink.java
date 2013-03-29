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
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.tangosol.util.Binary;

public class ZipFileCacheSink implements CacheDumpSink {

	private final ZipOutputStream stream;
	private String prefix;
	private long counter = 0;
	
	public ZipFileCacheSink(ZipOutputStream sout) {
		this.stream = sout;
		this.prefix = "";		
	}
	
	public synchronized void setPrefix(String pre) {
		prefix = pre == null ? "" : pre;
	}

	@Override
	public synchronized void add(Binary key, Binary value) throws IOException {
		String entryName = String.format("%024d", counter++);
		ZipEntry ekey = new ZipEntry(prefix + entryName + ".key");
		ekey.setMethod(ZipEntry.STORED);
		byte[] bk = key.toByteArray();
		CRC32 crc32 = new CRC32();
		crc32.update(bk);
		ekey.setCrc(crc32.getValue());
		ekey.setSize(bk.length);
		stream.putNextEntry(ekey);
		stream.write(bk);
		
		ZipEntry eval = new ZipEntry(prefix + entryName + ".val");
		byte[] bv = value.toByteArray();
		if (bv.length > 4 << 10) { 
			eval.setMethod(ZipEntry.DEFLATED);
		}
		else {
			eval.setMethod(ZipEntry.STORED);
			crc32.reset();
			crc32.update(bv);
			eval.setCrc(crc32.getValue());
			eval.setSize(bv.length);
		}
		stream.putNextEntry(eval);
		stream.write(bv);
	}
	
	public void close() throws IOException {
		stream.close();
	}
}
