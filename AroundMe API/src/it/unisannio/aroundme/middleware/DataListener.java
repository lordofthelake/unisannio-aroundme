package it.unisannio.aroundme.middleware;


public interface DataListener<T> {
	void onData(T object);
	void onError(Exception e);
}
