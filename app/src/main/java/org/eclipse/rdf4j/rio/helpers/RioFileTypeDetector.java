/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package org.eclipse.rdf4j.rio.helpers;

import java.io.File;
import java.io.IOException;

import org.eclipse.java.nio.spi.FileTypeDetector;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.Rio;

/**
 * An implementation of FileTypeDetector which uses the {@link RDFParserRegistry} to find supported file types
 * and their extensions.
 *
 * @author Peter Ansell
 */
public class RioFileTypeDetector extends FileTypeDetector {

	public RioFileTypeDetector() {
		super();
	}

	@Override
	public String probeContentType(File path)
		throws IOException
	{
		RDFFormat result = Rio.getParserFormatForFileName(path.getAbsolutePath());

		if (result != null) {
			return result.getDefaultMIMEType();
		}

		// Specification says to return null if we could not
		// conclusively determine the file type
		return null;
	}

}
