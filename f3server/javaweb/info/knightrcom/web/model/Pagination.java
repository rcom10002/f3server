package info.knightrcom.web.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 分页类
 */
public class Pagination {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private Log log = LogFactory.getLog(Pagination.class);

    private int totalRecord = -1;

    private int currentPage = -1;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int totalPage;

    /**
     * @return the totalRecord
     */
    public int getTotalRecord() {
        return totalRecord;
    }

    /**
     * @param totalRecord the totalRecord to set
     */
    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
        getTotalPage();
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        if (totalPage < 1) {
            log.warn("当前分页计划中的总页数尚未指定");
            this.currentPage = currentPage;
            return;
        }
        if (currentPage < 1) {
            this.currentPage = 1;
        } else if (currentPage > totalPage) {
            this.currentPage = totalPage;
        } else {
            this.currentPage = currentPage; 
        }
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return
     */
    public int getTotalPage() {
        if (totalRecord < 1) {
            totalPage = 0;
        } else if (totalRecord % getPageSize() == 0) {
            totalPage = totalRecord / getPageSize();
        } else {
            totalPage = totalRecord / getPageSize() + 1;
        }
        return totalPage;
    }

    /**
     * @return
     */
    public int getPreviousPage() {
        if (currentPage == 1 || currentPage == -1) {
            return -1;
        }
        return currentPage - 1;
    }

    /**
     * @return
     */
    public int getNextPage() {
        if (currentPage == totalRecord || currentPage == -1) {
            return -1;
        }
        return currentPage + 1;
    }
}
