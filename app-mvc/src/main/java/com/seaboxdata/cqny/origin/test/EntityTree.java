package com.seaboxdata.cqny.origin.test;

import java.util.List;

/**
 * 生成树数据的接收类
 *
 */
public class EntityTree implements ITree<EntityTree> {

	private String id;
	
	private String parentId;
	
	private String name;
	
	private List<EntityTree> childList;

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<EntityTree> getChildList() {
		return childList;
	}

	@Override
	public void setChildList(List<EntityTree> childList) {
		this.childList = childList;
	}
}
