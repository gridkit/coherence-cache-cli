package org.gridkit.coherence.cachecli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.gridkit.coherence.extend.binary.BinaryCache;
import org.gridkit.coherence.extend.binary.BinaryCacheConnector;
import org.gridkit.coherence.extend.binary.BlobSerializer;
import org.gridkit.coherence.misc.pofviewer.PofCompactPrinter;
import org.gridkit.coherence.misc.pofviewer.PofEntry;
import org.gridkit.coherence.misc.pofviewer.PofFinePrinter;
import org.gridkit.coherence.misc.pofviewer.PofParser;
import org.gridkit.coherence.misc.pofviewer.PofPath;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.tangosol.io.pof.PortableException;
import com.tangosol.net.NamedCache;
import com.tangosol.net.partition.PartitionSet;
import com.tangosol.util.Binary;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.PartitionedFilter;

public class CacheCli {

	@Parameter(names = {"--help"}, help = true, hidden = true, description = "Display help")
	private boolean help;

	@Parameter(names = {"-c", "--cache"}, description = "Target cache URL. E.g. extend://host:port/CachName", required = true)
	private String cacheUrl; 
	
	public static void main(String[] args) {
		CacheCli cli = new CacheCli();
		cli.start(args);
			
	}

	private Map<String, Cmd> commands = new HashMap<String, Cmd>();
	{
		commands.put("size", new SizeCmd());
		commands.put("list", new ListCmd());
		commands.put("export", new ExportCmd());
		commands.put("import", new ImportCmd());
	}
	
	private boolean suppressSystemExit = false;
	
	public void suppressSystemExit() {
		suppressSystemExit = true;
	}
	
	public boolean start(String... args) {
		try {
			JCommander parser = new JCommander(this);
			for(String cmd: commands.keySet()) {
				parser.addCommand(cmd, commands.get(cmd));
			}
			
			try {
				parser.parse(args);
			}
			catch(Exception e) {
				System.err.println(e.toString());
				String cmd = parser.getParsedCommand();
				parser.usage();
				error("");
			}

			if (help) {
				String cmd = parser.getParsedCommand();
				if (cmd == null) { 
					parser.usage();
				}
				else {
					parser.usage(cmd);
				}
				
				if (suppressSystemExit) {
					return true;
				}
				else {
					System.exit(0);
				}
			}
			else {
			
				Cmd cmd = commands.get(parser.getParsedCommand());
				
				if (cmd == null) {
					parser.usage();
					if (suppressSystemExit) {
						return false;
					}
					else {
						System.exit(1);
					}
				}
				else {
					cmd.exec();
				}
				
				if (suppressSystemExit) {
					return true;
				}
				else {
					System.exit(0);
				}
			}
		}
		catch(AbnormalExitError e) {
			if (suppressSystemExit) {
				return false;
			}
			else {
				System.exit(1);
			}
		}
		return false;
	}

	protected BinaryCacheConnector connect(URI uri) {
		if ("extend".equals(uri.getScheme())) {
			String host = uri.getHost();
			int port = uri.getPort();
			BinaryCacheConnector connector = new BinaryCacheConnector();
			connector.addRemoteAddress(host, port);
			try {
				connector.connect();
				return connector;
			} catch (IOException e) {
				error("Cannot connect: " + e.toString());
			}
		}
		else {
			error("Unsupported scheme: " + uri.getScheme());
		}
		throw new Error("Unreachable");
	}
	
	private void error(String message) {
		System.err.println(message);
		throw new AbnormalExitError();		
	}

	protected BinaryCache getBinaryCache() {
		try {
			URI uri = new URI(cacheUrl);
			String path = uri.getPath();
			if (path == null) {
				error("No cache name in URL");
			}
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
			if (path.length() == 0) {
				error("No cache name in URL");
			}
			BinaryCacheConnector connector = connect(uri);
			return connector.getBinaryCache(path);
		} catch (URISyntaxException e) {
			error("Cannot parse cache URI: " + cacheUrl);			
		}		
		throw new Error("Unreachable");
	}

	protected NamedCache getObjectCache() {
		try {
			URI uri = new URI(cacheUrl);
			String path = uri.getPath();
			if (path == null) {
				error("No cache name in URL");
			}
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
			if (path.length() == 0) {
				error("No cache name in URL");
			}
			BinaryCacheConnector connector = connect(uri);
			return connector.getCache(path);
		} catch (URISyntaxException e) {
			error("Cannot parse cache URI: " + cacheUrl);			
		}		
		throw new Error("Unreachable");
	}
	
