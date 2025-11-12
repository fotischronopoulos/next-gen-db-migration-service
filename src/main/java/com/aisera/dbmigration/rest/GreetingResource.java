package com.aisera.dbmigration.rest;

import com.aisera.dbmigration.service.DbMigrationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.quarkus.logging.Log;


@Path("/hello")
public class GreetingResource {

    @Inject
    DbMigrationService dbMigrationService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Log.info("TEST");
        dbMigrationService.migrate();
        return "Hello from Quarkus REST";
    }
}
