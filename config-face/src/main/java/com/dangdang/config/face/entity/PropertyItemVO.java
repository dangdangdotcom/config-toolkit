/**
 * Copyright 1999-2014 dangdang.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dangdang.config.face.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class PropertyItemVO implements Serializable, Comparable<PropertyItemVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String oriName;
	private String name;
	private String value;
	private String comment;

	public PropertyItemVO() {
		super();
	}

	public PropertyItemVO(PropertyItem propertyItem) {
		super();
		this.name = propertyItem.getName();
		this.oriName = propertyItem.getName();
		this.value = propertyItem.getValue();
	}

	public PropertyItemVO(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOriName() {
		return oriName;
	}

	public void setOriName(String oriName) {
		this.oriName = oriName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "PropertyItemVO [oriName=" + oriName + ", name=" + name + ", value=" + value + ", comment=" + comment + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PropertyItemVO o) {
		return this.name.compareTo(o.getName());
	}

}
