package org.gridkit.coherence.cachecli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gridkit.coherence.extend.binary.BinaryCache;
import org.gridkit.coherence.extend.binary.BinaryCacheConnector;

public class BinCacheTool {

	private Map<String, BinaryCacheConnector> connections = new HashMap<String, BinaryCacheConnector>();
	private BinaryCache activeCache;
	
	public static void main(String[] args) {
		BinCacheTool tool = new BinCacheTool();
		tool.execute(Arrays.asList(args));
	}

	private void execute(Iterable<String> asList) {
		
		
	}
	
	private static class InputParser {
		
		public String nextCo
		
	}
	
	private static class CommandLine {
		
		private List
		
	}
}
