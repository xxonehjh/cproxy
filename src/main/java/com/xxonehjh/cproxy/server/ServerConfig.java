package com.xxonehjh.cproxy.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.xxonehjh.cproxy.Constants;

public class ServerConfig {

	private Properties prop;

	public ServerConfig(String path) {
		prop = new Properties();
		try (InputStream in = new File(path).isFile() ? new FileInputStream(path) : this.getClass().getClassLoader().getResourceAsStream(path)) {
			prop.load(in);
		} catch (IOException e) {
			throw new RuntimeException("加载配置文件" + Constants.SERVER_CONFIG + "失败", e);
		}
	}

	public boolean isDebug() {
		return "true".equals(getString("debug"));
	}

	public String getServerToken() {
		return getString("server.token");
	}

	public int getServerInnerPort() {
		return getInt("server.inner.port");
	}

	public int[] getServerOuterPorts() {
		String[] ports = getString("server.outer.ports").split(",");
		int[] result = new int[ports.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(ports[i]);
		}
		return result;
	}

	public int getServerOuterNettyBossThreads() {
		return getInt("server.outer.netty.boss.threads");
	}

	public int getServerOuterNettyWorkThreads() {
		return getInt("server.outer.netty.worker.threads");
	}

	public int getServerInnerNettyBossThreads() {
		return getInt("server.inner.netty.boss.threads");
	}

	public int getServerInnerNettyWorkThreads() {
		return getInt("server.inner.netty.worker.threads");
	}

	private String getString(String key) {
		return prop.getProperty(key);
	}

	private int getInt(String key) {
		return Integer.parseInt(prop.getProperty(key));
	}

}
