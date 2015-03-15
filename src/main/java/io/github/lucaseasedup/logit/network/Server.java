package io.github.lucaseasedup.logit.network;


import io.github.lucaseasedup.logit.Core;
import io.github.lucaseasedup.logit.util.ExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class Server implements INetworkManager
{
	@Override
	public void start()
	{
		try
		{
			server = new ServerSocket(1337);
			socket = server.accept();
			input = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			Core.getCore().log(Level.SEVERE, "FAILED TO BIND TO PORT!");
			Core.getCore().log(Level.SEVERE, "The exception was: "+e.getCause());
			return;
		}

	}

	@Override
	public void stop()
	{

	}

	@Override
	public boolean isConnected()
	{
		return isConnected;
	}

	private class Listener implements Runnable
	{
		@Override
		public void run()
		{
			while(isConnected && socket.isConnected() && !server.isClosed())
			{
				Object o;
				try
				{
					o = input.readObject();
				}
				catch (IOException | ClassNotFoundException e)
				{
					ExceptionHandler.handleException(e);
					continue;
				}

				if(o == null)
					continue;


			}
			Core.getCore().log(Level.INFO, "Stopped listening for packets.");
			isConnected = false;
		}
	}

	private ServerSocket server;
	private Socket socket;
	private ObjectInputStream input;
	private boolean isConnected = false;
}
