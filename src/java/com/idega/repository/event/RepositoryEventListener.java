package com.idega.repository.event;

import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

public interface RepositoryEventListener extends EventListener {

	@Override
	public void onEvent(EventIterator events);

}
