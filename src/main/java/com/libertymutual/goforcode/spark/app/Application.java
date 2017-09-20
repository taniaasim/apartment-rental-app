package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.controllers.ApartmentAPIController;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentController;
import com.libertymutual.goforcode.spark.app.controllers.HomeController;
import com.libertymutual.goforcode.spark.app.controllers.SessionController;
import com.libertymutual.goforcode.spark.app.filters.SecurityFilters;
import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;

import spark.*;

public class Application {

	public static void main(String[] args) {
		String encryptPassword = BCrypt.hashpw("password", BCrypt.gensalt());

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User.deleteAll();
			new User("a@a.com", encryptPassword, "Tania", "Asim").saveIt();
			Apartment.deleteAll();
			new Apartment(4500 , 1, 0.0, 350, "123 Main St", "San Francisco", "CA", "95125").saveIt();
			new Apartment(1400, 5, 6.0, 4050, "123 Cowboy Way", "Houston", "TX", "77006").saveIt();
			new Apartment(56000, 23, 12.0, 7650, "123 Rodeo Blvd", "Beverly Hills", "CA", "90210").saveIt();
		} 
		
		path("/apartments", () -> {
			before("/new", SecurityFilters.isAuthenticated);
			get("/new", ApartmentController.newForm);
			get("/:id", ApartmentController.details);
			
			before("", SecurityFilters.isAuthenticated);
			post("", ApartmentController.create);
		});
		
		get("/", HomeController.index);
		get("/login", SessionController.newForm);
		post("/login", SessionController.create);

		path("/api", () ->  {
			get("/apartments/:id", ApartmentAPIController.details);
			post("/apartments", ApartmentAPIController.create);
		});
	}
}