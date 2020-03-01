package com.xxonehjh.cproxy.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.xxonehjh.cproxy.Constants;

public class ClientConfig {

	private Properties prop;

	public ClientConfig(String path) {
		prop = new Properties();
		try (InputStream in = new File(path).isFile() ? new FileInputStream(path) : this.getClass().getClassLoader().getResourceAsStream(path)) {
			prop.load(in);
		} catch (IOException e) {
			throw new RuntimeException("加载配置文件" + Constants.CLINET_CONFIG + "失败", e);
		}
	}

	public boolean isDebug() {
		return "true".equals(getString("debug"));
	}

	public String getServerToken() {
		return getString("server.token");
	}

	public String getServerInnerHost() {
		return getString("server.inner.host");
	}

	public int getServerInnerPort() {
		return getInt("server.inner.port");
	}

	public int getServerOuterPort() {
		return getInt("server.outer.port");
	}

	public String getTargetHost() {
		return getString("target.host");
	}

	public int getTargetPort() {
		return getInt("target.port");
	}

	public int getWorkThreads() {
		return getInt("netty.worker.threads");
	}

	public int getConnectCount() {
		return getInt("netty.connect.count");
	}

	private String getString(String key) {
		return prop.getProperty(key);
	}

	private int getInt(String key) {
		return Integer.parseInt(prop.getProperty(key));
	}

}