	public interface Cmd {
		void exec();
	}
	
	@Parameters(commandDescription = "Show size of cache")
	public class SizeCmd implements Cmd {

		@Override
		public void exec() {
			System.out.println(getObjectCache().size());
		}
	}
	
	public static enum ListType {
		KEYS,
		ENTRIES,
		VALUES
	}
	
	@Parameters(commandDescription = "List keys in cache")
	public class ListCmd implements Cmd {

		@Parameter(names = {"-pp", "--parse-pof"}, description = "Print POF structure")
		private boolean printPof = false;
		
		@Parameter(names = {"-s", "--show"}, description = "What to display [keys, values, entries]")
		private ListType mode = ListType.KEYS;
		
		@Override
		public void exec() {
			
			boolean binaryMode = false;
			if (printPof) {
				binaryMode = true;
			}
			
			NamedCache cache;
			
			if (binaryMode) {
				cache = getBinaryCache();
			}
			else {
				cache = getObjectCache();
			}

			if (mode == ListType.ENTRIES) {
				for(Object entry: cache.entrySet()) {
					Map.Entry<?, ?> e = (Entry<?, ?>) entry;
					System.out.println(printObject(e.getKey()) + " --> " + printObject(e.getValue()));
				}				
			}
			else if (mode == ListType.VALUES) {
				for(Object value: cache.values()) {
					System.out.println(printObject(value));
				}				
			}
			else {
				for(Object key: cache.keySet()) {
					System.out.println(printObject(key));
				}				
			}
		}
		
		public String printObject(Object bin) {
			if (printPof) {
				return printCompactPof((Binary)bin);
			}
			else {
				return String.valueOf(bin);
			}			
		}

		public String printPof(Binary bin) {
			List<PofEntry> entries = PofParser.parsePof(bin, new BlobSerializer());

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
				sb.append(path).append(", ").append(type).append(", ").append(value).append('|');
			}
			
			sb.setLength(sb.length() - 1);

			return sb.toString();
		}

