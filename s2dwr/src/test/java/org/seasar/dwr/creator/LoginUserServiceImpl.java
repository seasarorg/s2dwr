package org.seasar.dwr.creator;

public class LoginUserServiceImpl implements LoginUserService {
    private boolean authenticated;
    private String userId;
    private String userName;
    private String email;

    /* (non-Javadoc)
     * @see org.seasar.dwr.creator.LoginUserService#isAuthenticated()
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    /* (non-Javadoc)
     * @see org.seasar.dwr.creator.LoginUserService#getUserId()
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /* (non-Javadoc)
     * @see org.seasar.dwr.creator.LoginUserService#getUserName()
     */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /* (non-Javadoc)
     * @see org.seasar.dwr.creator.LoginUserService#getEmail()
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
