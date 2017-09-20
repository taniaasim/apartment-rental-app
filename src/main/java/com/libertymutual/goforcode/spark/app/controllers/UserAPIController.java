package com.libertymutual.goforcode.spark.app.controllers;

import java.util.AbstractMap;
import java.util.stream.Collectors;

import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.*;

public class UserAPIController {

	public static final Route newForm = (Request req, Response res) -> {
		return MustacheRenderer.getInstance().render("users/newForm.html", null);
	};
	
	public static final Route create = (Request req, Response res) -> {
		User user = new User(
				req.queryParams("email"),
				req.queryParams("password"),
				req.queryParams("firstName"),
				req.queryParams("lastName")
				);
		try (AutoCloseable db = new AutoCloseableDb()) {
			user.saveIt();
			req.session().attribute("currentUser", user);
			res.redirect("/");
			return "";
		}
	};
}
		/*
		req.queryMap("user") 
			.toMap()
			.entrySet()
			.stream()
			.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()[0]))
			.collect(Collectors.toMap(null, null));
		User user = new User();
	};*/
	