		public String printCompactPof(Binary bin) {
			List<PofEntry> entries = PofParser.parsePof(bin, new BlobSerializer());
			
			PofFinePrinter fp = new PofFinePrinter();
			PofCompactPrinter cp = new PofCompactPrinter(fp); 
						
			return cp.format(entries);
		}
	}	
	
	public class Filter {
		
	}

	@Parameters(commandDescription = "Export binary cache content to a file")
	public class ExportCmd implements Cmd {

		@ParametersDelegate
		private DumpSink sink = new DumpSink();
		
		@Parameter(names = {"-bl", "--batch-limit"})
		private int batchLimit = 128;
		
		@Parameter(names = {"-pb", "--partition-block"}, description = "Max number of paration to query")
		private int partitionBlock = 0;
		
		@Override
		public void exec() {
			List<Binary> buffer = new ArrayList<Binary>(batchLimit);
			BinaryCache cache = getBinaryCache();
			if (partitionBlock < 1) {
				int partEst = cache.size() / 257;
				partEst = partEst > 0 ? partEst : 1;
				partitionBlock = (16 * batchLimit) / partEst;
				partitionBlock = partitionBlock > 0 ? partitionBlock : 1; 
			}
			int p = 0;
			int size = 0;
			while(true) {
				PartitionSet ps = new PartitionSet(p + partitionBlock);
				for(int i = 0; i != partitionBlock; ++i) {
					ps.add(p + i);
				}
				PartitionedFilter filter = new PartitionedFilter(AlwaysFilter.INSTANCE, ps);
				Set<Binary> keys;
				try {
					keys = new HashSet<Binary>(cache.binaryKeySet(filter));
				}
				catch(PortableException e) {
					if (e.getName().contains("ArrayIndexOutOfBoundsException")) {
						if (partitionBlock > 1) {
							partitionBlock = partitionBlock >> 1;
							continue;
						}
						else {
							// max partition reached
							break;
						}
					}
					else {
						error(e.toString());
						throw new Error("Unreachable");
					}
				}
				System.out.println("Partitions [" + p + "-" + (p + partitionBlock - 1) + "] - " + keys.size());
				if (!keys.isEmpty()) {
					Iterator<Binary> it = keys.iterator();
					while(it.hasNext()) {
						if (buffer.size() > batchLimit) {
							size += dump(cache, buffer);
						}
						buffer.add(it.next());
						it.remove();
					}
					size += dump(cache, buffer);
				}
				p += partitionBlock;
			}		
			try {
				sink.close();
				System.out.println("Entry count: " + size);
			} catch (IOException e) {
				error(e.toString());
			}
		}		

		@SuppressWarnings("unchecked")
		private int dump(BinaryCache cache, List<Binary> buffer) {
			int size = 0;
			Map<Binary, Binary> map = cache.getAll(buffer);
			for(Map.Entry<Binary, Binary> e: map.entrySet()) {
				if (e.getValue() != null) {
					++size;
					try {					
						sink.dump(e.getKey(), e.getValue());
					} catch (IOException ee) {
						error(ee.toString());
						try {
							sink.close();
						}
						catch(IOException eee) {
							// ignore
						}
					}
				}
			}
			buffer.clear();
			return size;
		}
	}	

	@Parameters(commandDescription = "Import binary content from file to a cache")
	public class ImportCmd implements Cmd {

		@ParametersDelegate
		private DumpSource source = new DumpSource();
		
		@Parameter(names = {"-bl", "--batch-limit"})
		private int batchLimit = 128;
		
		@Override
		public void exec() {
			CacheDumpSource source = this.source.getSource();
			BinaryCache cache = getBinaryCache();
			Map<Binary, Binary> buffer = new HashMap<Binary, Binary>(batchLimit);

			int size = 0;
			try {
				try {
					while(source.next()) {
						buffer.put(source.getKey(), source.getValue());
						if (buffer.size() >= batchLimit) {
							size += dump(cache, buffer);
						}				
					}
				} catch (IOException e) {
					error(e.toString());
					throw new Error("Unreachable");
				}		
				size += dump(cache, buffer);
			}
			finally {
				if (size > 0) {
					System.out.println("Imported: " + size + " entries");
				}				
			}
			try {
				this.source.close();
			} catch (IOException e) {
				// ignore
			}
		}		

		private int dump(BinaryCache cache, Map<Binary, Binary> buffer) {
			int size = buffer.size();
			cache.putAll(buffer);
			buffer.clear();
			return size;
		}
	}	
	
	public class DumpSink {
		
		@Parameter(names = {"-zf", "--zip-file"}, required = true, description = "Name of zip archive to write cache contents")
		private String zipName;

		@Parameter(names = {"-o", "--override"}, description = "Forces to override existing dump file")
		private boolean override = false;
		
		private FileOutputStream fileStream;
		private ZipFileCacheSink sink;
		
		public void dump(Binary key, Binary value) throws IOException {
			getSink().add(key, value);
		}
		
		public CacheDumpSink getSink() {
			if (sink == null) {
				File file = new File(zipName);
				if (file.exists()) {
					if (override) {
						if (!file.delete()) {
							error("Cannot delete file [" + zipName + "]");
							throw new Error("Unreachable");
						}
					}
					else {
						error("File [" + zipName + "] already exists");
						throw new Error("Unreachable");
					}
				}
				try {
					fileStream = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					error(e.toString());
					throw new Error("Unreachable");
				}
				sink = new ZipFileCacheSink(new ZipOutputStream(fileStream));				
			}
			return sink;
		}
		
		public void setScope(String name) {
			getSink();
			sink.setPrefix(name);
		}
		
		public void close() throws IOException {
			sink.close();
		}		
	}

	public class DumpSource {
		
		@Parameter(names = {"-zf", "--zip-file"}, required = true, description = "Name of zip archive to write cache contents")
		private String zipName;
		
		private ZipFileCacheSource source;
		
		public CacheDumpSource getSource() {
			if (source == null) {
				File file = new File(zipName);
				try {
					ZipInputStream zin = new ZipInputStream(new FileInputStream(file));
					source = new ZipFileCacheSource(zin);
				} catch (FileNotFoundException e) {
					error("File not found [" + zipName + "]");
				}
			}
			return source;
		}
		
		public void close() throws IOException {
			source.close();
		}		
	}
	
	@SuppressWarnings("serial")
	private static class AbnormalExitError extends Error {
		
	}	
}
