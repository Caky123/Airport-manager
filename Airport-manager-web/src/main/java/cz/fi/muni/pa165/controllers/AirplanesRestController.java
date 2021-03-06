package cz.fi.muni.pa165.controllers;

import cz.fi.muni.pa165.dto.AirplaneDTO;
import cz.fi.muni.pa165.exceptions.AirplaneDataAccessException;
import cz.fi.muni.pa165.exceptions.InvalidRequestException;
import cz.fi.muni.pa165.exceptions.ResourceNotFoundException;
import cz.fi.muni.pa165.facade.AirplaneFacade;
import cz.fi.muni.pa165.hateoas.AirplaneResource;
import cz.fi.muni.pa165.hateoas.AirplaneResourceAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@RestController
@RequestMapping("/airplanes")
public class AirplanesRestController {

    private AirplaneFacade airplaneFacade;
    private AirplaneResourceAssembler airplaneResourceAssembler;
    private final static Logger logger = LoggerFactory.getLogger(AirplanesRestController.class);


    public AirplanesRestController(
            @Autowired AirplaneFacade airplaneFacade,
            @Autowired AirplaneResourceAssembler airplaneResourceAssembler
    ) {

        this.airplaneFacade = airplaneFacade;
        this.airplaneResourceAssembler = airplaneResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.GET)
    public final HttpEntity<Resources<AirplaneResource>> getAirplanes() {
        List<AirplaneResource> resourcesCollection = airplaneResourceAssembler.toResources(airplaneFacade.findAll());
        Resources<AirplaneResource> airplaneResources = new Resources<>(resourcesCollection,
                linkTo(AirplanesRestController.class).withSelfRel(),
                linkTo(AirplanesRestController.class).slash("/create").withRel("create"));
        return new ResponseEntity<>(airplaneResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public final HttpEntity<AirplaneResource> getAirplane(@PathVariable("id") long id) throws Exception {
        AirplaneDTO airplaneDTO = airplaneFacade.findById(id);
        if (airplaneDTO == null) throw new ResourceNotFoundException("Airplane " + id + " not found.");
        AirplaneResource airplaneResource = airplaneResourceAssembler.toResource(airplaneDTO);
        return new ResponseEntity<>(airplaneResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public final void deleteAirplane(@PathVariable("id") long id) throws Exception {
        try {
            airplaneFacade.deleteAirplane(id);
        } catch (AirplaneDataAccessException e) {
            // TODO error handling
        }
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public final HttpEntity<AirplaneResource> createAirplane(@RequestBody @Valid AirplaneDTO airplaneDTO,
                                                             BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException("Failed validation");
        }

        Long id = airplaneFacade.addAirplane(airplaneDTO);
        AirplaneResource airplaneResource = airplaneResourceAssembler.toResource(airplaneFacade.findById(id));
        return new ResponseEntity<>(airplaneResource, HttpStatus.OK);
    }
}
