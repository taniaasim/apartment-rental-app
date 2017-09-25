package com.libertymutual.goforcode.spark.app.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Model;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.ApartmentsUsers;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

	public static final Route details = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User currentUser = req.session().attribute("currentUser");
			long apartmentId = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(apartmentId);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("noUser", req.session().attribute("currentUser") == null);
			model.put("apartment", apartment);
			System.out.println(apartment.getZipCode());
			model.put("currentUser", currentUser);
			// if i already liked this apartment, set liked to true. only
			// show the like button if liked is false

			// if apartment I'm looking at is in the list of apartments I created
			// then I'm the owner and owner should be set to true
			List<ApartmentsUsers> likesThisApartmentHas = ApartmentsUsers.where("apartment_id = ?", apartmentId);
			int count = likesThisApartmentHas.size();
			model.put("count", count);
			if (currentUser != null) {
				long id = (long) currentUser.getId();
				List<Apartment> apartmentsIOwn = Apartment.where("user_id = ?", id);
				List<ApartmentsUsers> apartmentsILiked = ApartmentsUsers.where("user_id = ?", id);
				if (apartmentsILiked == null) {
					System.out.println("How does this freakin thing keep ending up null.");
				} else {
					for (ApartmentsUsers au : apartmentsILiked) {
						int apartmentidfrommethod = au.getApartmentId();
						if (apartmentidfrommethod == apartmentId) {
							model.put("liked", true);
						} else {
							model.put("liked", false);
						}
						System.out.println(apartmentidfrommethod);
					}
				}

				if (apartmentsIOwn.contains(apartment)) {
					model.put("owner", true);
					// if i'm the owner, and i'm logged in, then show a list of people who liked my
					// apartment
					List<ApartmentsUsers> usersLikedThisApartment = ApartmentsUsers.where("apartment_id = ?",
							apartmentId);
				//	List<Integer> users = new ArrayList<Integer>();
					List<User> usersNames = new ArrayList<User>();
					for (ApartmentsUsers au : usersLikedThisApartment) {
						int userId = au.getUserId();
						User user = User.findById(userId);
						usersNames.add(user);
					}
					model.put("usersWhoLikeThis", usersNames);
					System.out.println("I own this apartment");
				} else {
					model.put("owner", false);
					System.out.println("I DO NOT own this apartment");
				}

			}
			return MustacheRenderer.getInstance().render("apartments/details.html", model);
		}
	};

	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);
		return MustacheRenderer.getInstance().render("apartments/newForm.html", model);

	};
	public static final Route create = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Map<String, Object> model = new HashMap<String, Object>();
			User currentUser = req.session().attribute("currentUser");
			model.put("currentUser", req.session().attribute("currentUser"));  // THIS MAKES NAV WORK
			Apartment apartment = new Apartment(Integer.parseInt(req.queryParams("rent")),
					Integer.parseInt(req.queryParams("number_of_bedrooms")),
					Double.parseDouble(req.queryParams("number_of_bathrooms")),
					Integer.parseInt(req.queryParams("square_footage")), req.queryParams("address"),
					req.queryParams("city"), req.queryParams("state"), req.queryParams("zip_code"));
			currentUser.add(apartment);
			apartment.saveIt();
			res.redirect("/");
			return "";
		}
	};
	public static final Route index = (Request req, Response res) -> {
		User currentUser = req.session().attribute("currentUser");
		long id = (long) currentUser.getId();

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			List<Apartment> apartments = Apartment.where("user_id = ?", id);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("currentUser", req.session().attribute("currentUser"));  // THIS MAKES NAV WORK
			// loop through list, if the apartment isActive is true, put it in true list.
			// otherwise
			// put it in false list
			List<Apartment> activeApartments = new ArrayList<Apartment>();
			List<Apartment> inactiveApartments = new ArrayList<Apartment>();
			for (Apartment apartment : apartments) {
				if (apartment.getIsActive()) {
					activeApartments.add(apartment);
				} else {
					inactiveApartments.add(apartment);
				}
			}
			model.put("activeApartments", activeApartments);
			model.put("inactiveApartments", inactiveApartments);
			return MustacheRenderer.getInstance().render("apartments/index.html", model);
		}
	};

	// doesn't need nav stuff
	public static final Route like = (Request req, Response res) -> {
		String email = req.queryParams("email");
		String password = req.queryParams("password");
		User currentUser = req.session().attribute("currentUser");
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User user = User.findFirst("email = ?", email);
			Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
			long id = (long) apartment.getId();

			if (req.queryParams("returnPath") != null) {
				res.redirect(req.queryParamOrDefault("returnPath", "/"));
			}

			apartment.add(currentUser);
			res.redirect("/apartments/" + id);
			return "";
		}
	};

	// doesn't need nav stuff
	public static final Route deactivate = (Request req, Response res) -> {
		long id = 0;
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
			apartment.setIsActive(false);
			apartment.saveIt();
			id = (long) apartment.getId();
		}
		res.redirect("/apartments/" + id);
		return "";
	};

	// doesn't need nav stuff
	public static final Route activate = (Request req, Response res) -> {
		long id = 0;
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
			apartment.setIsActive(true);
			apartment.saveIt();
			id = (long) apartment.getId();
		}
		res.redirect("/apartments/" + id);
		return "";
	};

}
