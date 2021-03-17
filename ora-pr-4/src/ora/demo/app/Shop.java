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
import java.time.LocalDate;
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
		
		Product p1 = pm.createProduct(101, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
		pm.printProductReport(p1);
		
		p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Nice hot cup of tea");
		p1 = pm.reviewProduct(p1, Rating.TWO_STAR, "Rather weak tea");
		p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Fine tea");
		p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Good tea");
		p1 = pm.reviewProduct(p1, Rating.FIVE_STAR, "Perfect tea");
		p1 = pm.reviewProduct(p1, Rating.THREE_STAR, "Just add some lemon");
		
		pm.printProductReport(p1);
		
		Product p2 = pm.createProduct(102, "Coffe", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
		p2 = pm.reviewProduct(p2, Rating.THREE_STAR, "Coffee was ok");
		p2 = pm.reviewProduct(p2, Rating.ONE_STAR, "Where is the milk?!");
		p2 = pm.reviewProduct(p2, Rating.FIVE_STAR, "It's perfect with ten spoons of sugar");
		
		pm.printProductReport(p2);
		
		Product p3 = pm.createProduct(103, "Cake", BigDecimal.valueOf(3.99), Rating.NOT_RATED);
		p3 = pm.reviewProduct(p3, Rating.THREE_STAR, "Very nice cake");
		p3 = pm.reviewProduct(p3, Rating.FOUR_STAR, "It's good, but I've expected more chocolate");
		p3 = pm.reviewProduct(p3, Rating.FIVE_STAR, "This cake is perfect");
		
		pm.printProductReport(p3);
	}
}
