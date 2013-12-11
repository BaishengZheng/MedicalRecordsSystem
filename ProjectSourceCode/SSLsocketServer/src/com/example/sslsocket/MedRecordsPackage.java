package com.example.sslsocket;

import java.io.Serializable;
import java.util.ArrayList;

public class MedRecordsPackage implements Serializable {
	
	ArrayList<MedRecord> arrayList = new ArrayList<MedRecord>();
	
	public void addMedRecord(MedRecord mr) {
		arrayList.add(mr);
	}
	
	public ArrayList<MedRecord> getAllMedRecords() {
		return arrayList;
	}
}
