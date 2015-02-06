/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2014 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.extractor;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.dao.NodeDocumentDAO;

/**
 * @author pavila
 */
public class TextExtractorWorker extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(TextExtractorWorker.class);
	private static List<TextExtractorWork> inProgress = new ArrayList<TextExtractorWork>();
	private static Calendar lastExecution = null;
	private static volatile boolean running = false;
	
	/**
	 * Get in progress extraction works.
	 */
	public static List<TextExtractorWork> getInProgressWorks() throws DatabaseException {
		return inProgress;
	}
	
	/**
	 * Get pending extraction works.
	 */
	public static List<TextExtractorWork> getPendingWorks(int max) throws DatabaseException {
		return NodeDocumentDAO.getInstance().getPendingExtractions(max);
	}
	
	/**
	 * Get pending extraction work size.
	 */
	public static long getPendingSize() throws DatabaseException {
		return NodeDocumentDAO.getInstance().getPendingExtractionSize();
	}
	
	/**
	 * Return if text extraction worker is running.
	 */
	public static boolean isRunning() {
		return running;
	}
	
	/**
	 * Return text extractor worker last execution 
	 */
	public static Calendar lastExecution() {
		return lastExecution;
	}
	
	/**
	 * Document text extraction batch
	 */
	@Override
	public void run() {
		if (running) {
			log.warn("*** Text extraction already running ***");
		} else {
			running = true;
			log.debug("*** Begin text extraction ***");
			
			try {
				if (!Config.SYSTEM_READONLY) {
					processQueue(null, Config.MANAGED_TEXT_EXTRACTION_BATCH);
				} else {
					log.warn("*** Text extraction disabled because system is readonly ***");
				}
			} finally {
				running = false;
			}
			
			lastExecution = Calendar.getInstance();
			log.debug("*** End text extraction ***");
		}
	}
	
	/**
	 * Force text extraction of every document in the repository
	 */
	public void rebuildWorker(MassIndexerProgressMonitor monitor) throws PathNotFoundException, DatabaseException, InterruptedException {
		if (running) {
			log.warn("*** Text extraction already running ***");
		} else {
			running = true;
			log.debug("*** Begin massive text extraction ***");
			
			try {
				// Clear pending extraction queue
				NodeDocumentDAO.getInstance().resetAllPendingExtractionFlags();
				
				// Process queue
				while (NodeDocumentDAO.getInstance().hasPendingExtractions()) {
					processQueue(monitor, Config.MANAGED_TEXT_EXTRACTION_BATCH);
					Thread.sleep(750);
					System.gc();
				}
			} finally {
				running = false;
			}
			
			log.debug("*** End massive text extraction ***");
		}
	}
	
	/**
	 * Process text extraction pending queue
	 */
	private void processQueue(MassIndexerProgressMonitor monitor, int maxResults) {
		if (Config.MANAGED_TEXT_EXTRACTION_CONCURRENT) {
			log.debug("Processing queue concurrently with {} processors", Config.AVAILABLE_PROCESSORS);
			processConcurrent(monitor, maxResults);
		} else {
			processSerial(monitor, maxResults);
		}
	}
	
	/**
	 * Process queue serial
	 */
	private void processSerial(MassIndexerProgressMonitor monitor, int maxResults) {
		log.debug("processSerial({}, {})", monitor, maxResults);
		long begin = System.currentTimeMillis();
		
		try {
			for (TextExtractorWork work : NodeDocumentDAO.getInstance().getPendingExtractions(maxResults)) {
				log.info("processSerial.Working on {}", work);
				inProgress.add(work);
				NodeDocumentDAO.getInstance().textExtractorHelper(work);
				inProgress.clear();
				
				if (monitor != null) {
					monitor.documentsAdded(1);
				}
			}
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		} finally {
			inProgress.clear();
		}
		
		log.trace("processSerial.Time: {}", System.currentTimeMillis() - begin);
	}
	
	/**
	 * Process queue concurrent
	 */
	private void processConcurrent(MassIndexerProgressMonitor monitor, int maxResults) {
		log.info("processConcurrent({}, {})", monitor, maxResults);
		long begin = System.currentTimeMillis();
		int pool = 0;
		
		try {
			List<TextExtractorWork> pendExts = NodeDocumentDAO.getInstance().getPendingExtractions(maxResults);
			int pendExtSize = pendExts.size();
			
			for (Iterator<TextExtractorWork> it = pendExts.iterator(); it.hasNext(); ) {
				ExecutorService executor = Executors.newFixedThreadPool(Config.MANAGED_TEXT_EXTRACTION_POOL_SIZE);
				int totalPools = (int) Math.ceil((double) pendExtSize / Config.MANAGED_TEXT_EXTRACTION_POOL_THREADS);
				log.info("Begin pool {} of {}", ++pool, totalPools);
				
				for (int i=0; i < Config.MANAGED_TEXT_EXTRACTION_POOL_THREADS; i++) {
					if (it.hasNext()) {
						TextExtractorWork work = it.next();
						inProgress.add(work);
						executor.execute(new TextExtractorThread(work));
						log.info("processConcurrent.added {} documents", i);
						
						if (monitor != null) {
							monitor.documentsAdded(1);
						}
					}
				}
				
				log.info("End pool {} of {}", pool, totalPools);
				executor.shutdown();
				log.info("### All threads shutdown requested ###");
				
				try {
					for (int i=0; !executor.awaitTermination(Config.MANAGED_TEXT_EXTRACTION_POOL_TIMEOUT, TimeUnit.MINUTES); i++) {
						log.info("### Awaiting for pool tasks termination... ({}) ###", i);
					}
				} catch (InterruptedException e) {
					log.warn("### Exception awaiting for pool tasks termination: {} ###", e.getMessage());
				}
				
				log.info("### All threads have finished ###");
				inProgress.clear();
			}
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		} finally {
			inProgress.clear();
		}
		
		log.trace("processConcurrent.Time: {}", System.currentTimeMillis() - begin);
	}
}
