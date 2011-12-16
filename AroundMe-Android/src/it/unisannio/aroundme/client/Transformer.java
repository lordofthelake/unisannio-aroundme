package it.unisannio.aroundme.client;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 * @param <I>
 * @param <O>
 */
public interface Transformer<I, O> {
	O transform(I input) throws Exception;

}
