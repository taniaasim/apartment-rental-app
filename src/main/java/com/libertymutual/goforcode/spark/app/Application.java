package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.controllers.ApartmentAPIController;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentController;
import com.libertymutual.goforcode.spark.app.controllers.HomeController;
import com.libertymutual.goforcode.spark.app.controllers.SessionController;
import com.libertymutual.goforcode.spark.app.controllers.UserAPIController;
import com.libertymutual.goforcode.spark.app.filters.SecurityFilters;
import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.ApartmentsUsers;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;

public class Application {

	public static void main(String[] args) {
		String encryptPassword = BCrypt.hashpw("password", BCrypt.gensalt());

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User.deleteAll();
			User tania = new User("a@a.com", encryptPassword, "Tania", "Asim");
			tania.saveIt();
			User bob = new User("bob@bob.com", encryptPassword, "Bob", "Bobby");
			bob.saveIt();
			
			Apartment.deleteAll();
			Apartment firstApartment = new Apartment(4500 , 1, 0.0, 350, "123 Main St", "San Francisco", "CA", "95125");
			tania.add(firstApartment);
			firstApartment.saveIt();
			Apartment secondApartment = new Apartment(1400, 5, 6.0, 4050, "123 Cowboy Way", "Houston", "TX", "77006");
			tania.add(secondApartment);
			secondApartment.saveIt();
			Apartment thirdApartment = new Apartment(56000, 23, 12.0, 7650, "123 Rodeo Blvd", "Beverly Hills", "CA", "90210");
			tania.add(thirdApartment);
			thirdApartment.saveIt();
			ApartmentsUsers.deleteAll(); // ALL THE LIKING STARTS FRESH
		} 
		
		path("/apartments", () -> {
			before("/new", SecurityFilters.isAuthenticated);
			get("/new", ApartmentController.newForm);
			
			before("/mine", SecurityFilters.isAuthenticated);
			get("/mine", ApartmentController.index);
			get("/:id", ApartmentController.details);
			
			before("/:id/like", SecurityFilters.isAuthenticated);
			post("/:id/like", ApartmentController.like);
			
			before("", SecurityFilters.isAuthenticated);
			post("", ApartmentController.create);
			
			before("/:id/deactivations", SecurityFilters.isAuthenticated);
			post("/:id/deactivations", ApartmentController.deactivate);
			
			before("/:id/activations", SecurityFilters.isAuthenticated);
			post("/:id/activations", ApartmentController.activate);
			
		});
		
		
		get("/", HomeController.index);
		get("/login", SessionController.newForm);
		post("/login", SessionController.create);
		post("/logout", SessionController.logout);
		get("/users/new", UserAPIController.newForm);
		post("/users", UserAPIController.create);

		path("/api", () ->  {
			get("/apartments/:id", ApartmentAPIController.details);
			post("/apartments", ApartmentAPIController.create);
		});
	}
}