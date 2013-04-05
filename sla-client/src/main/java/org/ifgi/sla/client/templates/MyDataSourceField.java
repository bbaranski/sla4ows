package org.ifgi.sla.client.templates;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;

public class MyDataSourceField extends DataSourceField {
	
	private String dataSourceName=null;
	
	public MyDataSourceField(String name,FieldType fieldType,  String dsName){
	
		super(name, fieldType);
		dataSourceName=dsName;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

}
