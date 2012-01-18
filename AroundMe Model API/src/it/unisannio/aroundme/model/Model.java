/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.model;

import java.io.Serializable;

/**
 * Interfaccia implementata da tutti gli oggetti che dispongono di un {@link Serializer}.
 * 
 * <p>Per quanto l'interfaccia non definisca metodi o classi, essa viene utilizzata per marcare gli oggetti
 * che devono essere serializzati in XML per essere inviati su rete.</p>
 * 
 * <p>Per convenzione, tutti i modelli devono dichiarare un campo statico <code>SERIALIZER</code>, contenente
 * un'istanza del serializzatore in grado di persistere i propri dati.</p>
 * 
 * <p>Esempio:
 * <pre><code>
 * public class Foo implements Model {
 * 		public static final Serializer&lt;Foo&gt; SERIALIZER = new Serializer&lt;Foo&gt;() { 
 * 			// ... 
 *		}
 *
 *		// ...
 * }
 * </code></pre></p>
 * 
 * @see Serializer
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public interface Model extends Serializable { }
