package org.gridkit.coherence.cachecli;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gridkit.coherence.extend.binary.BinaryCache;
import org.gridkit.coherence.extend.binary.BinaryCacheConnector;
import org.gridkit.coherence.extend.binary.BlobSerializer;
import org.gridkit.coherence.misc.pofviewer.PofEntry;
import org.gridkit.coherence.misc.pofviewer.PofFinePrinter;
import org.gridkit.coherence.misc.pofviewer.PofParser;
import org.gridkit.coherence.misc.pofviewer.PofPath;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Binary;

public class CacheCli {

	@Parameter(names = {"--help"}, help = true)
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
	}
	
	private void start(String[] args) {
		JCommander parser = new JCommander(this);
		for(String cmd: commands.keySet()) {
			parser.addCommand(cmd, commands.get(cmd));
		}
		
		parser.parse(args);
		Cmd cmd = commands.get(parser.getParsedCommand());
		
		if (cmd == null) {
			parser.usage();
			System.exit(1);
		}
		else {
			cmd.exec();
		}
		System.exit(0);
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
		System.exit(1);		
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
	
	@Parameters(commandDescription = "List keys in cache")
	public class ListCmd implements Cmd {

		@Parameter(names = {"-pp", "--parse-pof"}, description = "Print POF structure")
		private boolean printPof = false;
		
		@Parameter(names = {"-v", "--values"}, description = "List values, not keys")
		private boolean valueMode = false;

		@Parameter(names = {"-e", "--entries"}, description = "List entries, not keys")
		private boolean entryMode = false;

		
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

			if (entryMode) {
				for(Object entry: cache.entrySet()) {
					Map.Entry<?, ?> e = (Entry<?, ?>) entry;
					System.out.println(printObject(e.getKey()) + " --> " + printObject(e.getValue()));
				}				
			}
			else if (valueMode) {
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
				return printPof((Binary)bin);
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
	}	
	
	public class Filter {
		
	}
	
	public interface DumpWriter {
		
		public void add(Binary key, Binary value) throws IOException;
		
		public void close() throws IOException;
	}
}
