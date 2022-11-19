/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.site;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.net.NetworkClient;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.TransferProtocolClient;

/**
 * This class implements the FTP client.
 *
 * @author Jonathan Payne
 * @version %I%, %G%
 */

public class FtpClient extends TransferProtocolClient
{
	/**
	 * A class to signal exception from the RegexpPool class.
	 *
	 * @author James Gosling
	 */

	static public class REException extends Exception
	{

		private static final long serialVersionUID = 4656584872733646963L;

		REException(String s)
		{
			super(s);
		}
	}

	/**
	 * A class to define actions to be performed when a regular expression match
	 * occurs.
	 *
	 * @author James Gosling
	 */

	public interface RegexpTarget
	{
		/**
		 * Gets called when a pattern in a RegexpPool matches.
		 * This method is called by RegexpPool.match() who passes the return
		 * value from found() back to its caller.
		 *
		 * @param remainder the string that matched the * in the pattern.
		 */
		Object found(String remainder);
	}

	/**
	 * A class to represent a pool of regular expressions.  A string
	 * can be matched against the whole pool all at once.  It is much
	 * faster than doing individual regular expression matches one-by-one.
	 *
	 * @author James Gosling
	 */

	static public class RegexpPool
	{
		private final RegexpNode prefixMachine = new RegexpNode();
		private final RegexpNode suffixMachine = new RegexpNode();
		private static final int BIG = 0x7FFFFFFF;
		private int lastDepth = BIG;

		public RegexpPool()
		{
		}

		/**
		 * Add a regular expression to the pool of regular expressions.
		 *
		 * @param re  The regular expression to add to the pool.
		 *            For now, only handles strings that either begin or end with
		 *            a '*'.
		 * @param ret The object to be returned when this regular expression is
		 *            matched.  If ret is an instance of the RegexpTarget class, ret.found
		 *            is called with the string fragment that matched the '*' as its
		 *            parameter.
		 * @throws REException error
		 */
		public void add(String re, Object ret) throws REException
		{
			add(re, ret, false);
		}

		/**
		 * Replace the target for the regular expression with a different
		 * target.
		 *
		 * @param re  The regular expression to be replaced in the pool.
		 *            For now, only handles strings that either begin or end with
		 *            a '*'.
		 * @param ret The object to be returned when this regular expression is
		 *            matched.  If ret is an instance of the RegexpTarget class, ret.found
		 *            is called with the string fragment that matched the '*' as its
		 *            parameter.
		 */
		public void replace(String re, Object ret)
		{
			try
			{
				add(re, ret, true);
			}
			catch (Exception e)
			{
				// should never occur if replace is true
			}
		}

		/**
		 * Delete the regular expression and its target.
		 *
		 * @param re The regular expression to be deleted from the pool.
		 *           must begin or end with a '*'
		 * @return target - the old target.
		 */
		public Object delete(String re)
		{
			Object o = null;
			RegexpNode p = this.prefixMachine;
			RegexpNode best = p;
			int len = re.length() - 1;
			int i;
			boolean prefix = true;

			if (!re.startsWith("*") || !re.endsWith("*"))
			{
				len++;
			}

			if (len <= 0)
			{
				return null;
			}

			/* March forward through the prefix machine */
			for (i = 0; p != null; i++)
			{
				if (p.result != null && p.depth < BIG && (!p.exact || i == len))
				{
					best = p;
				}
				if (i >= len)
				{
					break;
				}
				p = p.find(re.charAt(i));
			}

			/* march backward through the suffix machine */
			p = this.suffixMachine;
			for (i = len; --i >= 0 && p != null; )
			{
				if (p.result != null && p.depth < BIG)
				{
					best = p;
				}
				p = p.find(re.charAt(i));
			}

			// delete only if there is an exact match
			if (re.equals(best.re))
			{
				o = best.result;
				best.result = null;

			}
			return o;
		}

		/**
		 * Search for a match to a string & return the object associated
		 * with it with the match.  When multiple regular expressions
		 * would match the string, the best match is returned first.
		 * The next best match is returned the next time matchNext is
		 * called.
		 *
		 * @param s The string to match against the regular expressions
		 *          in the pool.
		 * @return null on failure, otherwise the object associated with
		 * the regular expression when it was added to the pool.
		 * If the object is an instance of RegexpTarget, then
		 * the return value is the result from calling
		 * return.found(string_that_matched_wildcard).
		 */
		public Object match(String s)
		{
			return matchAfter(s, BIG);
		}

