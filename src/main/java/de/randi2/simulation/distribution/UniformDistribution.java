/* 
 * (c) 2008- RANDI2 Core Development Team
 * 
 * This file is part of RANDI2.
 * 
 * RANDI2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * RANDI2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RANDI2. If not, see <http://www.gnu.org/licenses/>.
 */
package de.randi2.simulation.distribution;

import java.io.Serializable;
import java.util.List;

/**
 * Represented the uniform distribution. The elements you get with the getNextValue() method are uniform distributed.
 * 
 * @author dschrimpf <ds@randi2.de>
 *
 * @param <E>
 */
public  class  UniformDistribution<E extends Serializable> extends AbstractDistribution<E> {

	

	public UniformDistribution(List<E> elements) {
		super(elements);
	}
	
	public UniformDistribution(List<E> elements, long seed) {
		super(elements, seed);
	}


	@Override
	public E getNextValue() {
		return elements.get(random.nextInt(elements.size()));
	}

	
	
}
