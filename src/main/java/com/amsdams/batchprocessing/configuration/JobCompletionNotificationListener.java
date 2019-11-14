package com.amsdams.batchprocessing.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.amsdams.batchprocessing.entity.Todo;

import lombok.extern.slf4j.Slf4j;

/**
 * Moves file after job execution.
 */
@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	@Value("${batchprocessing.output-dir}")
	private String outputDir;
	@Value("${batchprocessing.error-dir}")
	private String errorDir;

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		String fileName = jobExecution.getJobParameters().getString("input.file.name");
		Path sourcePath = Paths.get(fileName);
		Path errorPath = Paths.get(errorDir);
		Path succesPath = Paths.get(outputDir);

		ExitStatus exitStatus = jobExecution.getExitStatus();
		if (ExitStatus.FAILED.getExitCode().equals(exitStatus.getExitCode())) {
			try {
				Files.createDirectories(errorPath.resolve(sourcePath.getFileName()));
				Files.move(sourcePath, errorPath.resolve(sourcePath.getFileName()),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				log.error("Error during moving " + fileName + " to " + errorPath + " : " + e.getMessage());
			}
		} else if (ExitStatus.COMPLETED.getExitCode().equals(exitStatus.getExitCode())) {
			try {
				Files.createDirectories(succesPath.resolve(sourcePath.getFileName()));

				Files.move(sourcePath, succesPath.resolve(sourcePath.getFileName()),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				log.error("Error during moving " + fileName + " to " + succesPath + " : " + e.getMessage());
			}
		}

		jdbcTemplate
				.query("SELECT id, title, description, done FROM todo",
						(rs, row) -> new Todo(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getBoolean(4)))
				.forEach(todo -> log.info("Found <{}> in the database.", todo));

		log.debug("Job executed. EndTime: " + jobExecution.getEndTime() + " " + jobExecution.getExitStatus());
	}

}
