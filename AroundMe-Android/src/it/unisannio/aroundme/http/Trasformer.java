package it.unisannio.aroundme.http;

public interface Trasformer<I, O> {
	O trasform(I input) throws Exception;

}
