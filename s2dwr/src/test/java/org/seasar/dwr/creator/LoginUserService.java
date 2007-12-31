package org.seasar.dwr.creator;

public interface LoginUserService {

    public abstract boolean isAuthenticated();

    public abstract String getUserId();

    public abstract String getUserName();

    public abstract String getEmail();

}