/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.portal.kernel.json.JSON;

import java.io.Serializable;

/**
 * @author Carlos Lancha
 */
public class ButtonItem implements Serializable {
  	public String getAriaLabel() {
      return _ariaLabel;
    }

		public boolean getBlock() {
      return _block;
    }

		public Object getData() {
      return _data;
    }

		public boolean getDisabled() {
      return _disabled;
    }

    @JSON (name="elementClasses")
		public String getElementCssClasses() {
      return _elementCssClasses;
    }

		public String getIcon() {
      return _icon;
    }

		public String getIconAlignment() {
      return _iconAlignment;
    }

		public String getId() {
      return _id;
    }

		public String getLabel() {
      return _label;
    }

		public boolean getMonospaced() {
      return _monospaced;
    }

		public String getName() {
      return _name;
    }

		public String getSize() {
      return _size;
    }

		public String getStyle() {
      return _style;
    }

		public String getType() {
      return _type;
    }

		public String getValue() {
      return _value;
    }

  	public void setAriaLabel(String ariaLabel) {
      _ariaLabel = ariaLabel;
    }

		public void setBlock(boolean block) {
      _block = block;
    }

		public void setData(Object data) {
      _data = data;
    }

		public void setDisabled(boolean disabled) {
      _disabled = disabled;
    }

		public void setElementCssClasses(String elementCssClasses) {
      _elementCssClasses = elementCssClasses;
    }

		public void setIcon(String icon) {
      _icon = icon;
    }

		public void setIconAlignment(String iconAlignment) {
      _iconAlignment = iconAlignment;
    }

		public void setId(String id) {
      _id = id;
    }

		public void setLabel(String label) {
      _label = label;
    }

		public void setMonospaced(boolean monospaced) {
      _monospaced = monospaced;
    }

		public void setName(String name) {
      _name = name;
    }

		public void setSize(String size) {
      _size = size;
    }

		public void setStyle(String style) {
      _style = style;
    }

		public void setType(String type) {
      _type = type;
    }

		public void setValue(String value) {
      _value = value;
    }

    private String _ariaLabel;
    private boolean _block;
    private Object _data;
    private boolean _disabled;
    private String _elementCssClasses;
    private String _icon;
    private String _iconAlignment;
    private String _id;
    private String _label;
    private boolean _monospaced;
    private String _name;
    private String _size;
    private String _style;
    private String _type;
    private String _value;

}