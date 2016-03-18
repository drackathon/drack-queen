package org.drack.hackathon.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.drack.hackathon.dao.PetDAO;
import org.drack.hackathon.dto.Links;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.ThrowableProblem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

@RestController
@RequestMapping(value = {"/pets"})
public class PetsService {

    private final ObjectMapper objectMapper;
    private final PetDAO petDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(PetsService.class);

    @Autowired
    public PetsService(final ObjectMapper objectMapper, final PetDAO petDAO) {
        this.objectMapper = checkNotNull(objectMapper, "ObjectMapper must not be null");
        this.petDAO = checkNotNull(petDAO, "Pet DAO must not be null");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = {"/{pet}"}, method = RequestMethod.PUT)
    public Links createPet(@PathVariable String pet,
                          final HttpServletRequest servletRequest,
                          final HttpServletResponse servletResponse) {
        throw new NotSupportedException(URI.create("/pets/error/not-supported"), "not supported");
    }


    @ExceptionHandler(NotSupportedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(NotSupportedException exception) throws JsonProcessingException {
        return objectMapper.writeValueAsString(exception);
    }


    /**
     * NOTE: Exception is registered in ObjectMapper with
     * mapper.registerSubtypes(PetsService.NotSupportedException.class);
     */
    public static final class NotSupportedException extends ThrowableProblem {
        private final URI type;
        private final String title;

        public NotSupportedException(final URI type, final String title) {
            this.type = checkNotNull(type, "type URI must not be null");
            this.title = checkNotNull(title, "title must not be null");
        }

        @Override
        public URI getType() {
            return type;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public Response.StatusType getStatus() {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }

}
