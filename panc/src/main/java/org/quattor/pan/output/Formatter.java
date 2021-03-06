/*
 Copyright (c) 2006 Charles A. Loomis, Jr, Cedric Duprilot, and
 Centre National de la Recherche Scientifique (CNRS).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $HeadURL: https://svn.lal.in2p3.fr/LCG/QWG/panc/trunk/src/org/quattor/pan/output/Formatter.java $
 $Id: Formatter.java 2986 2008-03-01 10:10:06Z loomis $
 */

package org.quattor.pan.output;

import java.net.URI;

import org.quattor.pan.tasks.FinalResult;

/**
 * 
 * @author duprilot
 * 
 */
public interface Formatter {

	public URI getResultURI(String objectName);

	/**
	 * Returns the name of the formatter that will be used to identify it. This
	 * key is used as the value for the command line and for the ant task. The
	 * returned key must not be null.
	 * 
	 * @return String identifying this formatter
	 */
	public String getFormatKey();

	public void write(FinalResult result, URI outputURI) throws Exception;

}