		/**
		 * Identical to match except that it will only find matches to
		 * regular expressions that were added to the pool <i>after</i>
		 * the last regular expression that matched in the last call
		 * to match() or matchNext()
		 */
		public Object matchNext(String s)
		{
			return matchAfter(s, this.lastDepth);
		}

		private void add(String re, Object ret, boolean replace) throws REException
		{
			int len = re.length();
			RegexpNode p;
			if (re.charAt(0) == '*')
			{
				p = this.suffixMachine;
				while (len > 1)
				{
					p = p.add(re.charAt(--len));
				}
			}
			else
			{
				boolean exact = false;
				if (re.charAt(len - 1) == '*')
				{
					len--;
				}
				else
				{
					exact = true;
				}
				p = this.prefixMachine;
				for (int i = 0; i < len; i++)
				{
					p = p.add(re.charAt(i));
				}
				p.exact = exact;
			}

			if (p.result != null && !replace)
			{
				throw new REException(re + " is a duplicate");
			}

			p.re = re;
			p.result = ret;
		}

		private Object matchAfter(String s, int lastMatchDepth)
		{
			RegexpNode p = this.prefixMachine;
			RegexpNode best = p;
			int bst = 0;
			int bend = 0;
			int len = s.length();
			int i;
			if (len == 0)
			{
				return null;
			}
			/* March forward through the prefix machine */
			for (i = 0; p != null; i++)
			{
				if (p.result != null && p.depth < lastMatchDepth && (!p.exact || i == len))
				{
					this.lastDepth = p.depth;
					best = p;
					bst = i;
					bend = len;
				}
				if (i >= len)
				{
					break;
				}
				p = p.find(s.charAt(i));
			}
			/* march backward through the suffix machine */
			p = this.suffixMachine;
			for (i = len; --i >= 0 && p != null; )
			{
				if (p.result != null && p.depth < lastMatchDepth)
				{
					this.lastDepth = p.depth;
					best = p;
					bst = 0;
					bend = i + 1;
				}
				p = p.find(s.charAt(i));
			}
			Object o = best.result;
			if (o instanceof RegexpTarget)
			{
				o = ((RegexpTarget) o).found(s.substring(bst, bend));
			}
			return o;
		}

		/**
		 * Resets the pool so that the next call to matchNext looks
		 * at all regular expressions in the pool.  match(s); is equivalent
		 * to reset(); matchNext(s);
		 * <p><b>Multithreading note:</b> reset/nextMatch leave state in the
		 * regular expression pool.  If multiple threads could be using this
		 * pool this way, they should be syncronized to avoid race hazards.
		 * match() was done in such a way that there are no such race
		 * hazards: multiple threads can be matching in the same pool
		 * simultaneously.
		 */
		public void reset()
		{
			this.lastDepth = BIG;
		}

		/**
		 * Print this pool to standard output
		 */
		public void print(PrintStream out)
		{
			out.print("Regexp pool:\n");
			if (this.suffixMachine.firstchild != null)
			{
				out.print(" Suffix machine: ");
				this.suffixMachine.firstchild.print(out);
				out.print("\n");
			}
			if (this.prefixMachine.firstchild != null)
			{
				out.print(" Prefix machine: ");
				this.prefixMachine.firstchild.print(out);
				out.print("\n");
			}
		}

	}

	/* A node in a regular expression finite state machine. */
	static class RegexpNode
	{
		final char c;
		RegexpNode firstchild;
		RegexpNode nextsibling;
		final int depth;
		boolean exact;
		Object result;
		String re = null;

		RegexpNode()
		{
			this.c = '#';
			this.depth = 0;
		}

		RegexpNode(char C, int depth)
		{
			this.c = C;
			this.depth = depth;
		}

