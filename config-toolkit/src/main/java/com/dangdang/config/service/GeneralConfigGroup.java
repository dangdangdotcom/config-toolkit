package com.dangdang.config.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.config.service.observer.IObserver;
import com.dangdang.config.service.observer.ISubject;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public abstract class GeneralConfigGroup extends ConcurrentHashMap<String, String> implements ConfigGroup, ISubject {

	private static final long serialVersionUID = 1L;

	private ConfigGroup internalConfigGroup;

	protected GeneralConfigGroup(ConfigGroup internalConfigGroup) {
		this.internalConfigGroup = internalConfigGroup;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneralConfigGroup.class);

	/**
	 * 配置组的最后加载时间
	 */
	private long lastLoadTime;

	public long getLastLoadTime() {
		return lastLoadTime;
	}

	@Override
	public final String get(String key) {
		String val = super.get(key);
		if (val == null && internalConfigGroup != null) {
			val = internalConfigGroup.get(key);
		}
		return val;
	}

	@Override
	public final String get(Object key) {
		return get(key.toString());
	}

	@Override
	public final void putAll(Map<? extends String, ? extends String> configs) {
		lastLoadTime = System.currentTimeMillis();
		if (configs != null && configs.size() > 0) {
			// clear
			if (this.size() > 0) {
				final Set<String> newKeys = Sets.newHashSet();
				newKeys.addAll(configs.keySet());
				final Iterable<String> redundances = Iterables.filter(Sets.newHashSet(this.keySet()), new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return !newKeys.contains(input);
					}
				});
				for (String redundance : redundances) {
					super.remove(redundance);
				}
			}

			// update
			for (Map.Entry<? extends String, ? extends String> entry : configs.entrySet()) {
				this.put(entry.getKey(), entry.getValue());
			}

		}
	}

	@Override
	public final String put(String key, String value) {
		String preValue = super.get(key);
		if (!Objects.equal(preValue, value)) {
			LOGGER.debug("Key {} change from {} to {}", key, preValue, value);
			super.put(key, value);

			// If value change, notify
			if (preValue != null) {
				notify(key, value);
			}
		}
		return preValue;
	}

	/**
	 * 观察者列表
	 */
	private final List<IObserver> watchers = Lists.newArrayList();

	@Override
	public void register(final IObserver watcher) {
		watchers.add(Preconditions.checkNotNull(watcher));
	}

	@Override
	public void notify(final String key, final String value) {
		for (final IObserver watcher : watchers) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					watcher.notified(key, value);
				}
			}).start();
		}
	}

	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

}
