package edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping;

import java.io.Serializable;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;

public interface IExercise extends Serializable {

	int getExerciseId();

	boolean hasSecondCorrectionRound();

	double getMaxPoints();

	boolean isSecondCorrectionEnabled();

	String getShortName();

	String getTestRepositoryUrl();

	String getTitle();

	String getType();

	ICourse getCourse();

	ISubmission getSubmission(int id) throws ArtemisClientException;

}
