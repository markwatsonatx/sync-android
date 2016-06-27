/**
 * Copyright (C) 2013 Cloudant
 *
 * Copyright (C) 2011 Ahmed Yehia (ahmed.yehia.m@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudant.mazha;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Object representation of changes feed, example:
 *
 * {
 *   "total_rows":2,
 *   "offset":0,
 *   "rows":[
 *      { "id": "1bc098fd33134731bd774dbf41b8e797", "key":"1bc098fd33134731bd774dbf41b8e797", "value":{"rev":"1-6a640f502a9140aea3d49e398fc886a2"}},
 *      { "id": "3cc7a998b29b48b493b128163f96c3fe", "key":"3cc7a998b29b48b493b128163f96c3fe", "value":{"rev":"1-a55a9e5737bb41ab8bbe087f43f5d8bc"}}
 *   ]
 * }
 *
 * @api_private
*/
public class AllDocsResult {
	private List<Row> rows;

	@JsonProperty("total_rows")
	private Integer totalRows;

	@JsonProperty("offset")
	private Integer offset;

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}


    /**
     * this.getResults().size()
     */
    public int size() {
        return this.getRows() == null ? 0 : this.getRows().size();
    }

	/**
	 * Represent a row in Changes result.
	 */
	public static class Row {
		private String id;
		private String key;
		private Map value;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Map getValue() {
			return value;
		}

		public void setValue(Map value) {
			this.value = value;
		}

		public String getRev() {
			if (this.value.containsKey("rev")) {
				return (String)this.value.get("rev");
			}
			else {
				return null;
			}
		}

		public Map getDoc() {
			if (this.value.containsKey("doc")) {
				return (Map)this.value.get("doc");
			}
			else {
				return null;
			}
		}
	} // end class Row
} // end class AllDocsResult