		RegexpNode add(char C)
		{
			RegexpNode p = this.firstchild;
			if (p == null)
			{
				p = new RegexpNode(C, this.depth + 1);
			}
			else
			{
				while (p != null)
				{
					if (p.c == C)
					{
						return p;
					}
					else
					{
						p = p.nextsibling;
					}
				}
				p = new RegexpNode(C, this.depth + 1);
				p.nextsibling = this.firstchild;
			}
			this.firstchild = p;
			return p;
		}

		RegexpNode find(char C)
		{
			for (RegexpNode p = this.firstchild; p != null; p = p.nextsibling)
			{
				if (p.c == C)
				{
					return p;
				}
			}
			return null;
		}

		void print(PrintStream out)
		{
			if (this.nextsibling != null)
			{
				RegexpNode p = this;
				out.print("(");
				while (p != null)
				{
					out.write(p.c);
					if (p.firstchild != null)
					{
						p.firstchild.print(out);
					}
					p = p.nextsibling;
					out.write(p != null ? '|' : ')');
				}
			}
			else
			{
				out.write(this.c);
				if (this.firstchild != null)
				{
					this.firstchild.print(out);
				}
			}
		}
	}

	public static final int FTP_PORT = 21;

	static final int FTP_SUCCESS = 1;

	static final int FTP_TRY_AGAIN = 2;

	static final int FTP_ERROR = 3;

	/**
	 * remember the ftp server name because we may need it
	 */
	private String serverName = null;

	/**
	 * socket for data transfer
	 */
	private boolean replyPending = false;

	private boolean binaryMode = false;

	private boolean loggedIn = false;

	/**
	 * regexp pool of hosts for which we should connect directly, not Proxy these are intialized from a property.
	 */
	private static RegexpPool nonProxyHostsPool = null;

	/**
	 * The string source of nonProxyHostsPool
	 */
	private static String nonProxyHostsSource = null;

	/**
	 * last command issued
	 */
	String command;

	/**
	 * The last reply code from the ftp daemon.
	 */
	int lastReplyCode;

	/**
	 * Welcome message from the server, if any.
	 */
	public String welcomeMsg;

	/* these methods are used to determine whether ftp urls are sent to */
	/* an http server instead of using a direct connection to the */
	/* host. They aren't used directly here. */

	/**
	 * @return if the networking layer should send ftp connections through a proxy
	 */
	public static boolean getUseFtpProxy()
	{
		// if the ftp.proxyHost is set, use it!
		return FtpClient.getFtpProxyHost() != null;
	}

	/**
	 * @return the host to use, or null if none has been specified
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static String getFtpProxyHost()
	{
		return (String) java.security.AccessController.doPrivileged((PrivilegedAction) () -> {
			String result = System.getProperty("ftp.proxyHost"); 
			if (result == null)
			{
				result = System.getProperty("ftpProxyHost"); 
			}
			if (result == null)
			{
				// as a last resort we use the general one if ftp.useProxy
				// is true
				if (Boolean.getBoolean("ftp.useProxy")) 
				{
					result = System.getProperty("proxyHost"); 
				}
			}
			return result;
		});
	}

	/**
	 * @return the proxy port to use. Will default reasonably if not set.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static int getFtpProxyPort()
	{
		final int[] result = {80};
		java.security.AccessController.doPrivileged((PrivilegedAction) () -> {

			String tmp = System.getProperty("ftp.proxyPort"); 
			if (tmp == null)
			{
				// for compatibility with 1.0.2
				tmp = System.getProperty("ftpProxyPort"); 
			}
			if (tmp == null)
			{
				// as a last resort we use the general one if ftp.useProxy
				// is true
				if (Boolean.getBoolean("ftp.useProxy")) 
				{
					tmp = System.getProperty("proxyPort"); 
				}
			}
			if (tmp != null)
			{
				result[0] = Integer.parseInt(tmp);
			}
			return null;
		});
		return result[0];
	}

	public static boolean matchNonProxyHosts(final String host)
	{
		synchronized (FtpClient.class)
		{
			final String rawList = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("ftp.nonProxyHosts")); 
			if (rawList == null)
			{
				FtpClient.nonProxyHostsPool = null;
			}
			else
			{
				if (!rawList.equals(FtpClient.nonProxyHostsSource))
				{
					final RegexpPool pool = new RegexpPool();
					final StringTokenizer st = new StringTokenizer(rawList, "|", false); 
					try
					{
						while (st.hasMoreTokens())
						{
							pool.add(st.nextToken().toLowerCase(), Boolean.TRUE);
						}
					}
					catch (final REException ex)
					{
						System.err.println("Error in http.nonProxyHosts system property: " + ex); 
					}
					FtpClient.nonProxyHostsPool = pool;
				}
			}
			FtpClient.nonProxyHostsSource = rawList;
		}

		if (FtpClient.nonProxyHostsPool == null)
		{
			return false;
		}

		return FtpClient.nonProxyHostsPool.match(host) != null;
	}

	/**
	 * issue the QUIT command to the FTP server and close the connection.
	 *
	 * @throws FtpProtocolException if an error occured
	 */
	@Override
	public void closeServer() throws IOException
	{
		if (serverIsOpen())
		{
			issueCommand("QUIT"); 
			super.closeServer();
		}
	}

