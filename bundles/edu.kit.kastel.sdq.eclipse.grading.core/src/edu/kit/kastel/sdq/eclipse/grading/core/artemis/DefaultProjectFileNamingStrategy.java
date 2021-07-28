package edu.kit.kastel.sdq.eclipse.grading.core.artemis;

import java.io.File;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;

public class DefaultProjectFileNamingStrategy implements IProjectFileNamingStrategy {

	@Override
	public File getAssignmentFileInProjectDirectory(File projectDirectory) {
		return new File(projectDirectory, "assignment");
	}

	@Override
	public File getProjectFileInWorkspace(File workspaceDirectory, IExercise exercise, ISubmission submission) {
		return new File(workspaceDirectory, new StringBuilder()
				.append("exercise-").append(exercise.getExerciseId()).append("-").append(exercise.getShortName())
				.append("_submission-").append(submission.getSubmissionId())
				.append("-").append(submission.getParticipantIdentifier())
				.toString());
	}

}
