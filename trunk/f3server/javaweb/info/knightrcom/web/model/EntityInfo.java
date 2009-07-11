/**
 * 
 */
package info.knightrcom.web.model;


import info.knightrcom.web.service.F3SWebServiceResult;

import java.util.List;

/**
 *
 */
public class EntityInfo<T> {

    /**
     * 
     */
    public EntityInfo() {
    }

    private T entity;

    private List<T> entityList;

    private Pagination pagination = new Pagination();

    private F3SWebServiceResult result;

    /**
     * @return the pagination
     */
    public Pagination getPagination() {
        return pagination;
    }

    /**
     * @param pagination the pagination to set
     */
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    /**
     * @return the entityList
     */
    public List<T> getEntityList() {
        return entityList;
    }

    /**
     * @param entityList the entityList to set
     */
    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }

    /**
     * @return the result
     */
    public F3SWebServiceResult getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(F3SWebServiceResult result) {
        this.result = result;
    }

    /**
     * @return the entity
     */
    public T getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(T entity) {
        this.entity = entity;
    }}
