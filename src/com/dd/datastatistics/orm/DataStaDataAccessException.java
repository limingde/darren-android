/**
 * 
 */
package com.dd.datastatistics.orm;

/**
 * Sqlite访问异常类，主要用于ORM框架
 * 
 * 
 */
public class DataStaDataAccessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2846484017141855467L;

	/**
	 * 
	 */
	public DataStaDataAccessException() {
	}

	/**
	 * @param detailMessage
	 *            数据库访问异常的详细信息
	 */
	public DataStaDataAccessException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 *            异常
	 */
	public DataStaDataAccessException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param detailMessage
	 *            异常描述信息
	 * @param throwable
	 *            异常
	 */
	public DataStaDataAccessException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
