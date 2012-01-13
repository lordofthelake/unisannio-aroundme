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
