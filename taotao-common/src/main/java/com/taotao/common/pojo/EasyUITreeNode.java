package com.taotao.common.pojo;

import java.io.Serializable;

public class EasyUITreeNode implements Serializable{
	//节点树的id
	private long id;
	private String text;
	//状态state：如果节点下有子节点“closed”，如果没有子节点“open”
	private String state;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}	
}