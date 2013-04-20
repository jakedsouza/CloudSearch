package com.cloudsearch.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import com.cloudsearch.dao.GenericDao;



public abstract class GenericDaoImpl<T> implements GenericDao<T> {
	protected static final String PERSISTENCE_UNIT_NAME = "Video";
	protected static EntityManagerFactory factory;
	  
	protected EntityManager em;

	private Class<T> type;

	@SuppressWarnings("rawtypes")
	public GenericDaoImpl() {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		 em = factory.createEntityManager();
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class) pt.getActualTypeArguments()[0];
	}

	
	
	@Override
	public long countAll(@SuppressWarnings("rawtypes") final Map params) {

		final StringBuffer queryString = new StringBuffer(
				"SELECT count(o) from ");

		queryString.append(type.getSimpleName()).append(" o ");
		queryString.append(this.getQueryClauses(params, null));

		final Query query = this.em.createQuery(queryString.toString());

		return (Long) query.getSingleResult();

	}

	@Override
	public T create(final T t) {
		this.em.getTransaction().begin();
		this.em.persist(t);
		this.em.getTransaction().commit();
		return t;
	}

	@Override
	public void delete(final Object id) {
		this.em.getTransaction().begin();
		this.em.remove(this.em.getReference(type, id));
		this.em.getTransaction().commit();
	}

	@Override
	public T find(final Object id) {
		return (T) this.em.find(type, id);
	}

	@Override
	public T update(final T t) {
		this.em.getTransaction().begin();
		T t2 = this.em.merge(t) ;
		this.em.getTransaction().commit();
		return t2;
		
	}

	private String getQueryClauses(final Map<String, Object> params,
			final Map<String, Object> orderParams) {
		final StringBuffer queryString = new StringBuffer();
		if ((params != null) && !params.isEmpty()) {
			queryString.append(" where ");
			for (final Iterator<Map.Entry<String, Object>> it = params
					.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<String, Object> entry = it.next();
				if (entry.getValue() instanceof Boolean) {
					queryString.append(entry.getKey()).append(" is ")
					.append(entry.getValue()).append(" ");
				} else {
					if (entry.getValue() instanceof Number) {
						queryString.append(entry.getKey()).append(" = ")
						.append(entry.getValue());
					} else {
						// string equality
						queryString.append(entry.getKey()).append(" = '")
						.append(entry.getValue()).append("'");
					}
				}
				if (it.hasNext()) {
					queryString.append(" and ");
				}
			}
		}
		if ((orderParams != null) && !orderParams.isEmpty()) {
			queryString.append(" order by ");
			for (final Iterator<Map.Entry<String, Object>> it = orderParams
					.entrySet().iterator(); it.hasNext();) {
				final Map.Entry<String, Object> entry = it.next();
				queryString.append(entry.getKey()).append(" ");
				if (entry.getValue() != null) {
					queryString.append(entry.getValue());
				}
				if (it.hasNext()) {
					queryString.append(", ");
				}
			}
		}
		return queryString.toString();
	}

}
