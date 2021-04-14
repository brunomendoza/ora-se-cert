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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {
	private Map<Product, List<Review>> products = new HashMap<>();
//	private ResourceFormatter formatter;
	
	private final ResourceBundle config = ResourceBundle.getBundle("ora.demo.data.config");
	private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
	private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
	
	private final Path reportsFolder = Path.of(config.getString("reports.folder"));
	private final Path dataFolder = Path.of(config.getString("data.folder"));
	private final Path tempFolder = Path.of(config.getString("temp.folder"));
	
	private static final Map<String, ResourceFormatter> formatters = Map.of(
		"es-ES", new ResourceFormatter(new Locale("es", "ES")),
		"en-US", new ResourceFormatter(Locale.US),
		"en-GB", new ResourceFormatter(Locale.UK),
		"fr-FR", new ResourceFormatter(Locale.FRANCE),
		"zh-CH", new ResourceFormatter(Locale.CHINA)
	);
	
	private static final Logger logger = Logger.getLogger(ProductManager.class.getName());
	private static final ProductManager pm = new ProductManager();
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock writeLock = lock.writeLock();
	private final Lock readLock = lock.readLock();
	
	public static ProductManager getInstance() {
		return pm;
	}
	
	private ProductManager() {
		loadAllData();
	}

	public static Set<String> getSupportedLocales() {
		return formatters.keySet();
	}
	
	public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
		Product product = null; 
		
		try {
			readLock.lock();;
			product = new Food(id, name, price, rating, bestBefore);
			products.putIfAbsent(product, new ArrayList<Review>());
		} catch (Exception e) {
			logger.log(Level.INFO, "Error adding product " + e.getMessage());
			return null;
		} finally {
			readLock.unlock();
		}
		
		return product;
	}
	
	public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
		Product product = null;
		
		try {
			readLock.lock();
			product = new Drink(id, name, price, rating);
			products.putIfAbsent(product, new ArrayList<Review>());
		} catch (Exception e) {
			logger.log(Level.INFO, "Error adding product " + e.getMessage());
			return null;
		} finally {
			readLock.unlock();
		}
		
		return product;
	}
	
	private Review parseReview(String text) {
		Review review = null;
		
		try {
			Object[] values =  reviewFormat.parse(text);
			
			review = new Review(
					Rateable.convert(Integer.parseInt((String) values[0])),
					(String)values[1]);
		} catch (ParseException | NumberFormatException e) {
			logger.log(Level.WARNING, "Error parsing review " + text);
		}
		
		return review;
	}
	
	private Product parseProduct(String text) {
		Product product = null;
		
		try {
			Object[] values = productFormat.parse(text);
			int id = Integer.parseInt((String)values[1]);
			String name = (String)values[2];
			BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String)values[3]));
			Rating rating = Rateable.convert(Integer.parseInt((String)values[4]));
			
			switch ((String)values[0]) {
			case "D":
				product = new Drink(id, name, price, rating);
				break;
				
			case "F":
				LocalDate bestBefore = LocalDate.parse((String) values[5]);
				product = new Food(id, name, price, rating, bestBefore);
				break;
			}
			
		} catch (ParseException | NumberFormatException | DateTimeParseException e) {
			logger.log(Level.WARNING, "Error parsing product " + text + " " + e.getMessage()); 
		}
		
		return product;
	}
	
	private List<Review> loadReviews(Product product) {
		List<Review> reviews = null;
		Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
		
		if (Files.notExists(file)) {
			reviews = new ArrayList<>();
		} else {
			try {
				reviews = Files.lines(file, Charset.forName("UTF-8"))
					.map(text -> parseReview(text))
					.filter(review -> review != null)
					.collect(Collectors.toList());
			} catch (IOException e) {
				logger.log(Level.WARNING, "Error loading reviews " + e.getMessage());
			}
		}
		
		return reviews;
	}
	
	private Product loadProduct(Path file) {
		Product product = null;
		
		try {
			product = parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8")).findFirst().orElseThrow());
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error loading message " + e.getMessage());
		}
		
		return product;
	}
	
	private void loadAllData () {
		try {
			products = Files.list(dataFolder)
					.filter(file -> file.getFileName().toString().startsWith("product"))
					.map(file -> loadProduct(file))
					.filter(product -> product != null)
					.collect(Collectors.toMap(product -> product, product -> loadReviews(product)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error loading data " + e.getMessage());
		}
	}
	
	private void dumpData() {
		Path tempFile;	
		
		try {
			if (Files.notExists(tempFolder)) {
				Files.createDirectory(tempFolder);
			}
			
			String filename = String.valueOf(MessageFormat.format(config.getString("temp.file"), Instant.now().getEpochSecond()));
			
			tempFile = tempFolder.resolve(filename);	
			
			try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
				out.writeObject(products);
//				products = new HashMap<>();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error dumping  data " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void restoreData() {
		try {
			Path tempFile = Files.list(tempFolder)
					.filter(path -> path.getFileName().toString().endsWith("tmp"))
					.findFirst()
					.orElseThrow();
			
			try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
				products = (HashMap)in.readObject();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error restoring data " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @return A map storing rating number of stars and discount per rating.
	 */
	public Map<String, String> getDiscounts(String languageTag) {
		try {
			readLock.lock();
			
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
			
			return products.keySet()
					.stream()
					.collect(
							Collectors.groupingBy(
									p -> p.getRating().getStars(),
									Collectors.collectingAndThen(
											Collectors.summingDouble(p -> p.getDiscount().doubleValue()),
											d -> formatter.moneyFormat.format(d))));
		} finally {
			readLock.unlock();
		}
	}
	
	public Product findProduct(int productId) throws ProductManagerException {
		try {
			readLock.lock();
			return products.keySet()
					.stream()
					.filter(p -> p.getId() == productId)
					.findFirst()
					.orElseThrow(() -> new ProductManagerException("Product with id " + productId + " not found"));
		} finally {
			readLock.unlock();
		}
	}
	
	public Product reviewProduct(int productId, Rating rating, String comments) {
		try {
			writeLock.lock();
			return reviewProduct(findProduct(productId), rating, comments);
		} catch (ProductManagerException e) {
			logger.log(Level.INFO, e.getMessage());
			return null;
		} finally {
			writeLock.unlock();
		}
	}
	
	private Product reviewProduct(Product product, Rating rating, String comments) {
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
	
	public void printProductReport(int productId, String languageTag, String client) {
		try {
			readLock.lock();
			printProductReport(findProduct(productId), languageTag, client);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error printing product report " + e.getMessage(), e);
		} catch (ProductManagerException e) {
			logger.log(Level.INFO, e.getMessage());
		} finally {
			readLock.unlock();
		}
	}
	
	private void printProductReport(Product product, String languageTag, String client) throws IOException {
		ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
		
		List<Review> reviews = products.get(product);
		Collections.sort(reviews);
		
		Path productFile = reportsFolder
				.resolve(MessageFormat.format(config.getString("report.file"), product.getId(), client));
		
		try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE), "UTF-8"))) {
			out.append(formatter.formatProduct(product));
			out.append(System.lineSeparator());
			
			if (reviews.isEmpty()) {
				out.append(formatter.getText("no.reviews") + System.lineSeparator());
			} else {
				out.append(reviews.stream()
						.map(r -> formatter.formatReview(r) + System.lineSeparator())
						.collect(Collectors.joining()));
				
				// It could be 'reviews.stream().forEach(r -> txt.append(formatter.formatReview(r) + '\n'));'
				// but this statement has some parallel processing implications because of the order in which
				// StringBuilder and forEach method work.
			}
		}
	}
	
	public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {
		try {
			readLock.lock();
			
			ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
			StringBuilder txt = new StringBuilder();
			
			products.keySet()
			.stream()
			.sorted(sorter)
			.filter(filter)
			.forEach(p -> txt.append(formatter.formatProduct(p) + '\n'));
			
			System.out.println(txt);
		} finally {
			readLock.unlock();
		}
	}
	
	private static class ResourceFormatter {
		private ResourceBundle resources;
		private DateTimeFormatter dateFormat;
		private NumberFormat moneyFormat;
		
		private ResourceFormatter(Locale locale) {
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
