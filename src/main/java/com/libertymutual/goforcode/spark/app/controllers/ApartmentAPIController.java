package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;

import spark.*;
import static spark.Spark.*;

import java.util.Map;

public class ApartmentAPIController {

	public static final Route details = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
			if (apartment != null) {
				res.header("Content-Type", "application/json");
				return apartment.toJson(true);
			}
			notFound("Did not find that.");
			return "";
		}

	};
	public static Route create = (Request req, Response res) -> {

		String json = req.body();
		Map map = JsonHelper.toMap(json);

		Apartment apartment = new Apartment();
		apartment.fromMap(map);

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			apartment.saveIt();
			res.status(201);
			return apartment.toJson(true);
		}

	};

}
