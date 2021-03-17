 /*
  * Copyright (C) 2021  bruno
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
  */

package ora.demo.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductManager {
	
//	private Product product;
//	private Review[] reviews = new Review[5];
	private Map<Product, List<Review>> products = new HashMap<>();
	
	private Locale locale;
	private ResourceBundle resources;
	private DateTimeFormatter dateFormat;
	private NumberFormat moneyFormat;
	
	/**
	 * @param locale
	 */
	public ProductManager(Locale locale) {
		super();
		this.locale = locale;
		
		resources = ResourceBundle.getBundle("ora.demo.data.resources");
		dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
		moneyFormat = NumberFormat.getCurrencyInstance(locale);
	}

	public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
		Product product = new Food(id, name, price, rating, bestBefore);
		products.putIfAbsent(product, new ArrayList<Review>());
		return product;
	}
	
	public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
		Product product = new Drink(id, name, price, rating);
		products.putIfAbsent(product, new ArrayList<Review>());
		return product;
	}
	
	public Product reviewProduct(Product product, Rating rating, String comments) {
		List<Review> reviews = products.get(product);
		int sum = 0;
		
		// It's not possible to update the product in a HashMap.
		products.remove(product, reviews);
		reviews.add(new Review(rating, comments));
		
		for (Review review : reviews) {
			sum += review.getRating().ordinal();
		}
		
		product = product.applyRating(Rateable.convert(Math.round((float)sum / reviews.size())));
		// Add the updated product.
		products.put(product, reviews);
		return product;
	}
	
	public void printProductReport(Product product) {
		List<Review> reviews = products.get(product);
		StringBuilder txt = new StringBuilder();
		
		txt.append(MessageFormat.format(resources.getString("product"),
				product.getName(),
				moneyFormat.format(product.getPrice()),
				product.getRating().getStars(),
				dateFormat.format(product.getBestBefore())));
		txt.append("\n");
		
		for (Review review : reviews) {
			txt.append(MessageFormat.format(resources.getString("review"),
					review.getRating().getStars(),
					review.getComments()));
			
			txt.append("\n");
		}
		
		if (reviews.isEmpty()) {
			txt.append(resources.getString("no.reviews"));
			txt.append("\n");
		}
		
		System.out.println(txt);
	}
}
