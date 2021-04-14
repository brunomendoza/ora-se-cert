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

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ora.demo.data.ProductManager;

/**
 * {@code Shop} class represents an application that manages Products.
 * @version 1.0
 * @author Bruno Mendoza
 */
public class Shop {
	
	public static void main(String[] args) {
		AtomicInteger clientCount = new AtomicInteger(0);
		
		Callable<String> client = () -> {
			ProductManager pm = ProductManager.getInstance();
			String clientId = "Client " + clientCount.incrementAndGet();
			String threadName = Thread.currentThread().getName();
			int productId = ThreadLocalRandom.current().nextInt(63);
			String languageTag = ProductManager.getSupportedLocales()
					.stream()
					.skip(ThreadLocalRandom.current().nextInt(4))
					.findFirst()
					.get();
			StringBuilder log = new StringBuilder();
			
			log.append(clientId + " " + threadName + "\n-\tstart of log\t-\n");
			log.append(pm.getDiscounts(languageTag)
					.entrySet()
					.stream()
					.map(entry -> entry.getKey() + "\t" + entry.getValue())
					.collect(Collectors.joining("\n")));
			log.append("\n-\tendof log\t-\n");
			return log.toString();
		};
		
		
		ProductManager pm = ProductManager.getInstance();
		pm.printProductReport(101, "en-GB");
		pm.printProductReport(103, "es-ES");
	}
}
