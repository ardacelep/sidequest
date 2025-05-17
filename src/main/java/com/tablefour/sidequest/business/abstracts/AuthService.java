package com.tablefour.sidequest.business.abstracts;


import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.DataResult;
import com.tablefour.sidequest.core.results.Result;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.dtos.GetUserEmployeeDto;
import com.tablefour.sidequest.entities.dtos.GetUserEmployerDto;

public interface AuthService {

    Result register(User user);

    DataResult<String> login(User user);

    GetUserEmployeeDto registerUserEmployee(UserEmployee userEmployee);

    GetUserEmployerDto registerUserEmployer(UserEmployer userEmployer);

}
