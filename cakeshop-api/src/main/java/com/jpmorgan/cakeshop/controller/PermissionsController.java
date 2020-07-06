package com.jpmorgan.cakeshop.controller;

import com.jpmorgan.cakeshop.error.APIException;
import com.jpmorgan.cakeshop.model.APIError;
import com.jpmorgan.cakeshop.model.APIResponse;
import com.jpmorgan.cakeshop.model.PermissionsDetails;
import com.jpmorgan.cakeshop.model.PermissionsOrgDetails;
import com.jpmorgan.cakeshop.model.json.PermissionsPostJsonRequest;
import com.jpmorgan.cakeshop.service.PermissionsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/permissions",
    method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionsController extends BaseController {

    @Autowired
    PermissionsService permissionsService;

    @RequestMapping("/getList")
    public ResponseEntity<APIResponse> getPermissionsDetails() throws APIException {

        PermissionsDetails details = permissionsService.get();

        APIResponse res = new APIResponse();

        if (details != null) {
            res.setData(details.toAPIData());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        APIError err = new APIError();
        err.setStatus("404");
        err.setTitle("Block not found");
        res.addError(err);
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", required = false, value = "get org details by id", dataType = "java.lang.String", paramType = "body")
    })
    @RequestMapping("/getDetails")
    public ResponseEntity<APIResponse> getPermissionsOrgDetails(@RequestBody PermissionsPostJsonRequest jsonRequest) throws APIException {

        PermissionsOrgDetails details = permissionsService.get(jsonRequest.getId());

        APIResponse res = new APIResponse();

        if (details != null) {
            res.setData(details.toAPIData());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }

        APIError err = new APIError();
        err.setStatus("404");
        err.setTitle("Block not found");
        res.addError(err);
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }
}
