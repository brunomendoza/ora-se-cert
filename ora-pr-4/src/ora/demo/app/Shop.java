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

import ora.demo.data.ProductManager;

/**
 * {@code Shop} class represents an application that manages Products.
 * @version 1.0
 * @author Bruno Mendoza
 */
public class Shop {
	
	public static void main(String[] args) {
		ProductManager pm = ProductManager.getInstance();
		pm.printProductReport(101, "en-GB");
		pm.printProductReport(103, "es-ES");
	}
}
