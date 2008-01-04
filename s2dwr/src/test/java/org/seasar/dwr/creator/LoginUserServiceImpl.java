/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dwr.creator;

public class LoginUserServiceImpl implements LoginUserService {
    private boolean authenticated;
    private String userId;
    private String userName;
    private String email;

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.dwr.creator.LoginUserService#isAuthenticated()
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.dwr.creator.LoginUserService#getUserId()
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.dwr.creator.LoginUserService#getUserName()
     */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.dwr.creator.LoginUserService#getEmail()
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