	/**
	 * Send a command to the FTP server.
	 *
	 * @param cmd String containing the command
	 * @return reply code
	 * @throws FtpProtocolException if an error occured
	 */
	protected int issueCommand(final String cmd) throws IOException
	{
		this.command = cmd;

		int reply;

		while (this.replyPending)
		{
			this.replyPending = false;
			if (readReply() == FtpClient.FTP_ERROR)
			{
				throw new FtpProtocolException("Error reading FTP pending reply\n"); 
			}
		}
		do
		{
			sendServer(cmd + "\r\n"); 
			reply = readReply();
		}
		while (reply == FtpClient.FTP_TRY_AGAIN);
		return reply;
	}

	/**
	 * Send a command to the FTP server and check for success.
	 *
	 * @param cmd String containing the command
	 * @throws FtpProtocolException if an error occured
	 */
	protected void issueCommandCheck(final String cmd) throws IOException
	{
		if (issueCommand(cmd) != FtpClient.FTP_SUCCESS)
		{
			throw new FtpProtocolException(cmd + ":" + getResponseString()); 
		}
	}

	/**
	 * Read the reply from the FTP server.
	 *
	 * @return FTP_SUCCESS or FTP_ERROR depending on success
	 * @throws FtpProtocolException if an error occured
	 */
	protected int readReply() throws IOException
	{
		this.lastReplyCode = readServerResponse();

		switch (this.lastReplyCode / 100)
		{
			case 1:
				this.replyPending = true;
				/* falls into ... */

			case 2:
			case 3:
				return FtpClient.FTP_SUCCESS;

			case 5:
				if (this.lastReplyCode == 530)
				{
					if (!this.loggedIn)
					{
						throw new FtpLoginException("Not logged in"); 
					}
					return FtpClient.FTP_ERROR;
				}
				if (this.lastReplyCode == 550)
				{
					throw new FileNotFoundException(this.command + ": " + getResponseString()); 
				}
		}

		/* this statement is not reached */
		return FtpClient.FTP_ERROR;
	}

