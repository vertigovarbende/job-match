package com.deveyk.jobmatch.job.application.port.in.command;

public record ArchiveJobCommand(
        Long id,
        String actorId
) {

}
