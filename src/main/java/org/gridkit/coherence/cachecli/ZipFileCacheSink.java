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
