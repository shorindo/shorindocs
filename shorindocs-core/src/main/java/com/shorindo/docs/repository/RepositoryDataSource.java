package com.shorindo.docs.repository;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ApplicationContext;

public class RepositoryDataSource implements DataSource {
	private DataSource ds;

	public RepositoryDataSource() {
		try {
			Properties props = new Properties();
			for (Entry<Object,Object> e : ApplicationContext.getProperties().entrySet()) {
				String key = (String)e.getKey();
				String val = (String)e.getValue();
				if (key.startsWith("datasource.") && val != null) {
					props.setProperty(key.substring(11), val);
				}
			}
			ds = BasicDataSourceFactory.createDataSource(props);
		} catch (Exception e) {
			
		}
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return ds.getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return ds.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return ds.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return ds.getConnection(username, password);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return ds.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		ds.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		ds.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return ds.getLoginTimeout();
	}

}