	/**
	 * Tries to open a Data Connection in "PASSIVE" mode by issuing a EPSV or PASV command then opening a Socket to the specified address & port
	 *
	 * @return the opened socket
	 * @throws FtpProtocolException if an error occurs when issuing the PASV command to the ftp server.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected Socket openPassiveDataConnection() throws IOException
	{
		String serverAnswer;
		int port;
		InetSocketAddress dest;

		/*
		 * Here is the idea: - First we want to try the new (and IPv6 compatible) EPSV command But since we want to be nice with NAT software, we'll issue the
		 * EPSV ALL cmd first. EPSV is documented in RFC2428 - If EPSV fails, then we fall back to the older, yet OK PASV command - If PASV fails as well, then
		 * we throw an exception and the calling method will have to try the EPRT or PORT command
		 */
		if (issueCommand("EPSV ALL") == FtpClient.FTP_SUCCESS) 
		{
			// We can safely use EPSV commands
			if (issueCommand("EPSV") == FtpClient.FTP_ERROR) 
			{
				throw new FtpProtocolException("EPSV Failed: " + getResponseString()); 
			}
			serverAnswer = getResponseString();

			// The response string from a EPSV command will contain the port number
			// the format will be :
			// 229 Entering Extended Passive Mode (|||58210|)
			//
			// So we'll use the regular expresions package to parse the output.

			final Pattern p = Pattern.compile("^229 .* \\(\\|\\|\\|(\\d+)\\|\\)"); 
			final Matcher m = p.matcher(serverAnswer);
			if (!m.find())
			{
				throw new FtpProtocolException("EPSV failed : " + serverAnswer); 
			}
			// Yay! Let's extract the port number
			final String s = m.group(1);
			port = Integer.parseInt(s);
			final InetAddress add = this.serverSocket.getInetAddress();
			if (add != null)
			{
				dest = new InetSocketAddress(add, port);
			}
			else
			{
				// This means we used an Unresolved address to connect in
				// the first place. Most likely because the proxy is doing
				// the name resolution for us, so let's keep using unresolved
				// address.
				dest = InetSocketAddress.createUnresolved(this.serverName, port);
			}
		}
		else
		{
			// EPSV ALL failed, so Let's try the regular PASV cmd
			if (issueCommand("PASV") == FtpClient.FTP_ERROR) 
			{
				throw new FtpProtocolException("PASV failed: " + getResponseString()); 
			}
			serverAnswer = getResponseString();

			// Let's parse the response String to get the IP & port to connect to
			// the String should be in the following format :
			//
			// 227 Entering Passive Mode (A1,A2,A3,A4,p1,p2)
			//
			// Note that the two parenthesis are optional
			//
			// The IP address is A1.A2.A3.A4 and the port is p1 * 256 + p2
			//
			// The regular expression is a bit more complex this time, because the
			// parenthesis are optionals and we have to use 3 groups.

			final Pattern p = Pattern.compile("227 .* \\(?(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)?"); 
			final Matcher m = p.matcher(serverAnswer);
			if (!m.find())
			{
				throw new FtpProtocolException("PASV failed : " + serverAnswer); 
			}
			// Get port number out of group 2 & 3
			port = Integer.parseInt(m.group(3)) + (Integer.parseInt(m.group(2)) << 8);
			// IP address is simple
			final String s = m.group(1).replace(',', '.');
			dest = new InetSocketAddress(s, port);
		}
		// Got everything, let's open the socket!
		Socket s;
		if (this.proxy != null)
		{
			if (this.proxy.type() == Proxy.Type.SOCKS)
			{
				s = (Socket) AccessController.doPrivileged((PrivilegedAction) () -> new Socket(FtpClient.this.proxy));
			}
			else
			{
				s = new Socket(Proxy.NO_PROXY);
			}
		}
		else
		{
			s = new Socket();
		}
		if (this.connectTimeout >= 0)
		{
			s.connect(dest, this.connectTimeout);
		}
		else
		{
			if (NetworkClient.defaultConnectTimeout > 0)
			{
				s.connect(dest, NetworkClient.defaultConnectTimeout);
			}
			else
			{
				s.connect(dest);
			}
		}
		if (this.readTimeout >= 0)
		{
			s.setSoTimeout(this.readTimeout);
		}
		else if (NetworkClient.defaultSoTimeout > 0)
		{
			s.setSoTimeout(NetworkClient.defaultSoTimeout);
		}
		return s;
	}

	/**
	 * Tries to open a Data Connection with the server. It will first try a passive mode connection, then, if it fails, a more traditional PORT command
	 *
	 * @param cmd the command to execute (RETR, STOR, etc...)
	 * @return the opened socket
	 * @throws FtpProtocolException if an error occurs when issuing the PORT command to the ftp server.
	 */
	protected Socket openDataConnection(final String cmd) throws IOException
	{
		ServerSocket portSocket;
		Socket clientSocket;
		StringBuilder portCmd;
		InetAddress myAddress;
		IOException e;

		// Let's try passive mode first
		try
		{
			clientSocket = openPassiveDataConnection();
		}
		catch (final IOException ex)
		{
			clientSocket = null;
		}
		if (clientSocket != null)
		{
			// We did get a clientSocket, so the passive mode worked
			// Let's issue the command (GET, DIR, ...)
			try
			{
				if (issueCommand(cmd) == FtpClient.FTP_ERROR)
				{
					clientSocket.close();
					throw new FtpProtocolException(getResponseString());
				}
				else
				{
					return clientSocket;
				}
			}
			catch (final IOException ioe)
			{
				clientSocket.close();
				throw ioe;
			}
		}

		assert clientSocket == null;

		// Passive mode failed, let's fall back to the good old "PORT"

		if (this.proxy != null && this.proxy.type() == Proxy.Type.SOCKS)
		// We're behind a firewall and the passive mode fail,
		// since we can't accept a connection through SOCKS (yet)
		// throw an exception
		{
			throw new FtpProtocolException("Passive mode failed"); 
		}
		else
		{
			portSocket = new ServerSocket(0, 1);
		}
		try
		{
			myAddress = portSocket.getInetAddress();
			if (myAddress.isAnyLocalAddress())
			{
				myAddress = getLocalAddress();
			}
			// Let's try the new, IPv6 compatible EPRT command
			// See RFC2428 for specifics
			// Some FTP servers (like the one on Solaris) are bugged, they
			// will accept the EPRT command but then, the subsequent command
			// (e.g. RETR) will fail, so we have to check BOTH results (the
			// EPRT cmd then the actual command) to decide wether we should
			// fall back on the older PORT command.
			portCmd = new StringBuilder("EPRT |" + (myAddress instanceof Inet6Address ? "2" : "1") + "|" + myAddress.getHostAddress() + "|" + portSocket.getLocalPort() + "|");      
			if (issueCommand(portCmd.toString()) == FtpClient.FTP_ERROR || issueCommand(cmd) == FtpClient.FTP_ERROR)
			{
				// The EPRT command failed, let's fall back to good old PORT
				portCmd = new StringBuilder("PORT "); 
				final byte[] addr = myAddress.getAddress();

				/* append host addr */
				for (final byte element : addr)
				{
					portCmd.append(element & 0xFF).append(","); 
				}

				/* append port number */
				portCmd.append(portSocket.getLocalPort() >>> 8 & 0xff).append(",").append(portSocket.getLocalPort() & 0xff); 
				if (issueCommand(portCmd.toString()) == FtpClient.FTP_ERROR)
				{
					e = new FtpProtocolException("PORT :" + getResponseString()); 
					throw e;
				}
				if (issueCommand(cmd) == FtpClient.FTP_ERROR)
				{
					e = new FtpProtocolException(cmd + ":" + getResponseString()); 
					throw e;
				}
			}
			// Either the EPRT or the PORT command was successful
			// Let's create the client socket
			if (this.connectTimeout >= 0)
			{
				portSocket.setSoTimeout(this.connectTimeout);
			}
			else
			{
				if (NetworkClient.defaultConnectTimeout > 0)
				{
					portSocket.setSoTimeout(NetworkClient.defaultConnectTimeout);
				}
			}
			clientSocket = portSocket.accept();
			if (this.readTimeout >= 0)
			{
				clientSocket.setSoTimeout(this.readTimeout);
			}
			else
			{
				if (NetworkClient.defaultSoTimeout > 0)
				{
					clientSocket.setSoTimeout(NetworkClient.defaultSoTimeout);
				}
			}
		}
		finally
		{
			portSocket.close();
		}

		return clientSocket;
	}

	/* public methods */

	/**
	 * Open a FTP connection to host <i>host</i>.
	 *
	 * @param host The hostname of the ftp server
	 * @throws FtpProtocolException if connection fails
	 */
	public void openServer(final String host) throws IOException
	{
		openServer(host, FtpClient.FTP_PORT);
	}

	/**
	 * Open a FTP connection to host <i>host</i> on port <i>port</i>.
	 *
	 * @param host the hostname of the ftp server
	 * @param port the port to connect to (usually 21)
	 * @throws FtpProtocolException if connection fails
	 */
	@Override
	public void openServer(final String host, final int port) throws IOException
	{
		this.serverName = host;
		super.openServer(host, port);
		if (readReply() == FtpClient.FTP_ERROR)
		{
			throw new FtpProtocolException("Welcome message: " + getResponseString()); 
		}
	}

	/**
	 * login user to a host with username <i>user</i> and password <i>password</i>
	 *
	 * @param user     Username to use at login
	 * @param password Password to use at login or null of none is needed
	 * @throws FtpLoginException if login is unsuccesful
	 */
	public void login(final String user, final String password) throws IOException
	{
		if (!serverIsOpen())
		{
			throw new FtpLoginException("not connected to host"); 
		}
		if (user == null || user.length() == 0)
		{
			return;
		}
		if (issueCommand("USER " + user) == FtpClient.FTP_ERROR) 
		{
			throw new FtpLoginException("user " + user + " : " + getResponseString());  
		}
		/*
		 * Checks for "331 User name okay, need password." answer
		 */

		if (this.lastReplyCode == 331)
		{
			if (password == null || password.length() == 0 || issueCommand("PASS " + password) == FtpClient.FTP_ERROR) 
			{
				throw new FtpLoginException("password: " + getResponseString()); 
			}
		}

		// keep the welcome message around so we can
		// put it in the resulting HTML page.
		String l;
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.serverResponse.size(); i++)
		{
			l = this.serverResponse.elementAt(i);
			if (l != null)
			{
				if (l.length() >= 4 && l.startsWith("230")) 
				{
					// get rid of the "230-" prefix
					l = l.substring(4);
				}
				sb.append(l);
			}
		}
		this.welcomeMsg = sb.toString();
		this.loggedIn = true;
	}

	/**
	 * GET a file from the FTP server
	 *
	 * @param filename name of the file to retrieve
	 * @return the {@code InputStream} to read the file from
	 * @throws FileNotFoundException if the file can't be opened
	 */
	public TelnetInputStream get(final String filename) throws IOException
	{
		Socket s = null;

		try
		{
			s = openDataConnection("RETR " + filename); 
		}
		catch (final FileNotFoundException fileException)
		{
			/*
			 * Well, "/" might not be the file delimitor for this particular ftp server, so let's try a series of "cd" commands to get to the right place.
			 */
			/* But don't try this if there are no '/' in the path */
			if (filename.indexOf('/') == -1)
			{
				throw fileException;
			}

			final StringTokenizer t = new StringTokenizer(filename, "/"); 
			String pathElement = null;

			while (t.hasMoreElements())
			{
				pathElement = t.nextToken();

				if (!t.hasMoreElements())
				{
					/* This is the file component. Look it up now. */
					break;
				}
				try
				{
					cd(pathElement);
				}
				catch (final FtpProtocolException e)
				{
					/* Giving up. */
					throw fileException;
				}
			}
			if (pathElement != null)
			{
				s = openDataConnection("RETR " + pathElement); 
			}
			else
			{
				throw fileException;
			}
		}
		finally
		{
			if (s != null)
			{
				s.close();
			}
		}

		return new TelnetInputStream(s.getInputStream(), this.binaryMode);
	}

	/**
	 * PUT a file to the FTP server
	 *
	 * @param filename name of the file to store
	 * @return the {@code OutputStream} to write the file to
	 */
	public TelnetOutputStream put(final String filename) throws IOException
	{
		final Socket s = openDataConnection("STOR " + filename); 
		final TelnetOutputStream out = new TelnetOutputStream(s.getOutputStream(), this.binaryMode);
		if (!this.binaryMode)
		{
			out.setStickyCRLF(true);
		}
		return out;
	}

	/**
	 * Append to a file on the FTP server
	 *
	 * @param filename name of the file to append to
	 * @return the {@code OutputStream} to write the file to
	 */
	public TelnetOutputStream append(final String filename) throws IOException
	{
		final Socket s = openDataConnection("APPE " + filename); 
		final TelnetOutputStream out = new TelnetOutputStream(s.getOutputStream(), this.binaryMode);
		if (!this.binaryMode)
		{
			out.setStickyCRLF(true);
		}

		return out;
	}

	/**
	 * LIST files in the current directory on a remote FTP server
	 *
	 * @return the {@code InputStream} to read the list from
	 */
	public TelnetInputStream list() throws IOException
	{
		final Socket s = openDataConnection("LIST"); 

		return new TelnetInputStream(s.getInputStream(), this.binaryMode);
	}

	/**
	 * List (NLST) file names on a remote FTP server
	 *
	 * @param path pathname to the directory to list, null for current directory
	 * @return the {@code InputStream} to read the list from
	 * @throws IOException io exception
	 */
	public TelnetInputStream nameList(final String path) throws IOException
	{
		Socket s;

		if (path != null)
		{
			s = openDataConnection("NLST " + path); 
		}
		else
		{
			s = openDataConnection("NLST"); 
		}
		return new TelnetInputStream(s.getInputStream(), this.binaryMode);
	}

	/**
	 * CD to a specific directory on a remote FTP server
	 *
	 * @param remoteDirectory path of the directory to CD to
	 * @throws IOException io exception
	 */
	public void cd(final String remoteDirectory) throws IOException
	{
		if (remoteDirectory == null || "".equals(remoteDirectory)) 
		{
			return;
		}
		issueCommandCheck("CWD " + remoteDirectory); 
	}

	/**
	 * CD to the parent directory on a remote FTP server
	 *
	 * @throws IOException io exception
	 */
	public void cdUp() throws IOException
	{
		issueCommandCheck("CDUP"); 
	}

	/**
	 * Print working directory of remote FTP server
	 *
	 * @throws FtpProtocolException if the command fails
	 */
	public String pwd() throws IOException
	{
		String answ;

		issueCommandCheck("PWD"); 
		/*
		 * answer will be of the following format : 257 "/" is current directory.
		 */
		answ = getResponseString();
		if (!answ.startsWith("257")) 
		{
			throw new FtpProtocolException("PWD failed. " + answ); 
		}
		return answ.substring(5, answ.lastIndexOf('"'));
	}

	/**
	 * Set transfer type to 'I'
	 *
	 * @throws FtpProtocolException if the command fails
	 */
	public void binary() throws IOException
	{
		issueCommandCheck("TYPE I"); 
		this.binaryMode = true;
	}

	/**
	 * Set transfer type to 'A'
	 *
	 * @throws FtpProtocolException if the command fails
	 */
	public void ascii() throws IOException
	{
		issueCommandCheck("TYPE A"); 
		this.binaryMode = false;
	}

	/**
	 * Rename a file on the ftp server
	 *
	 * @throws FtpProtocolException if the command fails
	 */
	public void rename(final String from, final String to) throws IOException
	{
		issueCommandCheck("RNFR " + from); 
		issueCommandCheck("RNTO " + to); 
	}

	/**
	 * Get the "System string" from the FTP server
	 *
	 * @throws FtpProtocolException if it fails
	 */
	public String system() throws IOException
	{
		String answ;
		issueCommandCheck("SYST"); 
		answ = getResponseString();
		if (!answ.startsWith("215")) 
		{
			throw new FtpProtocolException("SYST failed." + answ); 
		}
		return answ.substring(4); // Skip "215 "
	}

	/**
	 * Send a No-operation command. It's usefull for testing the connection status
	 *
	 * @throws FtpProtocolException if the command fails
	 */
	public void noop() throws IOException
	{
		issueCommandCheck("NOOP"); 
	}

	/**
	 * Reinitialize the USER parameters on the FTp server
	 *
	 * @throws FtpProtocolException if the command fails
	 */
	public void reInit() throws IOException
	{
		issueCommandCheck("REIN"); 
		this.loggedIn = false;
	}

	/**
	 * New FTP client connected to host <i>host</i>.
	 *
	 * @param host Hostname of the FTP server
	 * @throws FtpProtocolException if the connection fails
	 */
	public FtpClient(final String host) throws IOException
	{
		super();
		openServer(host, FtpClient.FTP_PORT);
	}

	/**
	 * New FTP client connected to host <i>host</i>, port <i>port</i>.
	 *
	 * @param host Hostname of the FTP server
	 * @param port port number to connect to (usually 21)
	 * @throws FtpProtocolException if the connection fails
	 */
	public FtpClient(final String host, final int port) throws IOException
	{
		super();
		openServer(host, port);
	}

	/**
	 * Create an uninitialized FTP client.
	 */
	public FtpClient()
	{
	}

	public FtpClient(final Proxy p)
	{
		this.proxy = p;
	}

	@Override
	protected void finalize() throws IOException
	{
		// Do not call the "normal" closeServer() as we want finalization to be as efficient as possible
		if (serverIsOpen())
		{
			super.closeServer();
		}
	}

}
