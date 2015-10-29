package com.aslan.contra.db;

public interface Service<T> {

	public abstract Iterable<T> findAll();

	public abstract T find(Long id);

	public abstract void delete(Long id);

	public abstract T createOrUpdate(T object);

}
