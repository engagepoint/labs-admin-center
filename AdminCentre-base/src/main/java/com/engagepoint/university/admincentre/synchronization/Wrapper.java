package com.engagepoint.university.admincentre.synchronization;

import java.io.Serializable;
import java.util.Map;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.Node;

final class Wrapper implements Serializable{
	private static final long serialVersionUID = -2146939268672054204L;
	private final Map<String, Node> cacheNodeData;
	private final Map<String, Key> cacheKeyData;
	
	Wrapper(Map<String, Node> cacheNodeData, Map<String, Key> cacheKeyData) {
		this.cacheNodeData = cacheNodeData;
		this.cacheKeyData = cacheKeyData;
	}
	
	public Map<String, Key> getCacheKeyData() {
		return cacheKeyData;
	}
	
	public Map<String, Node> getCacheNodeData() {
		return cacheNodeData;
	}
}