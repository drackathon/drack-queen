package org.drack.hackathon.service;


import com.google.common.base.Preconditions;
import org.drack.hackathon.dto.Links;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Exceptional;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;

@RestController
@RequestMapping(value = {"/pets"})
public class PetsService {

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = {"/{pet}"}, method = RequestMethod.GET)
    public Links createPet(@PathVariable String pet,
                          final HttpServletRequest servletRequest,
                          final HttpServletResponse servletResponse) {
        throw new NotSupportedException(URI.create("/pets/error/not-supported"), "not supported");
    }


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
