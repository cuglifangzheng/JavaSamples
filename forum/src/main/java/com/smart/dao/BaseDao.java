package com.smart.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseDao<T> {
    private Class<T> entityClass;
    private HibernateTemplate hibernateTemplate;

    @Autowired
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public BaseDao() {
        Type type = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType)type).getActualTypeArguments();
        entityClass = (Class)params[0];
    }

    public T load(Serializable id) {
        return (T)getHibernateTemplate().load(entityClass, id);
    }

    public T get(Serializable id) {
        return (T)getHibernateTemplate().get(entityClass, id);
    }

    public List<T> loadAll() {
        return getHibernateTemplate().loadAll(entityClass);
    }

    public void save(T entity) {
        getHibernateTemplate().save(entity);
    }

    public void remove(T entity) {
        getHibernateTemplate().delete(entity);
    }

    public void update(T entity) {
        getHibernateTemplate().update(entity);
    }

    public List find(String hql) {
        return getHibernateTemplate().find(hql);
    }

    public List find(String hql, Object... params) {
            return getHibernateTemplate().find(hql, params);
    }

    public void initialize(Object entity) {
        getHibernateTemplate().initialize(entity);
    }

    public Page pagedQuery(String hql, int pageNo, int pageSize, Object... values) {
        Assert.hasText(hql);
        Assert.isTrue(pageNo >= 1, "page should start from 1");
        String countQueryString = "select count(*) " + removeSelect(hql);
        List countList = getHibernateTemplate().find(countQueryString, values);
        long totalCount = (Long)countList.get(0);
        if (totalCount < 1)
            return new Page();

        int startIndex = Page.getStartOfPage(pageNo, pageSize);
        Query query = createQuery(hql, values);
        List list = query.setFirstResult(startIndex).setMaxResults(pageSize).list();
        return new Page(startIndex, totalCount, pageSize, list);
    }

    public Query createQuery(String hql, Object... values) {
        Assert.hasText(hql);
        Query query = getSession().createQuery(hql);
        for (int i = 0; i<values.length; i++) {
            query.setParameter(i, values[i]);
        }
        return query;
    }

    /**
     * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
     *
     * @see #pagedQuery(String,int,int,Object[])
     */
    private static String removeSelect(String hql) {
        Assert.hasText(hql);
        int beginPos = hql.toLowerCase().indexOf("from");
        Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
        return hql.substring(beginPos);
    }

    /**
     * 去除hql的orderby 子句，用于pagedQuery.
     *
     * @see #pagedQuery(String,int,int,Object[])
     */
    private static String removeOrders(String hql) {
        Assert.hasText(hql);
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public Session getSession() {
        return getHibernateTemplate().getSessionFactory().getCurrentSession();
    }
}
