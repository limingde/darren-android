package com.dd.datastatistics.bean;

import java.io.Serializable;
import java.util.List;

public class UploadDataList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String client_id;
	public List<UploadDataItem> logs;

}
