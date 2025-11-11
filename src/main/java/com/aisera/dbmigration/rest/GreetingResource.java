package com.aisera.dbmigration.rest;

import com.aisera.dbmigration.service.DbMigrationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @Inject
    DbMigrationService dbMigrationService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        dbMigrationService.migrate();
        return "Hello from Quarkus REST";
    }
}
