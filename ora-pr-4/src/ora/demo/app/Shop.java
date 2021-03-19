 /*
  * Copyright (C) 2021  Bruno Mendoza
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

package ora.demo.app;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;

import ora.demo.data.Product;
import ora.demo.data.ProductManager;
import ora.demo.data.Rating;

/**
 * {@code Shop} class represents an application that manages Products.
 * @version 1.0
 * @author Bruno Mendoza
 */
public class Shop {
	
	public static void main(String[] args) {
		ProductManager pm = new ProductManager(new Locale("es", "ES"));
		
		pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//		pm.printProductReport(101);
		
		pm.reviewProduct(101, Rating.FOUR_STAR, "Nice hot cup of tea");
		pm.reviewProduct(101, Rating.TWO_STAR, "Rather weak tea");
		pm.reviewProduct(101, Rating.FOUR_STAR, "Fine tea");
		pm.reviewProduct(101, Rating.FOUR_STAR, "Good tea");
		pm.reviewProduct(101, Rating.FIVE_STAR, "Perfect tea");
		pm.reviewProduct(101, Rating.THREE_STAR, "Just add some lemon");
		
//		pm.printProductReport(101);
		
		pm.changeLocale("en-US");
		
		pm.createProduct(102, "Coffe", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
		pm.reviewProduct(102, Rating.THREE_STAR, "Coffee was ok");
		pm.reviewProduct(102, Rating.ONE_STAR, "Where is the milk?!");
		pm.reviewProduct(102, Rating.FIVE_STAR, "It's perfect with ten spoons of sugar");
		
//		pm.printProductReport(102);
		
		pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99), Rating.NOT_RATED);
		pm.reviewProduct(103, Rating.THREE_STAR, "Very nice cake");
		pm.reviewProduct(103, Rating.FOUR_STAR, "It's good, but I've expected more chocolate");
		pm.reviewProduct(103, Rating.FIVE_STAR, "This cake is perfect");
		
//		pm.printProductReport(103);
		
//		Anonymous class
//		pm.printProducts(new Comparator<Product>() {
//			@Override
//			public int compare(Product p1, Product p2) {
//				return p2.getRating().ordinal() - p1.getRating().ordinal();
//			}
//		});
		
//		Anonymous Inner Class
//		Anonymous class can access to final or practically final outer
//		member variables.
//		Comparator<Product> comparator = new Comparator<Product>() {
//			@Override
//			public int compare(Product o1, Product o2) {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//		};
		
//		pm.printProducts(comparator);
		
//		Compare by stars
		pm.printProducts((p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
		
//		Compare by price
		pm.printProducts((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
		
//		Compare by starts (rating)
		Comparator<Product> ratingSorter = (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
		Comparator<Product> priceSorter = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
		
		pm.printProducts(ratingSorter.thenComparing(priceSorter));
		pm.printProducts(priceSorter.thenComparing(ratingSorter).reversed());
	}
}
