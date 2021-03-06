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

/**
 * Packageless stub to shorten command line 
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class CacheCli {
	
	public static void main(String[] args) {
		System.setProperty("tangosol.coherence.log.level", "1");
		org.gridkit.coherence.cachecli.CacheCli.main(args);
	}
}
