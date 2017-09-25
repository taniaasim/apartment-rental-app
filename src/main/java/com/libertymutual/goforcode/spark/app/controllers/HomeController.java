package com.libertymutual.goforcode.spark.app.controllers;

import java.util.*;

import com.libertymutual.goforcode.spark.app.VelocityTemplateEngine;
import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.*;

public class HomeController {

	public static final Route index = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
		List<Apartment> apartments = Apartment.findAll();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("apartments", apartments);
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);
		return MustacheRenderer.getInstance().render("home/index.html", model);
		}
	};
	
	public static String render(Map<String, Object> model, String templatePath) {
	    return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
	}
	
}
