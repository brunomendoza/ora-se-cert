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
import java.time.LocalDate;

public final class Food  extends Product {
	private LocalDate bestBefore;
	
	/**
	 * @param id
	 * @param name
	 * @param price
	 * @param rating
	 * @param bestBefore
	 */
	Food(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
		super(id, name, price, rating);
		this.bestBefore = bestBefore;
	}

	/**
	 * Get the value of the best before date for the product.
	 * @return the valuue of bestBefore.
	 */
	@Override
	public LocalDate getBestBefore() {
		return bestBefore;
	}

	@Override
	public String toString() {
		return super.toString() + " " + "Food [bestBefore=" + bestBefore + "]";
	}
	
	@Override
	public BigDecimal getDiscount() {
		return (bestBefore.isEqual(LocalDate.now()) ? super.getDiscount() : BigDecimal.ZERO);
	}
	
	@Override
	public Product applyRating(Rating newRating) {
		return new Food(getId(), getName(), getPrice(), newRating, bestBefore);
	}
}
