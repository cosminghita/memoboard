package com.espresoh.memoboard.server.session;

import com.gncsoft.util.MakeMD5Password;

/**
 * 
 * @author Cosmin
 *
 */
public class ConnectionDetails {

	private static final String LOCALHOST = "127.0.0.1";
	
	private static final String OPENSHIFT = "531da1c84382ec09e70000f4-gncsoft.rhcloud.com";//vfrpontaj wildfly 8
	private static final String OPENSHIFT_PORT = "36456";//vfrpontaj wildfly 8
	

	private static final String SERVER_IP = LOCALHOST;
	private static final String SERVER_PORT = "5432";

//	private static final String SERVER_IP = OPENSHIFT;
//	private static final String SERVER_PORT = OPENSHIFT_PORT;

	// ====================== 2. Instance Fields =============================

	private String server;
	private String port;
	private String username;
	private String password;
	private String database = "vfrpontaj";

	// ==================== 4. Constructors ====================

	public ConnectionDetails() {
		this.port = SERVER_PORT;
		this.server = SERVER_IP;

	}
	public ConnectionDetails(final String server, final String username, final String password, final String port) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.port = port;
	}
	public ConnectionDetails(final String server, final String username, final String password) {
		this(server, username, password, SERVER_PORT);
	}

	// ==================== 7. Getters & Setters ====================

	public String getServer() {
		return server;
	}

	public String getServerWithoutPort() {
		if(server == null || server.isEmpty())
			return SERVER_IP;
		return server.split(":")[0];
	}

	public String getServerDefaultWithPort() {
		if(server == null || server.isEmpty())
			return SERVER_IP + ":" + port;
		else if(server.split(":").length == 1)
			return server + ":" + port;
		return server;
	}

	public void setServer(final String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(final String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public ConnectionDetails setUsername(final String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public ConnectionDetails setPassword(final String password) {
		this.password = password;
		return this;
	}

	public String getPasswordCrypted() {
		return MakeMD5Password.getInstance().makeMD5(password) ;
	}

	public String getDatabase() {
		return database;
	}

	public ConnectionDetails setDatabase(final String database) {
		this.database = database;
		return this;
	}
}
