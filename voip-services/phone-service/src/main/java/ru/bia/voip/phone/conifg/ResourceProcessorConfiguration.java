package ru.bia.voip.phone.conifg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import ru.bia.voip.phone.controller.ExtensionApiController;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Configuration
public class ResourceProcessorConfiguration {
/*    @Bean
    public RepresentationModelProcessor<EntityModel<AsteriskExtension>> asteriskExtensionProcessor(EntityLinks links) {
        return m -> m.add(WebMvcLinkBuilder
                .linkTo(
                        methodOn(ExtensionApiController.class)
                                .patchAsteriskExtension(m.getContent().getExten(), null))
                .withRel("patchExtension"));
    }*/

}


