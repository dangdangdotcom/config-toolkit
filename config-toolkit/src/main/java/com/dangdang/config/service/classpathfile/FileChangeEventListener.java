package com.dangdang.config.service.classpathfile;

import java.nio.file.WatchService;

import com.dangdang.config.service.ConfigGroup;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class FileChangeEventListener implements Runnable {

	private WatchService watcher;

	private ConfigGroup configGroup;

	public FileChangeEventListener(WatchService watcher, ConfigGroup configGroup) {
		super();
		this.watcher = watcher;
		this.configGroup = configGroup;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
