package com.libertymutual.goforcode.spark.app.models;

import org.javalite.activejdbc.Model;

public class ApartmentsUsers extends Model {

	@Override
	public boolean equals(Object o) {
		if (o instanceof Apartment) {
			Apartment apartment = (Apartment) o; 
			if (apartment.getId().equals(this.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public int getApartmentId() {
		return getInteger("apartment_id");
	}
	
	public int getUserId() {
		return getInteger("user_id");
	}
	

}
