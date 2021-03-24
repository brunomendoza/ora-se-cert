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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductManager {
	private Map<Product, List<Review>> products = new HashMap<>();
	private ResourceFormatter formatter;
	private static Map<String, ResourceFormatter> formatters = Map.of(
		"es-ES", new ResourceFormatter(new Locale("es", "ES")),
		"en-US", new ResourceFormatter(Locale.US),
		"en-GB", new ResourceFormatter(Locale.UK),
		"fr-FR", new ResourceFormatter(Locale.FRANCE),
		"zh-CH", new ResourceFormatter(Locale.CHINA)
	);
	
	/**
	 * @param locale
	 */
	public ProductManager(Locale locale) {
		this(locale.toLanguageTag());
	}
	
	public ProductManager(String languageTag) {
		changeLocale(languageTag);
	}
	
	public void changeLocale(String languageTag) {
		formatter = formatters.getOrDefault(languageTag, formatters.get("es-ES"));
	}

	public static Set<String> getSupportedLocales() {
		return formatters.keySet();
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
	
	public Product findProduct(int productId) {
		return products.keySet()
				.stream()
				.filter(p -> p.getId() == productId)
				.findFirst()
				.orElseGet(() -> null);
	}
	
	public Product reviewProduct(int productId, Rating rating, String comments) {
		return reviewProduct(findProduct(productId), rating, comments);
	}
	
	public Product reviewProduct(Product product, Rating rating, String comments) {
		List<Review> reviews = products.get(product);
		
		// It's not possible to update the product in a HashMap.
		products.remove(product, reviews);
		reviews.add(new Review(rating, comments));
		
		product = product.applyRating(
				Rateable.convert(
						(int)Math.round(
								reviews.stream()
								.mapToInt(r -> r.getRating().ordinal())
								.average()
								.orElse(0))));
		
		// Add the updated product.
		products.put(product, reviews);
		return product;
	}
	
	public void printProductReport(int productId) {
		printProductReport(findProduct(productId));
	}
	
	public void printProductReport(Product product) {
		List<Review> reviews = products.get(product);
		Collections.sort(reviews);
		StringBuilder txt = new StringBuilder();
		
		txt.append(formatter.formatProduct(product));
		txt.append("\n");
		
		if (reviews.isEmpty()) {
			txt.append(formatter.getText("no.reviews" + '\n'));
		} else {
			txt.append(reviews.stream()
					.map(r -> formatter.formatReview(r) + '\n')
					.collect(Collectors.joining()));
		}
		
		System.out.println(txt);
	}
	
	public void printProducts(Comparator<Product> sorter) {
		List<Product> productList = new ArrayList<Product>(products.keySet());
		productList.sort(sorter);
		StringBuilder txt = new StringBuilder();
		
		for (Product product : productList) {
			txt.append(formatter.formatProduct(product));
			txt.append('\n');
		}
		
		System.out.println(txt);
	}
	
	private static class ResourceFormatter {
//		private Locale locale;
		private ResourceBundle resources;
		private DateTimeFormatter dateFormat;
		private NumberFormat moneyFormat;
		
		private ResourceFormatter(Locale locale) {
//			this.locale = locale;
			resources = ResourceBundle.getBundle("ora.demo.data.resources", locale);
			dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
			moneyFormat = NumberFormat.getCurrencyInstance(locale);
		}
		
		private String formatProduct(Product product) {
			return MessageFormat.format(resources.getString("product"),
					product.getName(),
					moneyFormat.format(product.getPrice()),
					product.getRating().getStars(),
					dateFormat.format(product.getBestBefore()));
		}
		
		private String formatReview(Review review) {
			return MessageFormat.format(resources.getString("review"),
					review.getRating().getStars(),
					review.getComments());
		}
		
		private String getText(String key) {
			return resources.getString(key);
		}
	}
}
