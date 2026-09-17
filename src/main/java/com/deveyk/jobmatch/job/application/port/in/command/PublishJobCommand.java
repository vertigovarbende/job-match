package com.deveyk.jobmatch.job.application.port.in.command;

public record PublishJobCommand(
        Long id,
        String actorId
) {

}
