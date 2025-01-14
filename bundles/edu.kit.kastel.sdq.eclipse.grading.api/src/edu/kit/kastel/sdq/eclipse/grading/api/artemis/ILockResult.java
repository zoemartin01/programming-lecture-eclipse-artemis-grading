package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

import java.io.Serializable;
import java.util.List;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;

/**
 * This is gotten from acquiring a lock (no matter if the lock is already held
 * by the caller or not). It is used to calculate the assessment result.
 *
 */
public interface ILockResult extends Serializable {

	/**
	 *
	 * @return the participationID this submissionID belongs to (one participation
	 *         has one or many submissions).
	 */
	ParticipationDTO getParticipation();

	/**
	 *
	 * @return all {@link Feedback Feedbacks} that are saved in Artemis. This is
	 *         used to calculate the assessment result which is sent back to
	 *         Artemis.
	 */
	List<Feedback> getLatestFeedback();

	/**
	 *
	 * @return the submissionID this result belongs to (one participation has one or
	 *         many submissions).
	 */
	int getSubmissionId();
}
