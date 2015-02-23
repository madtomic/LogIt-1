package io.github.lucaseasedup.logit.config.observers;

import io.github.lucaseasedup.logit.LogItCore;
import io.github.lucaseasedup.logit.config.Property;
import io.github.lucaseasedup.logit.config.PropertyObserver;

public class AutoLogoutObserver extends PropertyObserver
{
	@Override
	public void update(Property p)
	{
		LogItCore.getInstance().getSessionManager().updateAutologoutSettings();
	}
}
