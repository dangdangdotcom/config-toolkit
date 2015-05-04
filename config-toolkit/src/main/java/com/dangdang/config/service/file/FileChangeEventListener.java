package com.dangdang.config.service.file;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Watcher for file changes
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileChangeEventListener implements Runnable {

	private WatchService watcher;

	private FileConfigGroup configGroup;

	private Path watchedFile;

	public FileChangeEventListener(WatchService watcher, FileConfigGroup configGroup, Path watchedFile) {
		super();
		this.watcher = watcher;
		this.configGroup = configGroup;
		this.watchedFile = watchedFile;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FileChangeEventListener.class);

	@Override
	public void run() {
		while (true) {
			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				// This key is registered only for ENTRY_MODIFY events,
				if (kind != StandardWatchEventKinds.ENTRY_MODIFY) {
					continue;
				}

				// The filename is the context of the event.
				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();

				LOGGER.debug("File {} changed.", filename);

				if (isSameFile(filename, watchedFile)) {
					configGroup.initConfigs();
				}

			}
			
			boolean status = key.reset();
			if(!status) {
				break;
			}
		}
	}

	private boolean isSameFile(Path file1, Path file2) {
		return file1.getFileName().equals(file2.getFileName());
	}

}
