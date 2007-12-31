package org.seasar.dwr.creator;

public class EmployeeServiceImpl implements EmployeeService {

    public static final String loginUserDto_BINDING = "bindingType=must";

    private LoginUserService loginUserService;

    public void setLoginUserDto(LoginUserService loginUserService) {
        this.loginUserService = loginUserService;
    }

}
