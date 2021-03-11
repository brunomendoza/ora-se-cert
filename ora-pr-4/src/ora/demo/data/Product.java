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

package ora.demo.data;

import static ora.demo.data.Rating.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * {@code Product} class represents properties and behaviours of
 * product objects in the Product Management System.
 * <br>
 * Each product has an id, name and price.
 * <br>
 * Each product can have a discount, calculated based on a
 * {@link DISCOUNT_RATE discount rate}.
 * @author Bruno Mendoza
 * @version 1.0
 */
public class Product {
	private int id;           // You can use 'final' for ummutable objects.
	private String name;      // You can use 'final' for ummutable objects.
	private BigDecimal price; // You can use 'final' for ummutable objects.
	private Rating rating;    // You can use 'final' for ummutable objects.
	
	/**
	 * A constant that defines a {@link java.math.BigDecimal BigDecimal} value
	 * of the discount rate.
	 * <br>
	 * Discount rate is 10%.
	 */
	public static final BigDecimal DISCOUNT_RATE=BigDecimal.valueOf(0.1);
	
	/**
	 * @param id
	 * @param name
	 * @param price
	 * @param rating
	 */
	public Product(int id, String name, BigDecimal price, Rating rating) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.rating = rating;
	}
	
	/**
	 * @param id
	 * @param name
	 * @param price
	 */
	public Product(int id, String name, BigDecimal price) {
		this(id, name, price, NOT_RATED);
	}
	
	public Product() {
		this(0, "no name", BigDecimal.ZERO);
	}

	public int getId() {
		return id;
	}
	
//	public void setId(int id) {
//		this.id = id;
//	}
	
	public String getName() {
		return name;
	}
	
//	public void setName(String name) {
//		this.name = name;
//	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
//	public void setPrice(BigDecimal price) {
//		this.price = price;
//	}
	
	public Rating getRating() {
		return rating;
	}
	
	/**
	 * Calculates discount based on a product price and
	 * {@link DISCOUNT_RATE discount rate}.
	 * @return a {@link java.math.BigDecimal BigDecimal}
	 * value of the discount.
	 */
	public BigDecimal getDiscount() {
		return price.multiply(DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
	}
	
	/**
	 * Create a copy from the current object with a new rating.
	 * @param newRating The new rating.
	 * @return A copy of the current {@code Product} with a new rating.
	 */
	public Product applyRating(Rating newRating) {
		return new Product(id, name, price, newRating);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
