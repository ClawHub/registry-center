package com.clawhub.registrycenter.core.bdb;

/**
 * <Description> 缓存类操作接口<br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018 -08-14 <br>
 */
public abstract class ICache {

    /**
     * The Closed.
     */
    private boolean closed = false;

    /**
     * Is closed boolean.
     *
     * @return the boolean
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets closed.
     *
     * @param closed the closed
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Close cache.
     */
    public abstract void closeCache();

}
