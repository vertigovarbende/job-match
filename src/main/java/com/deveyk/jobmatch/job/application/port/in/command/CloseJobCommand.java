package com.deveyk.jobmatch.job.application.port.in.command;

public record CloseJobCommand(
        Long id,
        String actorId
) {

}
