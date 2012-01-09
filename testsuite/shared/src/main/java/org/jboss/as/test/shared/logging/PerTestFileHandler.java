/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


package org.jboss.as.test.shared.logging;

import java.util.logging.ErrorManager;
import org.jboss.logmanager.handlers.FileHandler;
import org.jboss.logmanager.ExtLogRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;








/**
 * A file handler which rotates the log file when an expression with system properties changes.
 * 
 * Implementation of JBoss Logging Logmanager's ExtHandler.
 * Similar to org.jboss.logmanager.handlers.PeriodicRotatingFileHandler.
 *
 * @author Ondrej Zizka
 */
public class PerTestFileHandler extends FileHandler {

    private String currentValue;
		
		/**  Path with system properties references: "${arq.currentTest}-server-${arq.containerId}.txt" */
		private String fileNameFormat = "${arq.currentTest}-server-${arq.containerId}.txt";


		/**  Parsed parts (to speed up a bit). */
		private List<Object> expressionParts = new LinkedList();
		
		

    /**
     * Construct a new instance with no formatter and no output file.
     */
    public PerTestFileHandler() {
    }

    /**
     * Construct a new instance with the given output file.
     *
     * @param fileName the file name
     *
     * @throws java.io.FileNotFoundException if the file could not be found on open
     */
    public PerTestFileHandler(final String fileName) throws FileNotFoundException {
        super(fileName);
    }

    /**
     * Construct a new instance with the given output file and append setting.
     *
     * @param fileName the file name
     * @param append {@code true} to append, {@code false} to overwrite
     *
     * @throws java.io.FileNotFoundException if the file could not be found on open
     */
    public PerTestFileHandler(final String fileName, final boolean append) throws FileNotFoundException {
        super(fileName, append);
    }

    /**
     * Construct a new instance with the given output file.
     *
     * @param file the file
     * @param suffix the format suffix to use
     *
     * @throws java.io.FileNotFoundException if the file could not be found on open
     */
    public PerTestFileHandler(final File file, final String expr) throws FileNotFoundException {
        super(file);
        this.parseExpression(expr);
    }

    /**
     * Construct a new instance with the given output file and append setting.
     *
     * @param file the file
     * @param suffix the format suffix to use
     * @param append {@code true} to append, {@code false} to overwrite
     * @throws java.io.FileNotFoundException if the file could not be found on open
     */
    public PerTestFileHandler(final File file, final String expr, final boolean append) throws FileNotFoundException {
        super(file, append);
        this.parseExpression(expr);
    }

		
		

		// Properties.
		
		public void setFileNameFormat(String fileNameFormat) {
				this.fileNameFormat = fileNameFormat;
		}
		
		
		
		
    /**
		 * {@inheritDoc}  Switches log file if the expression's value changes.
		 */
    protected void preWrite( final ExtLogRecord record ) {
        final String newValue = this.evaluateValue();
        if( ! newValue.equals( this.currentValue ) ){
						this.currentValue = newValue;
            switchLogFile( newValue );
        }
    }


		/**
		 *  Switches the file to log messages to.
		 *  @param logFilePath 
		 */
    private void switchLogFile( String logFilePath ) {
        synchronized (outputLock) {
						File newLogFile = new File( logFilePath );
						try {
								final File file = getFile();
								// First, close the original file.
								setFile(null);
								// Start new file.
								setFile(file);
						} catch (FileNotFoundException e) {
								reportError("Unable to open new log file: " + newLogFile.getAbsolutePath(), e, ErrorManager.OPEN_FAILURE);
						}
        }
    }

		/**
		 *  Pre-parses the expression (to speed up).
		 */
    private void parseExpression( String expr ) {
				
				List<Object> parts = new LinkedList();
				try {
						String val = this.fileNameFormat;
						StringTokenizer tok = new StringTokenizer(val, "${");
						while( tok.hasMoreTokens() ){
								String str1 = tok.nextToken();
								String prop = tok.nextToken("}");
								parts.add( str1 );
								parts.add( new PropRef(prop) );
						}
				}
				catch( NoSuchElementException ex ){
						throw new IllegalArgumentException("Error parsing expression: " + expr);
				}
				this.expressionParts = parts;
		}
		
		/**
		 *  Evaluates the expression using current system properties.
		 *  Iterates through pre-parsed parts.
		 */
    private String evaluateValue() {
				StringBuilder sb = new StringBuilder();
				for( Object part : this.expressionParts ){
						if( part instanceof String ){
								sb.append( (String) part);
						}else{
								String propName = ((PropRef) part).propName;
								sb.append( System.getProperty(propName, "$"+propName) );
						}
				}
				return sb.toString();
    }
		

}// class
/**
 * Property reference - just a string wrapper.
 * @author ondra
 */
class PropRef {
		public final String propName;

		public PropRef(String propName) {
				this.propName = propName;
		}
}
