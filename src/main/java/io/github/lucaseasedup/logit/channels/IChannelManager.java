package io.github.lucaseasedup.logit.channels;

public interface IChannelManager
{
	public void stop();
	public boolean canSendPackets();
	public boolean isRunning();
}
