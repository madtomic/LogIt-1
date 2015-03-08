package io.github.lucaseasedup.logit;

import java.io.IOException;

public interface IPlugin
{
	public void reloadMessages(String prefix) throws IOException;
	public String getMessage(String label);
}
